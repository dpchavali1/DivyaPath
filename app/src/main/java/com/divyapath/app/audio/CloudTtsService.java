package com.divyapath.app.audio;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.divyapath.app.utils.PreferenceManager;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Service layer for Edge TTS (Microsoft Neural Voices).
 *
 * Responsibilities:
 *   - Convert text to SSML with devotional markup via {@link SsmlDevotionalBuilder}
 *   - Synthesize audio via {@link EdgeTtsClient} WebSocket protocol
 *   - LRU cache management with configurable max size
 *   - Return file paths for MediaPlayer playback
 *
 * Completely free — no API key, no billing, no limits.
 *
 * Hindi voices:
 *   - hi-IN-SwaraNeural  (female, warm and clear)
 *   - hi-IN-MadhurNeural (male, deep and natural)
 */
public class CloudTtsService {

    private static final String TAG = "CloudTtsService";
    private static final String CACHE_DIR_NAME = "cloud_tts";
    private static final long MAX_CACHE_SIZE_BYTES = 50 * 1024 * 1024; // 50MB

    private final Context appContext;
    private final PreferenceManager preferences;
    private final EdgeTtsClient edgeTtsClient;
    private final SsmlDevotionalBuilder ssmlBuilder;
    private final File cacheDir;

    public CloudTtsService(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
        this.preferences = new PreferenceManager(appContext);
        this.edgeTtsClient = new EdgeTtsClient();
        this.ssmlBuilder = new SsmlDevotionalBuilder();
        this.cacheDir = new File(appContext.getCacheDir(), CACHE_DIR_NAME);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    /**
     * Synthesize a single line of devotional text to audio.
     *
     * @param text        the line of text to synthesize
     * @param contentType content type for SSML profile (aarti, chalisa, mantra, etc.)
     * @return path to the cached audio file, or null on failure
     */
    @Nullable
    public String synthesizeLine(@NonNull String text, @Nullable String contentType) {
        String voiceName = getVoiceName();
        String processedText = SanskritPronunciationLexicon.processForCloudTts(text);
        String ssml = ssmlBuilder
                .setContentType(contentType)
                .setVoiceName(voiceName)
                .buildSingleLine(processedText);

        // Check cache first
        String cacheKey = computeCacheKey(ssml, voiceName);
        File cachedFile = new File(cacheDir, cacheKey + ".mp3");
        if (cachedFile.exists() && cachedFile.length() > 0) {
            Log.d(TAG, "Cache hit: " + cacheKey);
            return cachedFile.getAbsolutePath();
        }

        // Synthesize via Edge TTS
        boolean success = edgeTtsClient.synthesize(ssml, cachedFile);
        if (success && cachedFile.exists() && cachedFile.length() > 0) {
            Log.d(TAG, "Synthesized line: " + cachedFile.getName()
                    + " (" + cachedFile.length() + " bytes)");
            evictCache();
            return cachedFile.getAbsolutePath();
        } else {
            Log.e(TAG, "Edge TTS synthesis failed for line");
            cachedFile.delete(); // Clean up partial file
            return null;
        }
    }

    /**
     * Synthesize full text (all lines) to a single audio file.
     *
     * @param text        full devotional text
     * @param contentType content type for SSML profile
     * @return path to the cached audio file, or null on failure
     */
    @Nullable
    public String synthesizeFullText(@NonNull String text, @Nullable String contentType) {
        String voiceName = getVoiceName();
        String processedText = SanskritPronunciationLexicon.processForCloudTts(text);
        String ssml = ssmlBuilder
                .setContentType(contentType)
                .setVoiceName(voiceName)
                .buildFullText(processedText);

        // Check cache
        String cacheKey = computeCacheKey(ssml, voiceName);
        File cachedFile = new File(cacheDir, cacheKey + ".mp3");
        if (cachedFile.exists() && cachedFile.length() > 0) {
            Log.d(TAG, "Cache hit for full text: " + cacheKey);
            return cachedFile.getAbsolutePath();
        }

        // Synthesize via Edge TTS
        boolean success = edgeTtsClient.synthesize(ssml, cachedFile);
        if (success && cachedFile.exists() && cachedFile.length() > 0) {
            Log.d(TAG, "Synthesized full text: " + cachedFile.getName()
                    + " (" + cachedFile.length() + " bytes)");
            evictCache();
            return cachedFile.getAbsolutePath();
        } else {
            Log.e(TAG, "Edge TTS synthesis failed for full text");
            cachedFile.delete();
            return null;
        }
    }

    /**
     * Check if a cached audio file exists for the given text.
     */
    public boolean isCached(@NonNull String text, @Nullable String contentType) {
        String voiceName = getVoiceName();
        String processedText = SanskritPronunciationLexicon.processForCloudTts(text);
        String ssml = ssmlBuilder
                .setContentType(contentType)
                .setVoiceName(voiceName)
                .buildSingleLine(processedText);
        String cacheKey = computeCacheKey(ssml, voiceName);
        File cachedFile = new File(cacheDir, cacheKey + ".mp3");
        return cachedFile.exists() && cachedFile.length() > 0;
    }

    /**
     * Check if Edge TTS is available (enabled in preferences).
     * No API key needed — Edge TTS is always available when enabled.
     */
    public boolean isAvailable() {
        return preferences.isCloudTtsEnabled();
    }

    /**
     * Evict cached files using LRU until total size is under the limit.
     */
    public void evictCache() {
        File[] files = cacheDir.listFiles();
        if (files == null || files.length == 0) return;

        long totalSize = 0;
        for (File f : files) totalSize += f.length();

        if (totalSize <= MAX_CACHE_SIZE_BYTES) return;

        // Sort by last modified (oldest first)
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        for (File f : files) {
            if (totalSize <= MAX_CACHE_SIZE_BYTES) break;
            long size = f.length();
            if (f.delete()) {
                totalSize -= size;
                Log.d(TAG, "Evicted cache file: " + f.getName());
            }
        }
    }

    /**
     * Clear all cached TTS audio files.
     */
    public void clearCache() {
        File[] files = cacheDir.listFiles();
        if (files == null) return;
        for (File f : files) {
            f.delete();
        }
        Log.d(TAG, "TTS cache cleared");
    }

    /**
     * Get total cache size in bytes.
     */
    public long getCacheSizeBytes() {
        File[] files = cacheDir.listFiles();
        if (files == null) return 0;
        long total = 0;
        for (File f : files) total += f.length();
        return total;
    }

    // --- Internal ---

    private String getVoiceName() {
        // Check for user override
        String override = preferences.getCloudTtsVoice();
        if (override != null && !override.isEmpty()) return override;

        boolean isMale = PreferenceManager.VOICE_MALE.equals(preferences.getVoiceGender());
        return isMale ? EdgeTtsClient.VOICE_MALE : EdgeTtsClient.VOICE_FEMALE;
    }

    private String computeCacheKey(String ssml, String voiceName) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(ssml.getBytes());
            md.update(voiceName.getBytes());
            byte[] hash = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(String.format("%02x", hash[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf((ssml + voiceName).hashCode());
        }
    }
}
