package com.divyapath.app.audio;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Model representing a playable audio track (aarti, chalisa, mantra, bhajan).
 */
public class AudioTrack implements Parcelable {
    private String id;
    private String title;
    private String subtitle;
    private String deityName;
    private String audioUrl;
    private String imageUrl;
    private long durationMs;
    private String contentType; // "aarti", "chalisa", "mantra", "bhajan"
    private String language;    // "hindi", "sanskrit"
    private boolean isLocalAsset;
    private String localAssetName;
    private String lyricsHindi;
    private String lyricsEnglish;
    private int contentId; // DB entity id for lookup
    private String audioSourceType; // "local", "cached", "archive_org", "iskcon", "tts"
    private String audioSourceLabel; // "HD", "Streaming", "AI Voice", "Saved"

    public AudioTrack() {}

    public AudioTrack(String id, String title, String subtitle, String audioUrl) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.audioUrl = audioUrl;
    }

    // --- Getters & Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getDeityName() { return deityName; }
    public void setDeityName(String deityName) { this.deityName = deityName; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    @Nullable
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public boolean isLocalAsset() { return isLocalAsset; }
    public void setLocalAsset(boolean localAsset) { isLocalAsset = localAsset; }

    public String getLocalAssetName() { return localAssetName; }
    public void setLocalAssetName(String localAssetName) { this.localAssetName = localAssetName; }

    public String getLyricsHindi() { return lyricsHindi; }
    public void setLyricsHindi(String lyricsHindi) { this.lyricsHindi = lyricsHindi; }

    public String getLyricsEnglish() { return lyricsEnglish; }
    public void setLyricsEnglish(String lyricsEnglish) { this.lyricsEnglish = lyricsEnglish; }

    public int getContentId() { return contentId; }
    public void setContentId(int contentId) { this.contentId = contentId; }

    public String getAudioSourceType() { return audioSourceType; }
    public void setAudioSourceType(String audioSourceType) { this.audioSourceType = audioSourceType; }

    public String getAudioSourceLabel() { return audioSourceLabel; }
    public void setAudioSourceLabel(String audioSourceLabel) { this.audioSourceLabel = audioSourceLabel; }

    // --- Parcelable ---

    protected AudioTrack(Parcel in) {
        id = in.readString();
        title = in.readString();
        subtitle = in.readString();
        deityName = in.readString();
        audioUrl = in.readString();
        imageUrl = in.readString();
        durationMs = in.readLong();
        contentType = in.readString();
        language = in.readString();
        isLocalAsset = in.readByte() != 0;
        localAssetName = in.readString();
        lyricsHindi = in.readString();
        lyricsEnglish = in.readString();
        contentId = in.readInt();
        audioSourceType = in.readString();
        audioSourceLabel = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(deityName);
        dest.writeString(audioUrl);
        dest.writeString(imageUrl);
        dest.writeLong(durationMs);
        dest.writeString(contentType);
        dest.writeString(language);
        dest.writeByte((byte) (isLocalAsset ? 1 : 0));
        dest.writeString(localAssetName);
        dest.writeString(lyricsHindi);
        dest.writeString(lyricsEnglish);
        dest.writeInt(contentId);
        dest.writeString(audioSourceType);
        dest.writeString(audioSourceLabel);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<AudioTrack> CREATOR = new Creator<AudioTrack>() {
        @Override
        public AudioTrack createFromParcel(Parcel in) { return new AudioTrack(in); }
        @Override
        public AudioTrack[] newArray(int size) { return new AudioTrack[size]; }
    };
}
