package com.divyapath.app.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

import com.divyapath.app.audio.CloudTtsService;
import com.divyapath.app.audio.DivyaPathTTSEngine;
import com.divyapath.app.audio.SanskritPronunciationLexicon;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Audio player for devotional content with 3-tier playback:
 *
 *   1. STREAMING — Real human recordings from Archive.org (best quality)
 *   2. EDGE_TTS  — Microsoft Neural Voices via Edge TTS (natural AI voice)
 *   3. SYSTEM_TTS — Android built-in TTS (robotic fallback)
 *
 * Supports:
 *   - Time-based seekbar progress for streaming mode
 *   - Line-based seekbar progress for TTS modes
 *   - Playback speed selection (0.5x, 0.75x, 1.0x, 1.25x, 1.5x)
 *   - Seek-to for streaming mode
 */
public class TtsPlayerManager {

    private static final String TAG = "TtsPlayerManager";
    private static final long PROGRESS_UPDATE_INTERVAL = 500;

    private enum PlaybackMode { STREAMING, EDGE_TTS, SYSTEM_TTS }

    // Current playback mode
    private PlaybackMode mode = PlaybackMode.SYSTEM_TTS;

    // Streaming player (for Archive.org URLs)
    private MediaPlayer streamPlayer;
    private String audioUrl;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());

    // Edge TTS engine
    private DivyaPathTTSEngine edgeEngine;
    private boolean useEdgeEngine = false;
    private String contentType = null;

    // System TTS fallback
    private TextToSpeech tts;
    private boolean initialized = false;
    private boolean speaking = false;
    private String[] lines;
    private int currentLineIndex = 0;

    // Playback speed
    private float playbackSpeed = 1.0f;
    public static final float[] SPEED_OPTIONS = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f};

    // Devotional reading speed
    private static final float DEVOTIONAL_SPEECH_RATE = 0.72f;

    // Verse-aware pause durations (milliseconds)
    private static final long PAUSE_SECTION_HEADER = 1200;
    private static final long PAUSE_VERSE_END = 800;
    private static final long PAUSE_STANZA_BREAK = 600;
    private static final long PAUSE_NORMAL_LINE = 400;
    private static final long PAUSE_REFRAIN = 300;

    // LiveData for UI
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> currentLine = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isReady = new MutableLiveData<>(false);

    // Streaming progress (0-1000 range for smooth seekbar)
    private final MutableLiveData<Integer> streamProgress = new MutableLiveData<>(0);
    private final MutableLiveData<String> currentTimeText = new MutableLiveData<>("0:00");
    private final MutableLiveData<String> totalTimeText = new MutableLiveData<>("0:00");
    private final MutableLiveData<Integer> streamDurationMs = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isStreamingMode = new MutableLiveData<>(false);
    private final MutableLiveData<Float> speedLiveData = new MutableLiveData<>(1.0f);

    private final PreferenceManager preferenceManager;
    private final Context appContext;

    private OnLineSpokenListener lineListener;
    private String lastRefrain = null;

    public interface OnLineSpokenListener {
        void onLineStarted(int lineIndex);
        void onLineCompleted(int lineIndex);
        void onAllLinesCompleted();
    }

    public TtsPlayerManager(Context context) {
        appContext = context.getApplicationContext();
        preferenceManager = new PreferenceManager(context);

        // Try to set up Edge TTS engine
        try {
            edgeEngine = new DivyaPathTTSEngine(context);
            CloudTtsService cloudService = edgeEngine.getCloudTtsService();
            if (cloudService != null && cloudService.isAvailable()) {
                useEdgeEngine = true;
                Log.d(TAG, "Edge TTS (Natural Voice) enabled");
            }
        } catch (Exception e) {
            Log.w(TAG, "Edge TTS init failed, using system TTS", e);
            useEdgeEngine = false;
        }

        if (useEdgeEngine) {
            wireEdgeEngineLiveData();
        }

        // Always initialize system TTS as fallback
        tts = new TextToSpeech(appContext, status -> {
            if (status == TextToSpeech.SUCCESS) {
                setupVoice();
                initialized = true;
                isReady.postValue(true);

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        speaking = true;
                        isPlaying.postValue(true);
                        if (lineListener != null) {
                            lineListener.onLineStarted(currentLineIndex);
                        }
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (lineListener != null) {
                            lineListener.onLineCompleted(currentLineIndex);
                        }

                        int completedIndex = currentLineIndex;
                        currentLineIndex++;
                        currentLine.postValue(currentLineIndex);

                        if (currentLineIndex < lines.length && speaking) {
                            long pauseMs = computePauseDuration(completedIndex);
                            try { Thread.sleep(pauseMs); } catch (InterruptedException ignored) {}
                            speakCurrentLine();
                        } else {
                            speaking = false;
                            isPlaying.postValue(false);
                            currentLineIndex = 0;
                            currentLine.postValue(0);
                            if (lineListener != null) {
                                lineListener.onAllLinesCompleted();
                            }
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        speaking = false;
                        isPlaying.postValue(false);
                    }
                });
            } else if (!useEdgeEngine) {
                Log.e(TAG, "System TTS initialization failed and Edge TTS not available");
            }
        });
    }

    /**
     * Set the content type for SSML profile selection.
     */
    public void setContentType(@Nullable String contentType) {
        this.contentType = contentType;
        if (edgeEngine != null) {
            edgeEngine.setContentType(contentType);
        }
    }

    /**
     * Set a streaming audio URL (e.g. Archive.org MP3).
     * When set, play() will stream this URL instead of using TTS.
     */
    public void setAudioUrl(@Nullable String url) {
        this.audioUrl = url;
        isStreamingMode.postValue(!TextUtils.isEmpty(url));
    }

    // --- Playback Speed ---

    /**
     * Set playback speed. Affects streaming and system TTS modes.
     * @param speed e.g. 0.5f, 0.75f, 1.0f, 1.25f, 1.5f
     */
    public void setPlaybackSpeed(float speed) {
        this.playbackSpeed = speed;
        speedLiveData.postValue(speed);

        // Apply to active player
        if (mode == PlaybackMode.STREAMING && streamPlayer != null && streamPlayer.isPlaying()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    streamPlayer.setPlaybackParams(streamPlayer.getPlaybackParams().setSpeed(speed));
                } catch (Exception e) {
                    Log.w(TAG, "Failed to set stream speed", e);
                }
            }
        } else if (mode == PlaybackMode.SYSTEM_TTS && tts != null) {
            tts.setSpeechRate(DEVOTIONAL_SPEECH_RATE * speed);
        }
    }

    /**
     * Cycle to the next speed option. Returns the new speed.
     */
    public float cycleSpeed() {
        int currentIdx = 0;
        for (int i = 0; i < SPEED_OPTIONS.length; i++) {
            if (Math.abs(SPEED_OPTIONS[i] - playbackSpeed) < 0.01f) {
                currentIdx = i;
                break;
            }
        }
        int nextIdx = (currentIdx + 1) % SPEED_OPTIONS.length;
        setPlaybackSpeed(SPEED_OPTIONS[nextIdx]);
        return SPEED_OPTIONS[nextIdx];
    }

    public float getPlaybackSpeed() {
        return playbackSpeed;
    }

    public LiveData<Float> getSpeedLiveData() {
        return speedLiveData;
    }

    /**
     * Format speed for display (e.g. "1x", "1.5x", "0.75x").
     */
    public static String formatSpeed(float speed) {
        if (speed == (int) speed) {
            return (int) speed + "x";
        }
        return speed + "x";
    }

    private void wireEdgeEngineLiveData() {
        edgeEngine.getIsPlaying().observeForever(playing -> {
            if (mode == PlaybackMode.EDGE_TTS) isPlaying.postValue(playing);
        });
        edgeEngine.getCurrentLine().observeForever(line -> {
            if (mode == PlaybackMode.EDGE_TTS) currentLine.postValue(line);
        });
        edgeEngine.getIsReady().observeForever(ready -> {
            if (useEdgeEngine && ready) isReady.postValue(true);
        });
    }

    public void setLineListener(OnLineSpokenListener listener) {
        this.lineListener = listener;
        if (edgeEngine != null) {
            edgeEngine.setEventListener(new DivyaPathTTSEngine.OnTtsEventListener() {
                @Override
                public void onLineStarted(int lineIndex, String lineText) {
                    if (listener != null) listener.onLineStarted(lineIndex);
                }
                @Override
                public void onLineCompleted(int lineIndex) {
                    if (listener != null) listener.onLineCompleted(lineIndex);
                }
                @Override
                public void onCompleted() {
                    if (listener != null) listener.onAllLinesCompleted();
                }
                @Override
                public void onError(String message) {}
            });
        }
    }

    private void setupVoice() {
        Locale hindi = new Locale("hi", "IN");
        int result = tts.setLanguage(hindi);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.setLanguage(new Locale("hi"));
        }

        String preferredGender = preferenceManager.getVoiceGender();

        try {
            Set<Voice> voices = tts.getVoices();
            if (voices != null) {
                Voice bestVoice = null;
                int bestScore = -1;

                for (Voice voice : voices) {
                    String lang = voice.getLocale().getLanguage();
                    String country = voice.getLocale().getCountry();
                    String name = voice.getName().toLowerCase();
                    int quality = voice.getQuality();
                    boolean isNetworkRequired = voice.isNetworkConnectionRequired();

                    if (!lang.equals("hi")) continue;

                    int score = 0;
                    if ("IN".equals(country)) score += 10;
                    score += quality;
                    if (!isNetworkRequired) score += 50;

                    boolean isMaleVoice = name.contains("male") || name.contains("-m-") ||
                            name.contains("#male") || name.contains("_m_");
                    boolean isFemaleVoice = name.contains("female") || name.contains("-f-") ||
                            name.contains("#female") || name.contains("_f_");

                    if (preferredGender.equals("male") && isMaleVoice) score += 100;
                    else if (preferredGender.equals("female") && isFemaleVoice) score += 100;
                    else if (preferredGender.equals("female") && !isMaleVoice) score += 20;
                    else if (preferredGender.equals("male") && !isFemaleVoice) score += 20;

                    if (score > bestScore) {
                        bestScore = score;
                        bestVoice = voice;
                    }
                }

                if (bestVoice != null) {
                    tts.setVoice(bestVoice);
                    Log.d(TAG, "Selected voice: " + bestVoice.getName());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Voice selection failed", e);
        }

        tts.setSpeechRate(DEVOTIONAL_SPEECH_RATE * playbackSpeed);

        if (preferredGender.equals("male")) {
            tts.setPitch(0.85f);
        } else {
            tts.setPitch(1.0f);
        }
    }

    public void setText(String text) {
        if (text != null) {
            String[] rawLines = text.split("\n");
            List<String> filtered = new ArrayList<>();
            for (String line : rawLines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    filtered.add(trimmed);
                }
            }
            this.lines = filtered.toArray(new String[0]);
        }
        currentLineIndex = 0;
        lastRefrain = null;

        if (edgeEngine != null) {
            edgeEngine.setText(text);
            if (contentType != null) {
                edgeEngine.setContentType(contentType);
            }
        }
    }

    public void play() {
        // Tier 1: Stream from URL (real human recording)
        if (!TextUtils.isEmpty(audioUrl)) {
            playStream();
            return;
        }

        if (lines == null || lines.length == 0) return;

        // Tier 2: Edge TTS
        if (useEdgeEngine && edgeEngine != null) {
            mode = PlaybackMode.EDGE_TTS;
            edgeEngine.play();
            speaking = true;
            return;
        }

        // Tier 3: System TTS
        if (!initialized) return;
        mode = PlaybackMode.SYSTEM_TTS;
        setupVoice();
        speaking = true;
        speakCurrentLine();
    }

    public void stop() {
        speaking = false;
        stopProgressUpdates();
        stopStream();

        if (edgeEngine != null) {
            edgeEngine.stop();
        }
        if (tts != null) {
            tts.stop();
        }
        isPlaying.postValue(false);
        streamProgress.postValue(0);
        currentTimeText.postValue("0:00");
    }

    public void togglePlayPause() {
        // If streaming
        if (mode == PlaybackMode.STREAMING && streamPlayer != null) {
            try {
                if (streamPlayer.isPlaying()) {
                    streamPlayer.pause();
                    speaking = false;
                    isPlaying.postValue(false);
                    stopProgressUpdates();
                } else {
                    streamPlayer.start();
                    speaking = true;
                    isPlaying.postValue(true);
                    startProgressUpdates();
                }
            } catch (Exception e) {
                // Player in bad state, restart
                play();
            }
            return;
        }

        // If Edge TTS
        if (mode == PlaybackMode.EDGE_TTS && useEdgeEngine && edgeEngine != null) {
            edgeEngine.togglePlayPause();
            speaking = edgeEngine.isSpeaking();
            return;
        }

        // System TTS or initial play
        if (speaking) {
            stop();
        } else {
            play();
        }
    }

    /**
     * Seek to a position in streaming mode. Position is 0-1000 range.
     */
    public void seekTo(int progress1000) {
        if (mode == PlaybackMode.STREAMING && streamPlayer != null) {
            try {
                int duration = streamPlayer.getDuration();
                if (duration > 0) {
                    int positionMs = (int) ((long) progress1000 * duration / 1000);
                    streamPlayer.seekTo(positionMs);
                    currentTimeText.postValue(formatTime(positionMs));
                }
            } catch (Exception ignored) {}
        }
    }

    // --- Streaming Playback ---

    private void playStream() {
        stopStream();
        stopProgressUpdates();
        mode = PlaybackMode.STREAMING;
        speaking = true;
        isPlaying.postValue(true);
        streamProgress.postValue(0);

        try {
            streamPlayer = new MediaPlayer();
            streamPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            streamPlayer.setDataSource(audioUrl);
            streamPlayer.setOnPreparedListener(mp -> {
                if (speaking) {
                    // Apply playback speed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && playbackSpeed != 1.0f) {
                        try {
                            mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(playbackSpeed));
                        } catch (Exception ignored) {}
                    }
                    mp.start();
                    isPlaying.postValue(true);
                    int durationMs = mp.getDuration();
                    streamDurationMs.postValue(durationMs);
                    totalTimeText.postValue(formatTime(durationMs));
                    startProgressUpdates();
                    Log.d(TAG, "Streaming from: " + audioUrl);
                }
            });
            streamPlayer.setOnCompletionListener(mp -> {
                speaking = false;
                isPlaying.postValue(false);
                stopProgressUpdates();
                streamProgress.postValue(1000);
                if (lineListener != null) lineListener.onAllLinesCompleted();
            });
            streamPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Stream error: " + what + "/" + extra + ", falling back to TTS");
                speaking = false;
                isPlaying.postValue(false);
                stopStream();
                stopProgressUpdates();
                // Fall back to TTS
                audioUrl = null;
                isStreamingMode.postValue(false);
                play();
                return true;
            });
            streamPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Failed to start stream", e);
            stopStream();
            audioUrl = null;
            isStreamingMode.postValue(false);
            play();
        }
    }

    private void stopStream() {
        try {
            if (streamPlayer != null) {
                if (streamPlayer.isPlaying()) streamPlayer.stop();
                streamPlayer.release();
                streamPlayer = null;
            }
        } catch (Exception ignored) {}
    }

    // --- Progress Tracking ---

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (streamPlayer != null && speaking) {
                try {
                    if (streamPlayer.isPlaying()) {
                        int pos = streamPlayer.getCurrentPosition();
                        int dur = streamPlayer.getDuration();
                        if (dur > 0) {
                            streamProgress.postValue((int) ((long) pos * 1000 / dur));
                            currentTimeText.postValue(formatTime(pos));
                            totalTimeText.postValue(formatTime(dur));
                        }
                    }
                } catch (Exception ignored) {}
                progressHandler.postDelayed(this, PROGRESS_UPDATE_INTERVAL);
            }
        }
    };

    private void startProgressUpdates() {
        progressHandler.removeCallbacks(progressRunnable);
        progressHandler.post(progressRunnable);
    }

    private void stopProgressUpdates() {
        progressHandler.removeCallbacks(progressRunnable);
    }

    private String formatTime(long ms) {
        if (ms <= 0) return "0:00";
        long totalSec = ms / 1000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        return String.format(Locale.getDefault(), "%d:%02d", min, sec);
    }

    // --- Verse-aware pacing (system TTS fallback) ---

    private long computePauseDuration(int completedLineIndex) {
        if (lines == null || completedLineIndex < 0 || completedLineIndex >= lines.length) {
            return PAUSE_NORMAL_LINE;
        }

        String completedLine = lines[completedLineIndex];
        String nextLine = (completedLineIndex + 1 < lines.length) ? lines[completedLineIndex + 1] : null;

        if (nextLine != null && isSectionHeader(nextLine)) return PAUSE_SECTION_HEADER;
        if (isSectionHeader(completedLine)) return PAUSE_SECTION_HEADER;
        if (isVerseEndMarker(completedLine)) return PAUSE_VERSE_END;
        if (isRefrain(completedLine)) return PAUSE_REFRAIN;
        if (completedLine.endsWith("॥") || completedLine.endsWith("।।")) return PAUSE_STANZA_BREAK;

        return PAUSE_NORMAL_LINE;
    }

    private boolean isSectionHeader(String line) {
        String stripped = line.replaceAll("[॥।\\|\\s]", "").trim();
        return stripped.equalsIgnoreCase("दोहा") || stripped.equalsIgnoreCase("चौपाई") ||
               stripped.equalsIgnoreCase("सोरठा") || stripped.equalsIgnoreCase("छन्द") ||
               stripped.equalsIgnoreCase("अर्धाली") || stripped.equalsIgnoreCase("Doha") ||
               stripped.equalsIgnoreCase("Chaupai") || stripped.equalsIgnoreCase("Sortha");
    }

    private boolean isVerseEndMarker(String line) {
        return line.matches(".*॥\\s*\\d+\\s*॥\\s*$") ||
               line.matches(".*\\|\\|\\s*\\d+\\s*\\|\\|\\s*$") ||
               line.matches("^[॥।\\|\\s\\d०-९]+$");
    }

    private boolean isRefrain(String line) {
        if (lastRefrain != null && line.trim().equals(lastRefrain.trim())) return true;
        if (lines != null && currentLineIndex > 3) {
            for (int i = 0; i < Math.min(3, lines.length); i++) {
                if (lines[i].trim().equals(line.trim())) {
                    lastRefrain = line;
                    return true;
                }
            }
        }
        return false;
    }

    private void speakCurrentLine() {
        if (!initialized || !speaking || lines == null || currentLineIndex >= lines.length) return;

        String line = lines[currentLineIndex];

        if (line.length() < 3 || line.matches("^[॥।\\|\\s\\d०-९]+$")) {
            currentLineIndex++;
            currentLine.postValue(currentLineIndex);
            if (currentLineIndex < lines.length) {
                try { Thread.sleep(PAUSE_VERSE_END); } catch (InterruptedException ignored) {}
                speakCurrentLine();
            } else {
                speaking = false;
                isPlaying.postValue(false);
                currentLineIndex = 0;
                currentLine.postValue(0);
                if (lineListener != null) lineListener.onAllLinesCompleted();
            }
            return;
        }

        String processedLine = SanskritPronunciationLexicon.process(line);
        processedLine = processedLine.replaceAll("।।", "। ");

        Bundle params = new Bundle();
        tts.speak(processedLine, TextToSpeech.QUEUE_FLUSH, params, "line_" + currentLineIndex);
    }

    // --- LiveData Getters ---

    public LiveData<Boolean> getIsPlaying() { return isPlaying; }
    public LiveData<Integer> getCurrentLine() { return currentLine; }
    public int getTotalLines() { return lines != null ? lines.length : 0; }

    /** Streaming progress 0-1000 (for seekbar). Only updates in streaming mode. */
    public LiveData<Integer> getStreamProgress() { return streamProgress; }
    /** Current time formatted as "M:SS". Only updates in streaming mode. */
    public LiveData<String> getCurrentTimeText() { return currentTimeText; }
    /** Total duration formatted as "M:SS". Only updates in streaming mode. */
    public LiveData<String> getTotalTimeText() { return totalTimeText; }
    /** Whether the player is in streaming mode (real audio vs TTS). */
    public LiveData<Boolean> getIsStreamingMode() { return isStreamingMode; }

    public void release() {
        stop();
        stopStream();
        stopProgressUpdates();
        if (edgeEngine != null) {
            edgeEngine.release();
            edgeEngine = null;
        }
        if (tts != null) {
            tts.shutdown();
            tts = null;
        }
    }
}
