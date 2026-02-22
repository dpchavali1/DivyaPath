package com.divyapath.app.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.divyapath.app.R;

import java.util.Calendar;

/**
 * Adaptive devotional soundscapes that create immersive audio environments.
 *
 * Soundscape types:
 *   - "temple"     : Temple bell chimes at intervals + om drone
 *   - "nature"     : Gentle ambient (using om_tone as base)
 *   - "om_drone"   : Continuous Om meditation drone
 *   - "none"       : No ambient sounds
 *
 * Time-of-day awareness:
 *   - Morning (4-8 AM): Conch + bell emphasis, brighter
 *   - Daytime (8-5 PM): Standard temple ambience
 *   - Evening (5-9 PM): Deeper tones, aarti-time feeling
 *   - Night (9 PM-4 AM): Very soft, meditation-friendly
 *
 * Integrates with the audio playback system as a background layer.
 */
public class DevotionalSoundscape {

    private static final String TAG = "DevotionalSoundscape";

    public enum SoundscapeType {
        NONE, TEMPLE, NATURE, OM_DRONE
    }

    public enum TimeOfDay {
        MORNING, DAYTIME, EVENING, NIGHT
    }

    private final Context appContext;
    private MediaPlayer dronePlayer;
    private MediaPlayer bellPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Handler bellHandler = new Handler(Looper.getMainLooper());

    private SoundscapeType currentType = SoundscapeType.NONE;
    private boolean isActive = false;
    private float masterVolume = 0.10f; // Very subtle default

    // Bell interval scheduling
    private static final long BELL_INTERVAL_MIN_MS = 30_000;  // Min 30 seconds between bells
    private static final long BELL_INTERVAL_MAX_MS = 90_000;  // Max 90 seconds

    public DevotionalSoundscape(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
    }

    /**
     * Start the soundscape with the given type.
     */
    public void start(SoundscapeType type) {
        stop(); // Clean up previous
        this.currentType = type;

        if (type == SoundscapeType.NONE) return;

        isActive = true;
        TimeOfDay tod = getCurrentTimeOfDay();
        float adjustedVolume = getVolumeForTimeOfDay(tod);

        switch (type) {
            case TEMPLE:
                startDrone(adjustedVolume);
                scheduleBellChimes(tod);
                break;
            case NATURE:
                startDrone(adjustedVolume * 0.8f); // Softer for nature
                break;
            case OM_DRONE:
                startDrone(adjustedVolume * 1.2f); // Slightly louder for meditation
                break;
        }
    }

    /**
     * Start the soundscape using the string name (from preferences).
     */
    public void start(String typeName) {
        SoundscapeType type;
        try {
            type = SoundscapeType.valueOf(typeName.toUpperCase());
        } catch (Exception e) {
            type = SoundscapeType.NONE;
        }
        start(type);
    }

    /**
     * Stop all soundscape audio.
     */
    public void stop() {
        isActive = false;
        bellHandler.removeCallbacksAndMessages(null);

        if (dronePlayer != null) {
            try {
                if (dronePlayer.isPlaying()) dronePlayer.stop();
                dronePlayer.release();
            } catch (Exception ignored) {}
            dronePlayer = null;
        }

        if (bellPlayer != null) {
            try { bellPlayer.release(); } catch (Exception ignored) {}
            bellPlayer = null;
        }
    }

    /**
     * Temporarily duck the soundscape (when main audio/TTS is playing).
     */
    public void duck() {
        if (dronePlayer != null && dronePlayer.isPlaying()) {
            dronePlayer.setVolume(masterVolume * 0.2f, masterVolume * 0.2f);
        }
    }

    /**
     * Restore soundscape volume after ducking.
     */
    public void unduck() {
        if (dronePlayer != null && dronePlayer.isPlaying()) {
            float vol = getVolumeForTimeOfDay(getCurrentTimeOfDay());
            dronePlayer.setVolume(vol, vol);
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0f, Math.min(0.3f, volume));
        if (dronePlayer != null && dronePlayer.isPlaying()) {
            dronePlayer.setVolume(masterVolume, masterVolume);
        }
    }

    public boolean isActive() { return isActive; }
    public SoundscapeType getCurrentType() { return currentType; }

    // --- Internal ---

    private void startDrone(float volume) {
        try {
            dronePlayer = MediaPlayer.create(appContext, R.raw.om_tone);
            if (dronePlayer == null) return;
            dronePlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            dronePlayer.setLooping(true);
            dronePlayer.setVolume(volume, volume);
            dronePlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "Drone start error", e);
        }
    }

    private void scheduleBellChimes(TimeOfDay tod) {
        if (!isActive || currentType != SoundscapeType.TEMPLE) return;

        // Determine interval based on time of day
        long interval;
        float bellVolume;
        switch (tod) {
            case MORNING:
                interval = BELL_INTERVAL_MIN_MS; // More frequent in morning
                bellVolume = 0.3f;
                break;
            case EVENING:
                interval = BELL_INTERVAL_MIN_MS + 15_000;
                bellVolume = 0.25f;
                break;
            case NIGHT:
                interval = BELL_INTERVAL_MAX_MS; // Sparse at night
                bellVolume = 0.1f;
                break;
            default:
                interval = 60_000;
                bellVolume = 0.2f;
                break;
        }

        bellHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isActive) return;
                playBell(bellVolume);
                bellHandler.postDelayed(this, interval);
            }
        }, interval);
    }

    private void playBell(float volume) {
        try {
            if (bellPlayer != null) bellPlayer.release();
            bellPlayer = MediaPlayer.create(appContext, R.raw.bell_tone);
            if (bellPlayer != null) {
                bellPlayer.setVolume(volume, volume);
                bellPlayer.setOnCompletionListener(MediaPlayer::release);
                bellPlayer.start();
                bellPlayer = null; // Will be released on completion
            }
        } catch (Exception e) {
            Log.e(TAG, "Bell play error", e);
        }
    }

    public static TimeOfDay getCurrentTimeOfDay() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 4 && hour < 8) return TimeOfDay.MORNING;
        if (hour >= 8 && hour < 17) return TimeOfDay.DAYTIME;
        if (hour >= 17 && hour < 21) return TimeOfDay.EVENING;
        return TimeOfDay.NIGHT;
    }

    private float getVolumeForTimeOfDay(TimeOfDay tod) {
        switch (tod) {
            case MORNING: return masterVolume * 1.0f;
            case DAYTIME: return masterVolume * 0.9f;
            case EVENING: return masterVolume * 0.85f;
            case NIGHT:   return masterVolume * 0.5f;
            default:      return masterVolume;
        }
    }
}
