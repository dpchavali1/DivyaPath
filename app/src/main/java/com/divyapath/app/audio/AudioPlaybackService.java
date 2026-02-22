package com.divyapath.app.audio;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import java.io.File;

import com.divyapath.app.MainActivity;

/**
 * Foreground service for audio playback using Media3 MediaSessionService.
 * Handles: background playback, lock screen controls, notification, audio focus,
 * headphone disconnect (becoming noisy).
 */
public class AudioPlaybackService extends MediaSessionService {

    private static final long CACHE_SIZE = 100 * 1024 * 1024; // 100MB

    private MediaSession mediaSession;
    private ExoPlayer player;
    private BroadcastReceiver noisyReceiver;
    private boolean noisyReceiverRegistered = false;
    private static SimpleCache simpleCache;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate() {
        super.onCreate();

        // Build ExoPlayer with audio focus handling
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build();

        // Initialize SimpleCache for audio caching (100MB)
        if (simpleCache == null) {
            File cacheDir = new File(getCacheDir(), "audio_cache");
            if (!cacheDir.exists()) cacheDir.mkdirs();
            simpleCache = new SimpleCache(cacheDir,
                    new LeastRecentlyUsedCacheEvictor(CACHE_SIZE),
                    new StandaloneDatabaseProvider(this));
        }

        // Build cache-aware data source
        DefaultDataSource.Factory upstreamFactory = new DefaultDataSource.Factory(this);
        CacheDataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory()
                .setCache(simpleCache)
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

        player = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(cacheDataSourceFactory))
                .setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true)
                .setHandleAudioBecomingNoisy(true)
                .build();

        // Enable gapless playback for seamless transitions between tracks
        player.setPauseAtEndOfMediaItems(false);

        // Activity intent for notification tap
        Intent launchIntent = new Intent(this, MainActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build MediaSession
        mediaSession = new MediaSession.Builder(this, player)
                .setSessionActivity(pendingIntent)
                .build();

        // Listen for playback state to auto-stop service when idle
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    registerNoisyReceiver();
                } else {
                    unregisterNoisyReceiver();
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_IDLE && !player.getPlayWhenReady()) {
                    stopSelf();
                }
            }
        });
    }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onDestroy() {
        if (mediaSession != null) {
            mediaSession.getPlayer().release();
            mediaSession.release();
            mediaSession = null;
        }
        unregisterNoisyReceiver();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Player p = mediaSession != null ? mediaSession.getPlayer() : null;
        if (p == null || !p.getPlayWhenReady() || p.getMediaItemCount() == 0) {
            stopSelf();
        }
    }

    // --- Headphone disconnect handling ---

    private void registerNoisyReceiver() {
        if (!noisyReceiverRegistered) {
            noisyReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                        if (player.isPlaying()) {
                            player.pause();
                        }
                    }
                }
            };
            IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(noisyReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(noisyReceiver, filter);
            }
            noisyReceiverRegistered = true;
        }
    }

    private void unregisterNoisyReceiver() {
        if (noisyReceiverRegistered && noisyReceiver != null) {
            try {
                unregisterReceiver(noisyReceiver);
            } catch (IllegalArgumentException ignored) {}
            noisyReceiverRegistered = false;
        }
    }
}
