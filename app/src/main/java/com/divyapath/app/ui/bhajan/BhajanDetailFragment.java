package com.divyapath.app.ui.bhajan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentBhajanDetailBinding;
import com.divyapath.app.utils.PreferenceManager;
import com.divyapath.app.utils.TtsPlayerManager;

public class BhajanDetailFragment extends Fragment {

    private FragmentBhajanDetailBinding binding;
    private TtsPlayerManager ttsPlayer;
    private float fontSize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        binding = FragmentBhajanDetailBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        BhajanViewModel vm = new ViewModelProvider(this).get(BhajanViewModel.class);
        ttsPlayer = new TtsPlayerManager(requireContext());
        ttsPlayer.setContentType("bhajan");
        fontSize = new PreferenceManager(requireContext()).getFontSize();
        binding.tvBhajanLyricsHindi.setTextSize(fontSize);

        int id = getArguments() != null ? getArguments().getInt("contentId", 1) : 1;

        vm.getBhajanById(id).observe(getViewLifecycleOwner(), bhajan -> {
            if (bhajan != null) {
                binding.tvBhajanDetailTitle.setText(
                        bhajan.getTitleHindi() != null ? bhajan.getTitleHindi() : bhajan.getTitle());
                binding.tvBhajanDetailSubtitle.setText(bhajan.getTitle());
                binding.tvBhajanLyricsHindi.setText(bhajan.getLyricsHindi());

                if (bhajan.getLyricsEnglish() != null && !bhajan.getLyricsEnglish().isEmpty()) {
                    binding.tvBhajanLyricsEnglish.setText(bhajan.getLyricsEnglish());
                } else {
                    binding.tvBhajanLyricsEnglish.setVisibility(View.GONE);
                }

                ttsPlayer.setText(bhajan.getLyricsHindi());
                ttsPlayer.setAudioUrl(bhajan.getArchiveOrgUrl());
            }
        });

        // Seekbar setup for both streaming and TTS modes
        ttsPlayer.getIsStreamingMode().observe(getViewLifecycleOwner(), streaming -> {
            if (streaming) {
                binding.seekbarBhajan.setMax(1000);
                binding.tvDurationBhajan.setText("0:00");
            } else {
                binding.seekbarBhajan.setMax(ttsPlayer.getTotalLines());
                binding.tvDurationBhajan.setText(ttsPlayer.getTotalLines() + " lines");
            }
        });

        // Streaming progress
        ttsPlayer.getStreamProgress().observe(getViewLifecycleOwner(), progress -> {
            Boolean streaming = ttsPlayer.getIsStreamingMode().getValue();
            if (streaming != null && streaming) {
                binding.seekbarBhajan.setProgress(progress != null ? progress : 0);
            }
        });
        ttsPlayer.getCurrentTimeText().observe(getViewLifecycleOwner(), time -> {
            Boolean streaming = ttsPlayer.getIsStreamingMode().getValue();
            if (streaming != null && streaming) {
                binding.tvDurationBhajan.setText(time != null ? time : "0:00");
            }
        });

        // TTS line progress
        ttsPlayer.getCurrentLine().observe(getViewLifecycleOwner(), line -> {
            Boolean streaming = ttsPlayer.getIsStreamingMode().getValue();
            if (streaming == null || !streaming) {
                binding.seekbarBhajan.setProgress(line != null ? line : 0);
            }
        });

        // Seekbar touch for streaming seek
        binding.seekbarBhajan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                if (fromUser) {
                    Boolean streaming = ttsPlayer.getIsStreamingMode().getValue();
                    if (streaming != null && streaming) {
                        ttsPlayer.seekTo(progress);
                    }
                }
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        binding.btnPlayPauseBhajan.setOnClickListener(x -> ttsPlayer.togglePlayPause());

        ttsPlayer.getIsPlaying().observe(getViewLifecycleOwner(), p ->
                binding.btnPlayPauseBhajan.setImageResource(p ? R.drawable.ic_pause : R.drawable.ic_play));

        // Speed button
        binding.btnSpeedBhajan.setOnClickListener(x -> {
            float newSpeed = ttsPlayer.cycleSpeed();
            binding.btnSpeedBhajan.setText(TtsPlayerManager.formatSpeed(newSpeed));
        });

        binding.btnFontIncreaseBhajan.setOnClickListener(x -> {
            fontSize = Math.min(fontSize + 2, 32);
            binding.tvBhajanLyricsHindi.setTextSize(fontSize);
        });
        binding.btnFontDecreaseBhajan.setOnClickListener(x -> {
            fontSize = Math.max(fontSize - 2, 12);
            binding.tvBhajanLyricsHindi.setTextSize(fontSize);
        });

        binding.btnShareBhajan.setOnClickListener(x -> {
            Intent s = new Intent(Intent.ACTION_SEND);
            s.setType("text/plain");
            startActivity(Intent.createChooser(s, "Share Bhajan"));
        });

        binding.toolbarBhajanDetail.setNavigationOnClickListener(x -> {
            if (ttsPlayer != null) ttsPlayer.stop();
            androidx.navigation.Navigation.findNavController(v).popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ttsPlayer != null) ttsPlayer.release();
        binding = null;
    }
}
