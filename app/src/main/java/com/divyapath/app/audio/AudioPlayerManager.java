package com.divyapath.app.audio;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Singleton audio player manager that connects to AudioPlaybackService via MediaController.
 * Persists across fragment navigation. Provides LiveData for UI observation.
 */
public class AudioPlayerManager {

    public enum PlayerState { IDLE, LOADING, PLAYING, PAUSED, ERROR, COMPLETED }

    private static volatile AudioPlayerManager INSTANCE;

    private final Context appContext;
    private ListenableFuture<MediaController> controllerFuture;
    private MediaController controller;

    // Playlist
    private final List<AudioTrack> queue = new ArrayList<>();
    private int currentIndex = -1;

    // LiveData for UI
    private final MutableLiveData<AudioTrack> currentTrackLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlayingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> progressLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<String> currentTimeLiveData = new MutableLiveData<>("0:00");
    private final MutableLiveData<String> totalTimeLiveData = new MutableLiveData<>("0:00");
    private final MutableLiveData<PlayerState> playerStateLiveData = new MutableLiveData<>(PlayerState.IDLE);

    // Progress updater
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private static final long PROGRESS_UPDATE_INTERVAL = 500L;

    // Sleep timer
    private final Handler sleepHandler = new Handler(Looper.getMainLooper());
    private long sleepTimerEndMs = 0;
    private boolean sleepAfterTrack = false;

    // Loudness normalization
    private final LoudnessNormalizer loudnessNormalizer = new LoudnessNormalizer();

    private AudioPlayerManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public static AudioPlayerManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AudioPlayerManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AudioPlayerManager(context);
                }
            }
        }
        return INSTANCE;
    }

    /** Connect to the AudioPlaybackService. Call from Activity.onStart(). */
    public void connect() {
        if (controller != null) return;
        SessionToken token = new SessionToken(appContext,
                new ComponentName(appContext, AudioPlaybackService.class));
        controllerFuture = new MediaController.Builder(appContext, token).buildAsync();
        controllerFuture.addListener(() -> {
            try {
                controller = controllerFuture.get();
                setupControllerListener();
            } catch (Exception e) {
                playerStateLiveData.postValue(PlayerState.ERROR);
            }
        }, MoreExecutors.directExecutor());
    }

    /** Disconnect from service. Call from Activity.onStop() if desired, or leave connected. */
    public void disconnect() {
        stopProgressUpdates();
        if (controllerFuture != null) {
            MediaController.releaseFuture(controllerFuture);
            controllerFuture = null;
        }
        controller = null;
    }

    private void setupControllerListener() {
        if (controller == null) return;
        controller.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                isPlayingLiveData.postValue(isPlaying);
                if (isPlaying) {
                    playerStateLiveData.postValue(PlayerState.PLAYING);
                    startProgressUpdates();
                } else {
                    stopProgressUpdates();
                    if (controller != null && controller.getPlaybackState() != Player.STATE_ENDED) {
                        playerStateLiveData.postValue(PlayerState.PAUSED);
                    }
                }
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case Player.STATE_BUFFERING:
                        playerStateLiveData.postValue(PlayerState.LOADING);
                        break;
                    case Player.STATE_READY:
                        updateTotalTime();
                        break;
                    case Player.STATE_ENDED:
                        playerStateLiveData.postValue(PlayerState.COMPLETED);
                        isPlayingLiveData.postValue(false);
                        stopProgressUpdates();
                        if (sleepAfterTrack) {
                            sleepAfterTrack = false;
                            stop();
                        }
                        break;
                    case Player.STATE_IDLE:
                        playerStateLiveData.postValue(PlayerState.IDLE);
                        break;
                }
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem item, int reason) {
                if (controller != null) {
                    int idx = controller.getCurrentMediaItemIndex();
                    if (idx >= 0 && idx < queue.size()) {
                        currentIndex = idx;
                        AudioTrack track = queue.get(idx);
                        currentTrackLiveData.postValue(track);
                        // Apply loudness normalization based on audio source
                        applyLoudnessNormalization(track);
                    }
                }
            }
        });

        // Sync initial state if already playing
        if (controller.isPlaying()) {
            isPlayingLiveData.postValue(true);
            playerStateLiveData.postValue(PlayerState.PLAYING);
            startProgressUpdates();
        }
    }

    // --- Playback Controls ---

    public void play(@NonNull AudioTrack track) {
        List<AudioTrack> single = new ArrayList<>();
        single.add(track);
        playPlaylist(single, 0);
    }

    public void playPlaylist(@NonNull List<AudioTrack> tracks, int startIndex) {
        if (tracks.isEmpty()) return;
        queue.clear();
        queue.addAll(tracks);
        currentIndex = Math.max(0, Math.min(startIndex, tracks.size() - 1));
        currentTrackLiveData.postValue(queue.get(currentIndex));

        ensureController(() -> {
            List<MediaItem> items = new ArrayList<>();
            for (AudioTrack t : tracks) {
                items.add(buildMediaItem(t));
            }
            controller.setMediaItems(items, currentIndex, 0);
            controller.prepare();
            controller.play();
        });
    }

    public void pause() {
        if (controller != null) controller.pause();
    }

    public void resume() {
        if (controller != null) controller.play();
    }

    public void togglePlayPause() {
        if (controller == null) return;
        if (controller.isPlaying()) {
            controller.pause();
        } else {
            controller.play();
        }
    }

    public void stop() {
        if (controller != null) {
            controller.stop();
            controller.clearMediaItems();
        }
        currentTrackLiveData.postValue(null);
        isPlayingLiveData.postValue(false);
        playerStateLiveData.postValue(PlayerState.IDLE);
        progressLiveData.postValue(0);
        currentTimeLiveData.postValue("0:00");
        totalTimeLiveData.postValue("0:00");
        queue.clear();
        currentIndex = -1;
    }

    public void seekTo(long positionMs) {
        if (controller != null) controller.seekTo(positionMs);
    }

    public void skipToNext() {
        if (controller != null && controller.hasNextMediaItem()) {
            controller.seekToNextMediaItem();
        }
    }

    public void skipToPrevious() {
        if (controller != null) {
            if (controller.getCurrentPosition() > 3000) {
                controller.seekTo(0); // restart current if > 3s in
            } else if (controller.hasPreviousMediaItem()) {
                controller.seekToPreviousMediaItem();
            }
        }
    }

    public void setRepeatMode(int mode) {
        if (controller != null) controller.setRepeatMode(mode);
    }

    public void setShuffleEnabled(boolean enabled) {
        if (controller != null) controller.setShuffleModeEnabled(enabled);
    }

    public void setSpeed(float speed) {
        if (controller != null) {
            controller.setPlaybackParameters(new PlaybackParameters(speed));
        }
    }

    // --- Sleep Timer ---

    public void setSleepTimer(long durationMs) {
        sleepAfterTrack = false;
        sleepTimerEndMs = System.currentTimeMillis() + durationMs;
        sleepHandler.removeCallbacksAndMessages(null);
        sleepHandler.postDelayed(this::stop, durationMs);
    }

    public void setSleepAfterTrack() {
        sleepAfterTrack = true;
        sleepHandler.removeCallbacksAndMessages(null);
    }

    public void cancelSleepTimer() {
        sleepAfterTrack = false;
        sleepTimerEndMs = 0;
        sleepHandler.removeCallbacksAndMessages(null);
    }

    // --- Getters ---

    public long getCurrentPosition() {
        return controller != null ? controller.getCurrentPosition() : 0;
    }

    public long getDuration() {
        return controller != null ? controller.getDuration() : 0;
    }

    public boolean isPlaying() {
        return controller != null && controller.isPlaying();
    }

    @Nullable
    public AudioTrack getCurrentTrack() {
        return currentTrackLiveData.getValue();
    }

    public List<AudioTrack> getQueue() { return new ArrayList<>(queue); }
    public int getCurrentIndex() { return currentIndex; }

    public float getPlaybackSpeed() {
        return controller != null ? controller.getPlaybackParameters().speed : 1.0f;
    }

    public int getRepeatMode() {
        return controller != null ? controller.getRepeatMode() : Player.REPEAT_MODE_OFF;
    }

    public boolean isShuffleEnabled() {
        return controller != null && controller.getShuffleModeEnabled();
    }

    public long getSleepTimerRemainingMs() {
        if (sleepTimerEndMs <= 0) return 0;
        long remaining = sleepTimerEndMs - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public boolean isSleepTimerActive() {
        return sleepAfterTrack || sleepTimerEndMs > System.currentTimeMillis();
    }

    // --- LiveData ---

    public LiveData<AudioTrack> getCurrentTrackLiveData() { return currentTrackLiveData; }
    public LiveData<Boolean> getIsPlayingLiveData() { return isPlayingLiveData; }
    public LiveData<Integer> getProgressLiveData() { return progressLiveData; }
    public LiveData<String> getCurrentTimeLiveData() { return currentTimeLiveData; }
    public LiveData<String> getTotalTimeLiveData() { return totalTimeLiveData; }
    public LiveData<PlayerState> getPlayerStateLiveData() { return playerStateLiveData; }

    // --- Internal Helpers ---

    private MediaItem buildMediaItem(AudioTrack track) {
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setTitle(track.getTitle())
                .setArtist(track.getSubtitle())
                .setAlbumTitle(track.getDeityName())
                .build();

        return new MediaItem.Builder()
                .setUri(track.getAudioUrl())
                .setMediaMetadata(metadata)
                .setMediaId(track.getId() != null ? track.getId() : String.valueOf(track.getContentId()))
                .build();
    }

    private void ensureController(Runnable action) {
        if (controller != null) {
            action.run();
        } else {
            connect();
            if (controllerFuture != null) {
                controllerFuture.addListener(() -> {
                    try {
                        controller = controllerFuture.get();
                        setupControllerListener();
                        action.run();
                    } catch (Exception ignored) {}
                }, MoreExecutors.directExecutor());
            }
        }
    }

    // --- Progress Polling ---

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (controller != null && controller.isPlaying()) {
                long pos = controller.getCurrentPosition();
                long dur = controller.getDuration();
                if (dur > 0) {
                    progressLiveData.postValue((int) (pos * 100 / dur));
                }
                currentTimeLiveData.postValue(formatTime(pos));
                totalTimeLiveData.postValue(formatTime(dur));
            }
            progressHandler.postDelayed(this, PROGRESS_UPDATE_INTERVAL);
        }
    };

    private void startProgressUpdates() {
        progressHandler.removeCallbacks(progressRunnable);
        progressHandler.post(progressRunnable);
    }

    private void stopProgressUpdates() {
        progressHandler.removeCallbacks(progressRunnable);
    }

    private void updateTotalTime() {
        if (controller != null) {
            totalTimeLiveData.postValue(formatTime(controller.getDuration()));
        }
    }

    private String formatTime(long ms) {
        if (ms <= 0) return "0:00";
        long totalSec = ms / 1000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        return String.format(Locale.getDefault(), "%d:%02d", min, sec);
    }

    // --- Loudness Normalization ---

    private void applyLoudnessNormalization(AudioTrack track) {
        if (controller != null && track != null) {
            float gain = loudnessNormalizer.getGainForSource(track.getAudioSourceType());
            controller.setVolume(gain);
        }
    }

    public LoudnessNormalizer getLoudnessNormalizer() {
        return loudnessNormalizer;
    }

    public void setLoudnessNormalizationEnabled(boolean enabled) {
        loudnessNormalizer.setEnabled(enabled);
        // Re-apply to current track
        AudioTrack current = getCurrentTrack();
        if (current != null) {
            applyLoudnessNormalization(current);
        }
    }
}
