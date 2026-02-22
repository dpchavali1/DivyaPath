package com.divyapath.app.audio;

import android.content.Context;

import androidx.annotation.NonNull;

import com.divyapath.app.utils.PreferenceManager;

/**
 * Personalized audio profiles per content type.
 *
 * Profiles define playback characteristics:
 *   - "default"     : Standard settings
 *   - "festive"     : Louder, brighter, faster — for aarti during celebrations
 *   - "meditative"  : Slow, soft, deep — for chalisa and mantra jaap
 *   - "chanting"    : Moderate, rhythm-focused — for repetitive mantra
 *
 * Each profile adjusts: playback speed, volume, TTS rate, pitch, bg music enabled.
 */
public class AudioProfileManager {

    public static class AudioProfile {
        public final String name;
        public final float playbackSpeed;       // ExoPlayer speed (0.5–2.0)
        public final float volumeMultiplier;    // Volume gain (0.5–1.5)
        public final float ttsRate;             // TTS speech rate
        public final float ttsPitch;            // TTS pitch adjustment
        public final boolean bgMusicEnabled;    // Background music on/off
        public final String soundscapeType;     // "none", "temple", "om_drone", etc.
        public final float cloudTtsRate;        // Cloud TTS rate percentage (e.g. 85 = 85%)
        public final String cloudTtsPitch;      // Cloud TTS pitch in semitones (e.g. "-1st")

        public AudioProfile(String name, float playbackSpeed, float volumeMultiplier,
                          float ttsRate, float ttsPitch, boolean bgMusicEnabled,
                          String soundscapeType) {
            this(name, playbackSpeed, volumeMultiplier, ttsRate, ttsPitch,
                    bgMusicEnabled, soundscapeType, 85f, "-1st");
        }

        public AudioProfile(String name, float playbackSpeed, float volumeMultiplier,
                          float ttsRate, float ttsPitch, boolean bgMusicEnabled,
                          String soundscapeType, float cloudTtsRate, String cloudTtsPitch) {
            this.name = name;
            this.playbackSpeed = playbackSpeed;
            this.volumeMultiplier = volumeMultiplier;
            this.ttsRate = ttsRate;
            this.ttsPitch = ttsPitch;
            this.bgMusicEnabled = bgMusicEnabled;
            this.soundscapeType = soundscapeType;
            this.cloudTtsRate = cloudTtsRate;
            this.cloudTtsPitch = cloudTtsPitch;
        }
    }

    // Predefined profiles (with Cloud TTS rate/pitch)
    public static final AudioProfile PROFILE_DEFAULT = new AudioProfile(
            "default", 1.0f, 1.0f, 0.72f, 1.0f, false, "none", 85f, "-1st");

    public static final AudioProfile PROFILE_FESTIVE = new AudioProfile(
            "festive", 1.05f, 1.2f, 0.80f, 1.05f, false, "temple", 95f, "0");

    public static final AudioProfile PROFILE_MEDITATIVE = new AudioProfile(
            "meditative", 0.85f, 0.8f, 0.65f, 0.90f, true, "om_drone", 75f, "-2st");

    public static final AudioProfile PROFILE_CHANTING = new AudioProfile(
            "chanting", 0.95f, 1.0f, 0.75f, 0.95f, true, "none", 80f, "-1st");

    private final PreferenceManager preferences;

    public AudioProfileManager(@NonNull Context context) {
        this.preferences = new PreferenceManager(context);
    }

    /**
     * Get the audio profile for a given content type.
     */
    public AudioProfile getProfileForContentType(String contentType) {
        String profileName = preferences.getAudioProfile(contentType);
        return getProfileByName(profileName);
    }

    /**
     * Get a profile by its name.
     */
    public static AudioProfile getProfileByName(String name) {
        if (name == null) return PROFILE_DEFAULT;
        switch (name) {
            case "festive":    return PROFILE_FESTIVE;
            case "meditative": return PROFILE_MEDITATIVE;
            case "chanting":   return PROFILE_CHANTING;
            default:           return PROFILE_DEFAULT;
        }
    }

    /**
     * Get the recommended profile for a content type based on time of day.
     */
    public AudioProfile getSmartProfile(String contentType) {
        // Start with user preference
        AudioProfile baseProfile = getProfileForContentType(contentType);
        if (!baseProfile.name.equals("default")) return baseProfile;

        // Smart defaults based on content type and time
        DevotionalSoundscape.TimeOfDay tod = DevotionalSoundscape.getCurrentTimeOfDay();

        if ("aarti".equals(contentType)) {
            // Aarti: festive in morning/evening, default otherwise
            if (tod == DevotionalSoundscape.TimeOfDay.MORNING || tod == DevotionalSoundscape.TimeOfDay.EVENING) {
                return PROFILE_FESTIVE;
            }
        } else if ("mantra".equals(contentType)) {
            // Mantra: always chanting profile
            return PROFILE_CHANTING;
        } else if ("chalisa".equals(contentType)) {
            // Chalisa: meditative at night, default otherwise
            if (tod == DevotionalSoundscape.TimeOfDay.NIGHT) {
                return PROFILE_MEDITATIVE;
            }
        }

        return PROFILE_DEFAULT;
    }

    /**
     * Save the user's profile choice for a content type.
     */
    public void setProfileForContentType(String contentType, String profileName) {
        preferences.setAudioProfile(contentType, profileName);
    }

    /**
     * Apply a profile to the AudioPlayerManager.
     */
    public void applyToPlayer(@NonNull AudioPlayerManager player, @NonNull AudioProfile profile) {
        player.setSpeed(profile.playbackSpeed);
        player.getLoudnessNormalizer().setEnabled(true);
    }

    /**
     * Apply a profile to the DivyaPathTTSEngine.
     */
    public void applyToTtsEngine(@NonNull DivyaPathTTSEngine engine, @NonNull AudioProfile profile) {
        engine.setBgMusicEnabled(profile.bgMusicEnabled);
        // TTS rate and pitch are applied via the engine's preset system
    }

    /**
     * Get all available profile names.
     */
    public static String[] getProfileNames() {
        return new String[]{"default", "festive", "meditative", "chanting"};
    }

    /**
     * Get display-friendly labels for profiles.
     */
    public static String[] getProfileLabels() {
        return new String[]{"Default", "Festive", "Meditative", "Chanting"};
    }
}
