package com.divyapath.app.ui.stotra;

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
import com.divyapath.app.databinding.FragmentStotraDetailBinding;
import com.divyapath.app.utils.TtsPlayerManager;

public class StotraDetailFragment extends Fragment {

    private FragmentStotraDetailBinding binding;
    private TtsPlayerManager ttsPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        binding = FragmentStotraDetailBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        StotraViewModel vm = new ViewModelProvider(this).get(StotraViewModel.class);
        ttsPlayer = new TtsPlayerManager(requireContext());
        ttsPlayer.setContentType("stotra");

        int id = getArguments() != null ? getArguments().getInt("contentId", 1) : 1;

        vm.getStotraById(id).observe(getViewLifecycleOwner(), stotra -> {
            if (stotra != null) {
                binding.tvStotraDetailTitle.setText(stotra.getTitle());
                binding.tvStotraDetailTitleHindi.setText(stotra.getTitleHindi());
                binding.tvStotraDetailVerses.setText(stotra.getVerseCount() + " verses");

                int mins = stotra.getDuration() / 60;
                if (mins > 0) {
                    binding.tvStotraDetailDuration.setText(mins + " min");
                    binding.tvStotraDetailDuration.setVisibility(View.VISIBLE);
                } else {
                    binding.tvStotraDetailDuration.setVisibility(View.GONE);
                }

                if (stotra.getTextSanskrit() != null && !stotra.getTextSanskrit().isEmpty()) {
                    binding.tvStotraTextSanskrit.setText(stotra.getTextSanskrit());
                } else {
                    binding.tvStotraTextSanskrit.setVisibility(View.GONE);
                }

                if (stotra.getTextHindi() != null && !stotra.getTextHindi().isEmpty()) {
                    binding.tvStotraTextHindi.setText(stotra.getTextHindi());
                } else {
                    binding.tvStotraTextHindi.setVisibility(View.GONE);
                }

                if (stotra.getTextEnglish() != null && !stotra.getTextEnglish().isEmpty()) {
                    binding.tvStotraTextEnglish.setText(stotra.getTextEnglish());
                } else {
                    binding.tvStotraTextEnglish.setVisibility(View.GONE);
                }

                String ttsText = stotra.getTextSanskrit() != null && !stotra.getTextSanskrit().isEmpty()
                        ? stotra.getTextSanskrit() : stotra.getTextHindi();
                if (ttsText != null) {
                    ttsPlayer.setText(ttsText);
                    ttsPlayer.setAudioUrl(stotra.getArchiveOrgUrl());
                }
            }
        });

        // Seekbar setup for both modes
        ttsPlayer.getIsStreamingMode().observe(getViewLifecycleOwner(), streaming -> {
            if (streaming) {
                binding.seekbarStotra.setMax(1000);
                binding.tvDurationStotra.setText("0:00");
            } else {
                binding.seekbarStotra.setMax(ttsPlayer.getTotalLines());
                binding.tvDurationStotra.setText(ttsPlayer.getTotalLines() + " lines");
            }
        });

        ttsPlayer.getStreamProgress().observe(getViewLifecycleOwner(), progress -> {
            Boolean streaming = ttsPlayer.getIsStreamingMode().getValue();
            if (streaming != null && streaming) {
                binding.seekbarStotra.setProgress(progress != null ? progress : 0);
            }
        });
        ttsPlayer.getCurrentTimeText().observe(getViewLifecycleOwner(), time -> {
            Boolean streaming = ttsPlayer.getIsStreamingMode().getValue();
            if (streaming != null && streaming) {
                binding.tvDurationStotra.setText(time != null ? time : "0:00");
            }
        });

        ttsPlayer.getCurrentLine().observe(getViewLifecycleOwner(), line -> {
            Boolean streaming = ttsPlayer.getIsStreamingMode().getValue();
            if (streaming == null || !streaming) {
                binding.seekbarStotra.setProgress(line != null ? line : 0);
            }
        });

        binding.seekbarStotra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        binding.btnPlayPauseStotra.setOnClickListener(x -> ttsPlayer.togglePlayPause());

        ttsPlayer.getIsPlaying().observe(getViewLifecycleOwner(), p ->
                binding.btnPlayPauseStotra.setImageResource(p ? R.drawable.ic_pause : R.drawable.ic_play));

        // Speed button
        binding.btnSpeedStotra.setOnClickListener(x -> {
            float newSpeed = ttsPlayer.cycleSpeed();
            binding.btnSpeedStotra.setText(TtsPlayerManager.formatSpeed(newSpeed));
        });

        binding.toolbarStotraDetail.setNavigationOnClickListener(x -> {
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
