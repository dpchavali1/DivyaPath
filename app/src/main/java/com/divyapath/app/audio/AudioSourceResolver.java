package com.divyapath.app.audio;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.divyapath.app.R;
import com.divyapath.app.utils.PreferenceManager;

import java.io.File;

/**
 * Resolves audio for a content item using the 4-tier fallback:
 *   1. LOCAL  — raw:xxx bundled asset  (label "HD")
 *   2. CACHED — previously downloaded file  (label "Saved")
 *   3. ARCHIVE_ORG / ISKCON — streaming URL  (label "Streaming")
 *   4. TTS   — Android TextToSpeech  (label "AI Voice")
 */
public class AudioSourceResolver {

    private final Context context;

    public AudioSourceResolver(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Resolve the best available audio source for the given content.
     *
     * @param localAssetName   raw resource name (e.g. "aarti_jai_ganesh_deva"), nullable
     * @param archiveOrgUrl    archive.org direct MP3 URL, nullable
     * @param iskconUrl        ISKCON stream URL, nullable
     * @param cachedFilePath   path to cached file on disk, nullable
     * @param isCached         whether a cached copy exists
     * @param hasLyrics        whether lyrics are available for TTS fallback
     * @return AudioSource with resolved URL and badge info, never null (falls back to TTS)
     */
    @NonNull
    public AudioSource resolve(@Nullable String localAssetName,
                               @Nullable String archiveOrgUrl,
                               @Nullable String iskconUrl,
                               @Nullable String cachedFilePath,
                               boolean isCached,
                               boolean hasLyrics) {

        // Tier 1: Local raw asset
        if (!TextUtils.isEmpty(localAssetName)) {
            String resourceName = localAssetName.startsWith("raw:")
                    ? localAssetName.substring(4) : localAssetName;
            int resId = context.getResources().getIdentifier(
                    resourceName, "raw", context.getPackageName());
            if (resId != 0) {
                String uri = "android.resource://" + context.getPackageName() + "/" + resId;
                return new AudioSource(AudioSource.SourceType.LOCAL, uri,
                        "HD", R.color.abhijit_green);
            }
        }

        // Tier 2: Cached file
        if (isCached && !TextUtils.isEmpty(cachedFilePath)) {
            File cached = new File(cachedFilePath);
            if (cached.exists() && cached.length() > 0) {
                return new AudioSource(AudioSource.SourceType.CACHED,
                        "file://" + cached.getAbsolutePath(),
                        "Saved", R.color.abhijit_green);
            }
        }

        // Tier 3a: Archive.org streaming
        if (!TextUtils.isEmpty(archiveOrgUrl)) {
            return new AudioSource(AudioSource.SourceType.ARCHIVE_ORG,
                    archiveOrgUrl, "Streaming", R.color.saffron_primary);
        }

        // Tier 3b: ISKCON streaming
        if (!TextUtils.isEmpty(iskconUrl)) {
            return new AudioSource(AudioSource.SourceType.ISKCON,
                    iskconUrl, "Streaming", R.color.saffron_primary);
        }

        // Tier 4: Cloud TTS (requires internet + API key + lyrics)
        if (hasLyrics && isCloudTtsAvailable()) {
            return new AudioSource(AudioSource.SourceType.CLOUD_TTS, null,
                    "Natural Voice", R.color.saffron_light);
        }

        // Tier 5: Android system TTS fallback (only if lyrics available)
        return new AudioSource(AudioSource.SourceType.TTS, null,
                "AI Voice", R.color.text_secondary);
    }

    /**
     * Convenience method for AartiEntity fields.
     */
    @NonNull
    public AudioSource resolveForAarti(@Nullable String audioUrl,
                                       @Nullable String archiveOrgUrl,
                                       @Nullable String iskconUrl,
                                       @Nullable String cachedFilePath,
                                       boolean isCached,
                                       boolean hasLyrics) {
        // Extract local asset name from audioUrl if it has raw: prefix
        String localAssetName = null;
        if (!TextUtils.isEmpty(audioUrl) && !audioUrl.startsWith("http")) {
            localAssetName = audioUrl;
        }
        return resolve(localAssetName, archiveOrgUrl, iskconUrl,
                cachedFilePath, isCached, hasLyrics);
    }

    /**
     * Returns user-friendly description for the source type.
     */
    public static String getSourceDescription(AudioSource.SourceType type) {
        switch (type) {
            case LOCAL:      return "Playing from device";
            case CACHED:     return "Playing saved audio";
            case ARCHIVE_ORG: return "Streaming from Archive.org";
            case ISKCON:     return "Streaming from ISKCON";
            case CLOUD_TTS:  return "Natural voice reading";
            case TTS:        return "AI-generated reading";
            default:         return "";
        }
    }

    private boolean isCloudTtsAvailable() {
        PreferenceManager prefs = new PreferenceManager(context);
        return prefs.isCloudTtsEnabled();
    }
}
