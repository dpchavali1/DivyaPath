package com.divyapath.app.audio;

import android.util.Log;

import androidx.annotation.OptIn;
import androidx.media3.common.audio.AudioProcessor;
import androidx.media3.common.util.UnstableApi;

/**
 * Loudness normalization utility for consistent volume across tracks.
 *
 * Uses a target loudness level and applies gain adjustment to prevent
 * volume jumps between different aarti/chalisa/mantra files from
 * different sources (local assets, archive.org streams).
 *
 * Implementation approach:
 * - ExoPlayer's built-in volume adjustment via setVolume()
 * - Tracks from archive.org tend to be louder than local raw assets
 * - We apply per-source gain factors to normalize perceived loudness
 */
public class LoudnessNormalizer {

    private static final String TAG = "LoudnessNormalizer";

    // Target gain factors per audio source type
    // These normalize perceived loudness relative to local assets (1.0 baseline)
    private static final float GAIN_LOCAL = 1.0f;          // Local raw assets (reference)
    private static final float GAIN_CACHED = 1.0f;         // Same as local since it's downloaded
    private static final float GAIN_ARCHIVE_ORG = 0.75f;   // Archive.org tends louder
    private static final float GAIN_ISKCON = 0.80f;        // ISKCON streams moderate
    private static final float GAIN_CLOUD_TTS = 0.95f;      // Cloud TTS (well-normalized)
    private static final float GAIN_TTS = 0.90f;           // TTS output

    // Night mode multiplier
    private static final float NIGHT_MULTIPLIER = 0.6f;

    private boolean enabled = true;
    private String currentPreset = "normal";

    public LoudnessNormalizer() {}

    /**
     * Get the recommended volume gain for a given audio source type.
     * @param sourceType the AudioSource.SourceType name (lowercase)
     * @return gain factor (0.0 to 1.0) to apply to the player
     */
    public float getGainForSource(String sourceType) {
        if (!enabled) return 1.0f;

        float baseGain;
        if (sourceType == null) {
            baseGain = 1.0f;
        } else {
            switch (sourceType.toLowerCase()) {
                case "local":       baseGain = GAIN_LOCAL; break;
                case "cached":      baseGain = GAIN_CACHED; break;
                case "archive_org": baseGain = GAIN_ARCHIVE_ORG; break;
                case "iskcon":      baseGain = GAIN_ISKCON; break;
                case "cloud_tts":   baseGain = GAIN_CLOUD_TTS; break;
                case "tts":         baseGain = GAIN_TTS; break;
                default:            baseGain = 1.0f; break;
            }
        }

        // Apply night mode reduction
        if ("night".equals(currentPreset)) {
            baseGain *= NIGHT_MULTIPLIER;
        }

        return Math.max(0.1f, Math.min(1.0f, baseGain));
    }

    /**
     * Get the gain for AudioSource.SourceType enum.
     */
    public float getGainForSource(AudioSource.SourceType sourceType) {
        if (sourceType == null) return 1.0f;
        return getGainForSource(sourceType.name().toLowerCase());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setPreset(String preset) {
        this.currentPreset = preset != null ? preset : "normal";
    }

    public String getPreset() {
        return currentPreset;
    }
}
