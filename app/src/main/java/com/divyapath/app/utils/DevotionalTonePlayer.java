package com.divyapath.app.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.divyapath.app.R;

import java.util.Random;

/**
 * Advanced Devotional Tone Player with Humanization, Layering, and Temple Ambience.
 * Plays temple bell, chime, and Om sounds with reverb and natural variation.
 */
public class DevotionalTonePlayer {

    private final Context context;
    private MediaPlayer bellPlayer1;
    private MediaPlayer bellPlayer2;
    private MediaPlayer chimePlayer;
    private MediaPlayer omPlayer;
    private MediaPlayer dronePlayer;

    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final Handler handler;
    private Runnable loopRunnable;
    private boolean looping = false;
    private final Random random = new Random();

    public DevotionalTonePlayer(Context context) {
        this.context = context.getApplicationContext();
        handler = new Handler(Looper.getMainLooper());

        bellPlayer1 = createPlayer(R.raw.bell_tone);
        bellPlayer2 = createPlayer(R.raw.bell_tone);
        chimePlayer = createPlayer(R.raw.chime_tone);
        omPlayer = createPlayer(R.raw.om_tone);

        // Tanpura/Om Drone — loops continuously at low volume as foundation
        dronePlayer = createPlayer(R.raw.om_tone);
        if (dronePlayer != null) {
            dronePlayer.setLooping(true);
            dronePlayer.setVolume(0.12f, 0.12f);
        }
    }

    private MediaPlayer createPlayer(int resId) {
        try {
            MediaPlayer mp = MediaPlayer.create(context, resId);
            if (mp != null) {
                setupReverb(mp);
                // Auto-reset to beginning when playback completes so we can replay
                mp.setOnCompletionListener(p -> {
                    try { p.seekTo(0); } catch (Exception ignored) {}
                });
            }
            return mp;
        } catch (Exception e) {
            return null;
        }
    }

    private void setupReverb(MediaPlayer mp) {
        if (mp == null) return;
        try {
            EnvironmentalReverb reverb = new EnvironmentalReverb(0, mp.getAudioSessionId());
            reverb.setDecayTime(3000);           // Deep temple hall resonance
            reverb.setReverbLevel((short) -800);  // Prominent but not overwhelming
            reverb.setDiffusion((short) 1000);    // Spread the reflections
            reverb.setDensity((short) 1000);      // Rich dense reverb
            reverb.setEnabled(true);
            mp.attachAuxEffect(reverb.getId());
            mp.setAuxEffectSendLevel(0.8f);
        } catch (Exception e) {
            // EnvironmentalReverb not supported on all devices
        }
    }

    private void applyHumanization(MediaPlayer mp, float baseVolume) {
        if (mp == null) return;

        // Velocity variation: +/- 12%
        float variation = (random.nextFloat() * 0.24f) - 0.12f;
        float vol = Math.max(0.15f, Math.min(1.0f, baseVolume + variation));

        // Stereo panning: slight left/right shift (not extreme)
        float center = 0.5f;
        float pan = center + (random.nextFloat() * 0.4f - 0.2f); // 0.3 to 0.7
        mp.setVolume(vol * (1.0f - pan), vol * pan);

        // Pitch micro-variation (meend/natural drift)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                float pitch = 0.98f + (random.nextFloat() * 0.04f); // 0.98 to 1.02
                mp.setPlaybackParams(mp.getPlaybackParams().setPitch(pitch));
            } catch (Exception ignored) {}
        }
    }

    private void safePlay(MediaPlayer mp, float baseVolume) {
        if (mp == null) return;
        try {
            // Reset to beginning before playing
            mp.seekTo(0);
            applyHumanization(mp, baseVolume);
            mp.start();
        } catch (Exception e) {
            // Player may be in invalid state, try to recover
            try {
                mp.seekTo(0);
                mp.start();
            } catch (Exception ignored) {}
        }
    }

    /** Single bell strike */
    public void playBell() {
        playBellLayered();
    }

    /** Chime sound — used for mala bead counting */
    public void playChime() {
        safePlay(chimePlayer, 0.7f);
    }

    /** Om sound — used for mala completion */
    public void playOm() {
        safePlay(omPlayer, 0.85f);
    }

    /** Two layered bell strikes with slight delay for richness */
    public void playBellLayered() {
        if (bellPlayer1 == null) return;
        safePlay(bellPlayer1, 0.8f);

        // Ghost strike: softer second bell with human-like delay
        handler.postDelayed(() -> {
            if (bellPlayer2 != null) {
                safePlay(bellPlayer2, 0.35f);
            }
        }, 60 + random.nextInt(140)); // 60-200ms delay
    }

    /** Start continuous aarti bell loop with Om drone */
    public void startAartiLoop() {
        if (looping) return;
        looping = true;
        isPlaying.setValue(true);

        // Start Om drone as background
        if (dronePlayer != null) {
            try {
                dronePlayer.seekTo(0);
                dronePlayer.start();
            } catch (Exception ignored) {}
        }

        loopRunnable = new Runnable() {
            @Override
            public void run() {
                if (looping) {
                    playBellLayered();
                    // Humanized timing: theka variation like real aarti
                    long delay = 2500 + random.nextInt(800);
                    handler.postDelayed(this, delay);
                }
            }
        };
        handler.post(loopRunnable);
    }

    /** Stop all sounds and loops */
    public void stop() {
        looping = false;
        isPlaying.setValue(false);
        if (handler != null && loopRunnable != null) {
            handler.removeCallbacks(loopRunnable);
        }
        stopPlayer(dronePlayer);
        stopPlayer(bellPlayer1);
        stopPlayer(bellPlayer2);
    }

    private void stopPlayer(MediaPlayer mp) {
        if (mp == null) return;
        try {
            if (mp.isPlaying()) {
                mp.pause();
                mp.seekTo(0);
            }
        } catch (Exception ignored) {}
    }

    /** Toggle between playing and stopped for aarti loop */
    public void togglePlayPause(String type) {
        if (looping) stop();
        else startAartiLoop();
    }

    public LiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    /** Release all MediaPlayer resources */
    public void release() {
        stop();
        releasePlayer(bellPlayer1);
        releasePlayer(bellPlayer2);
        releasePlayer(chimePlayer);
        releasePlayer(omPlayer);
        releasePlayer(dronePlayer);
        bellPlayer1 = null;
        bellPlayer2 = null;
        chimePlayer = null;
        omPlayer = null;
        dronePlayer = null;
    }

    private void releasePlayer(MediaPlayer mp) {
        if (mp == null) return;
        try { mp.release(); } catch (Exception ignored) {}
    }
}
