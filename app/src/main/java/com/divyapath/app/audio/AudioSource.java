package com.divyapath.app.audio;

/**
 * Represents the source tier for audio playback.
 * Priority: LOCAL (raw assets) > CACHED (downloaded) > ARCHIVE_ORG (stream) > TTS (generated).
 */
public class AudioSource {

    public enum SourceType {
        LOCAL,          // raw:xxx assets bundled with APK
        CACHED,         // Previously downloaded from archive.org, saved locally
        ARCHIVE_ORG,    // Streaming from archive.org
        ISKCON,         // Streaming from ISKCON sources
        CLOUD_TTS,      // Google Cloud Neural2/WaveNet voice (natural-sounding)
        TTS             // Android TTS with background music (emergency fallback)
    }

    private final SourceType sourceType;
    private final String resolvedUrl;   // playable URI
    private final String displayLabel;  // "HD", "Streaming", "AI Voice", "Saved"
    private final int badgeColorRes;    // color resource for badge tint

    public AudioSource(SourceType sourceType, String resolvedUrl,
                       String displayLabel, int badgeColorRes) {
        this.sourceType = sourceType;
        this.resolvedUrl = resolvedUrl;
        this.displayLabel = displayLabel;
        this.badgeColorRes = badgeColorRes;
    }

    public SourceType getSourceType() { return sourceType; }
    public String getResolvedUrl() { return resolvedUrl; }
    public String getDisplayLabel() { return displayLabel; }
    public int getBadgeColorRes() { return badgeColorRes; }

    public boolean isStreamable() {
        return sourceType == SourceType.ARCHIVE_ORG || sourceType == SourceType.ISKCON;
    }

    public boolean isTts() {
        return sourceType == SourceType.TTS || sourceType == SourceType.CLOUD_TTS;
    }

    public boolean isCloudTts() {
        return sourceType == SourceType.CLOUD_TTS;
    }

    public boolean isLocal() {
        return sourceType == SourceType.LOCAL || sourceType == SourceType.CACHED;
    }
}
