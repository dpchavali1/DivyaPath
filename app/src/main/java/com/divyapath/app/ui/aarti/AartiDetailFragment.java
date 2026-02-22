package com.divyapath.app.ui.aarti;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.divyapath.app.R;
import com.divyapath.app.audio.AudioPlayerManager;
import com.divyapath.app.audio.AudioSource;
import com.divyapath.app.audio.AudioSourceResolver;
import com.divyapath.app.audio.AudioTrack;
import com.divyapath.app.data.local.entity.AartiEntity;
import com.divyapath.app.databinding.FragmentAartiDetailBinding;
import com.divyapath.app.utils.PreferenceManager;
import com.divyapath.app.utils.TtsPlayerManager;

import java.util.Locale;

public class AartiDetailFragment extends Fragment {

    private enum PlaybackTarget {
        SINGING,
        READING
    }

    private FragmentAartiDetailBinding binding;
    private AartiViewModel vm;
    private TtsPlayerManager ttsPlayer;
    private AudioPlayerManager audioPlayer;
    private AudioSourceResolver audioSourceResolver;
    private PreferenceManager preferences;
    private float fontSize;
    private boolean isBookmarked = false;
    private boolean fallbackToDeityLookupAttached = false;
    private PlaybackTarget activeTarget = PlaybackTarget.READING;
    private AartiEntity currentAarti;
    private AudioSource currentAudioSource;
    private LiveData<Boolean> bookmarkState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        binding = FragmentAartiDetailBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        vm = new ViewModelProvider(this).get(AartiViewModel.class);
        preferences = new PreferenceManager(requireContext());
        ttsPlayer = new TtsPlayerManager(requireContext());
        ttsPlayer.setContentType("aarti");
        audioPlayer = AudioPlayerManager.getInstance(requireContext());
        audioSourceResolver = new AudioSourceResolver(requireContext());

        fontSize = preferences.getFontSize();
        binding.tvAartiLyrics.setTextSize(fontSize);

        bindPlayerObservers();
        bindControls();
        loadAarti();
        binding.toolbarAartiDetail.setNavigationOnClickListener(x -> {
            ttsPlayer.stop();
            androidx.navigation.Navigation.findNavController(v).popBackStack();
        });
    }

    private void bindPlayerObservers() {
        // TTS reading observer
        ttsPlayer.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            if (activeTarget == PlaybackTarget.READING) {
                updatePlayButtonIcon(Boolean.TRUE.equals(isPlaying));
            }
        });

        ttsPlayer.getCurrentLine().observe(getViewLifecycleOwner(), line -> {
            if (binding == null || activeTarget != PlaybackTarget.READING) return;
            binding.seekbarAudio.setProgress(line != null ? line : 0);
        });

        // Singleton audio player observers
        audioPlayer.getIsPlayingLiveData().observe(getViewLifecycleOwner(), isPlaying -> {
            if (activeTarget == PlaybackTarget.SINGING) {
                updatePlayButtonIcon(Boolean.TRUE.equals(isPlaying));
            }
        });

        audioPlayer.getProgressLiveData().observe(getViewLifecycleOwner(), progress -> {
            if (binding != null && activeTarget == PlaybackTarget.SINGING && progress != null) {
                binding.seekbarAudio.setMax(100);
                binding.seekbarAudio.setProgress(progress);
            }
        });

        audioPlayer.getCurrentTimeLiveData().observe(getViewLifecycleOwner(), time -> {
            if (binding != null && activeTarget == PlaybackTarget.SINGING && time != null) {
                String total = audioPlayer.getTotalTimeLiveData().getValue();
                binding.tvAudioDuration.setText(time + " / " + (total != null ? total : "0:00"));
            }
        });

        audioPlayer.getTotalTimeLiveData().observe(getViewLifecycleOwner(), total -> {
            if (binding != null && activeTarget == PlaybackTarget.SINGING && total != null) {
                String current = audioPlayer.getCurrentTimeLiveData().getValue();
                binding.tvAudioDuration.setText((current != null ? current : "0:00") + " / " + total);
            }
        });
    }

    private void bindControls() {
        binding.btnFontIncrease.setOnClickListener(x -> {
            fontSize = Math.min(fontSize + 2, 32);
            binding.tvAartiLyrics.setTextSize(fontSize);
        });
        binding.btnFontDecrease.setOnClickListener(x -> {
            fontSize = Math.max(fontSize - 2, 12);
            binding.tvAartiLyrics.setTextSize(fontSize);
        });

        binding.btnPlayPause.setOnClickListener(x -> togglePlayback());

        binding.seekbarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && activeTarget == PlaybackTarget.SINGING) {
                    long duration = audioPlayer.getDuration();
                    if (duration > 0) {
                        long seekPosition = (long) (progress * duration / 100.0);
                        audioPlayer.seekTo(seekPosition);
                    }
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void loadAarti() {
        Bundle args = getArguments();
        int contentId = args != null ? args.getInt("contentId", 1) : 1;
        boolean isDeityId = args != null && args.getBoolean("isDeityId", false);

        if (isDeityId) {
            observeFirstAartiForDeity(contentId);
        } else {
            vm.getAartiById(contentId).observe(getViewLifecycleOwner(), aarti -> {
                if (aarti != null) {
                    bindAarti(aarti);
                } else if (!fallbackToDeityLookupAttached) {
                    fallbackToDeityLookupAttached = true;
                    observeFirstAartiForDeity(contentId);
                }
            });
        }
    }

    private void observeFirstAartiForDeity(int deityId) {
        vm.getAartisByDeity(deityId).observe(getViewLifecycleOwner(), aartis -> {
            if (aartis == null || aartis.isEmpty()) return;
            bindAarti(aartis.get(0));
        });
    }

    private void bindAarti(@NonNull AartiEntity aarti) {
        currentAarti = aarti;
        ttsPlayer.stop();

        binding.tvAartiDetailTitle.setText(
                aarti.getTitleHindi() != null ? aarti.getTitleHindi() : aarti.getTitle());
        binding.tvAartiLyrics.setText(aarti.getLyricsHindi());
        ttsPlayer.setText(aarti.getLyricsHindi());

        // Resolve audio source using the 4-tier fallback
        boolean hasLyrics = !TextUtils.isEmpty(aarti.getLyricsHindi());
        currentAudioSource = audioSourceResolver.resolveForAarti(
                aarti.getAudioUrl(),
                aarti.getArchiveOrgUrl(),
                aarti.getIskconUrl(),
                aarti.getCachedFilePath(),
                aarti.isCached(),
                hasLyrics
        );

        bindBookmark(aarti.getId());
        binding.btnBookmark.setOnClickListener(x -> vm.toggleBookmark(aarti.getId(), isBookmarked));
        binding.btnShare.setOnClickListener(x -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    aarti.getTitle() + "\n\n" + aarti.getLyricsHindi() + "\n\n— via DivyaPath");
            startActivity(Intent.createChooser(shareIntent, "Share Aarti"));
        });

        activeTarget = resolvePlaybackTarget();
        updatePlaybackUiForTarget();
    }

    private void bindBookmark(int aartiId) {
        if (bookmarkState != null) {
            bookmarkState.removeObservers(getViewLifecycleOwner());
        }
        bookmarkState = vm.isBookmarked(aartiId);
        bookmarkState.observe(getViewLifecycleOwner(), bookmarked -> {
            isBookmarked = Boolean.TRUE.equals(bookmarked);
            binding.btnBookmark.setImageResource(
                    isBookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark
            );
        });
    }

    private void togglePlayback() {
        if (currentAarti == null) return;

        PlaybackTarget target = resolvePlaybackTarget();
        if (target != activeTarget) {
            ttsPlayer.stop();
            activeTarget = target;
            updatePlaybackUiForTarget();
        }

        if (target == PlaybackTarget.SINGING) {
            toggleSingingPlayback();
        } else {
            toggleReadingPlayback();
        }
    }

    private void toggleSingingPlayback() {
        if (currentAudioSource == null || currentAudioSource.isTts()) {
            // No audio source resolved or TTS-only — fall back to reading
            activeTarget = PlaybackTarget.READING;
            updatePlaybackUiForTarget();
            toggleReadingPlayback();
            return;
        }

        String audioUrl = currentAudioSource.getResolvedUrl();
        if (TextUtils.isEmpty(audioUrl)) {
            activeTarget = PlaybackTarget.READING;
            updatePlaybackUiForTarget();
            toggleReadingPlayback();
            return;
        }

        ttsPlayer.stop();

        // Check if the singleton is already playing this track
        AudioTrack currentTrack = audioPlayer.getCurrentTrack();
        if (currentTrack != null && currentTrack.getContentId() == currentAarti.getId()
                && "aarti".equals(currentTrack.getContentType())) {
            audioPlayer.togglePlayPause();
        } else {
            AudioTrack track = buildAudioTrack(currentAarti, audioUrl);
            audioPlayer.play(track);
        }
    }

    private void toggleReadingPlayback() {
        if (Boolean.TRUE.equals(ttsPlayer.getIsPlaying().getValue())) {
            ttsPlayer.stop();
        } else {
            ttsPlayer.play();
        }
    }

    private AudioTrack buildAudioTrack(AartiEntity aarti, String audioUrl) {
        AudioTrack track = new AudioTrack();
        track.setId("aarti_" + aarti.getId());
        track.setTitle(aarti.getTitleHindi() != null ? aarti.getTitleHindi() : aarti.getTitle());
        track.setSubtitle(aarti.getTitle());
        track.setAudioUrl(audioUrl);
        track.setContentType("aarti");
        track.setContentId(aarti.getId());
        track.setDurationMs(aarti.getDuration() * 1000L);
        track.setLyricsHindi(aarti.getLyricsHindi());
        track.setLyricsEnglish(aarti.getLyricsEnglish());
        track.setLanguage("hi");

        // Set audio source metadata for player UI badge
        if (currentAudioSource != null) {
            track.setAudioSourceType(currentAudioSource.getSourceType().name().toLowerCase());
            track.setAudioSourceLabel(currentAudioSource.getDisplayLabel());
        }

        // Check if it's a local resource
        if (currentAudioSource != null && currentAudioSource.isLocal()) {
            track.setLocalAsset(true);
            String assetName = aarti.getLocalAssetName();
            if (assetName == null) assetName = aarti.getAudioUrl();
            if (assetName != null) {
                track.setLocalAssetName(assetName.startsWith("raw:") ? assetName.substring(4) : assetName);
            }
        }

        return track;
    }

    private PlaybackTarget resolvePlaybackTarget() {
        String mode = preferences.getAartiPlaybackMode();

        // Check if we have actual audio (not just TTS)
        boolean hasSingingAudio = currentAudioSource != null && !currentAudioSource.isTts();

        if (PreferenceManager.AARTI_PLAYBACK_READ.equals(mode)) {
            return PlaybackTarget.READING;
        }
        if (PreferenceManager.AARTI_PLAYBACK_SING.equals(mode)) {
            return hasSingingAudio ? PlaybackTarget.SINGING : PlaybackTarget.READING;
        }
        return hasSingingAudio ? PlaybackTarget.SINGING : PlaybackTarget.READING;
    }

    private void updatePlaybackUiForTarget() {
        if (binding == null) return;

        if (activeTarget == PlaybackTarget.SINGING) {
            binding.seekbarAudio.setEnabled(true);
            binding.seekbarAudio.setMax(100);
            Integer progress = audioPlayer.getProgressLiveData().getValue();
            binding.seekbarAudio.setProgress(progress != null ? progress : 0);
            updatePlayButtonIcon(audioPlayer.isPlaying());

            // Show source info
            if (currentAudioSource != null) {
                String desc = AudioSourceResolver.getSourceDescription(currentAudioSource.getSourceType());
                binding.tvAudioDuration.setText(desc);
            }
        } else {
            binding.seekbarAudio.setEnabled(false);
            int totalLines = Math.max(ttsPlayer.getTotalLines(), 0);
            Integer currentLine = ttsPlayer.getCurrentLine().getValue();
            binding.seekbarAudio.setMax(totalLines);
            binding.seekbarAudio.setProgress(currentLine != null ? currentLine : 0);
            binding.tvAudioDuration.setText(totalLines + " lines • AI Voice");
            updatePlayButtonIcon(Boolean.TRUE.equals(ttsPlayer.getIsPlaying().getValue()));
        }
    }

    private void updatePlayButtonIcon(boolean isPlaying) {
        if (binding != null) {
            binding.btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ttsPlayer != null) ttsPlayer.release();
        binding = null;
    }
}
