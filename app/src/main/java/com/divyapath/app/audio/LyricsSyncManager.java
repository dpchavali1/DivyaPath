package com.divyapath.app.audio;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages lyrics synchronization for audio playback.
 * Highlights the current line based on playback progress (estimated by line count distribution).
 * Works with both audio file playback (progress-based) and TTS (line-index-based).
 */
public class LyricsSyncManager {

    private final List<LyricsLine> lyricsLines = new ArrayList<>();
    private final MutableLiveData<Integer> highlightedLine = new MutableLiveData<>(-1);
    private final MutableLiveData<List<LyricsLine>> lyricsLiveData = new MutableLiveData<>();

    public static class LyricsLine {
        private final int index;
        private final String text;
        private final boolean isHeader; // Section headers like "॥ चौपाई ॥"
        private boolean isActive;

        public LyricsLine(int index, String text, boolean isHeader) {
            this.index = index;
            this.text = text;
            this.isHeader = isHeader;
        }

        public int getIndex() { return index; }
        public String getText() { return text; }
        public boolean isHeader() { return isHeader; }
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { this.isActive = active; }
    }

    /**
     * Parse lyrics text into lines for display and sync.
     */
    public void setLyrics(@Nullable String lyrics) {
        lyricsLines.clear();
        if (TextUtils.isEmpty(lyrics)) {
            lyricsLiveData.postValue(lyricsLines);
            return;
        }

        String[] rawLines = lyrics.split("\n");
        int index = 0;
        for (String raw : rawLines) {
            String trimmed = raw.trim();
            if (trimmed.isEmpty()) continue;

            boolean isHeader = trimmed.startsWith("॥") || trimmed.startsWith("||")
                    || trimmed.matches("^[॥।\\|\\s]+.*[॥।\\|\\s]+$");
            lyricsLines.add(new LyricsLine(index, trimmed, isHeader));
            index++;
        }
        lyricsLiveData.postValue(new ArrayList<>(lyricsLines));
        highlightedLine.postValue(-1);
    }

    /**
     * Update highlight based on progress percentage (0-100) for audio file playback.
     * Distributes lines evenly across the duration.
     */
    public void updateFromProgress(int progressPercent) {
        if (lyricsLines.isEmpty()) return;

        int totalLines = lyricsLines.size();
        int lineIndex = Math.min((int) (progressPercent * totalLines / 100.0), totalLines - 1);
        setHighlightedLine(lineIndex);
    }

    /**
     * Update highlight based on TTS line index (direct mapping).
     */
    public void updateFromTtsLine(int ttsLineIndex) {
        setHighlightedLine(ttsLineIndex);
    }

    private void setHighlightedLine(int lineIndex) {
        Integer current = highlightedLine.getValue();
        if (current != null && current == lineIndex) return;

        for (LyricsLine line : lyricsLines) {
            line.setActive(line.getIndex() == lineIndex);
        }
        highlightedLine.postValue(lineIndex);
        lyricsLiveData.postValue(new ArrayList<>(lyricsLines));
    }

    /**
     * Reset highlight state.
     */
    public void reset() {
        for (LyricsLine line : lyricsLines) {
            line.setActive(false);
        }
        highlightedLine.postValue(-1);
        lyricsLiveData.postValue(new ArrayList<>(lyricsLines));
    }

    public LiveData<Integer> getHighlightedLine() { return highlightedLine; }
    public LiveData<List<LyricsLine>> getLyricsLiveData() { return lyricsLiveData; }
    public int getTotalLines() { return lyricsLines.size(); }
    @NonNull public List<LyricsLine> getLines() { return new ArrayList<>(lyricsLines); }
}
