package com.divyapath.app.ui.chalisa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentChalisaDetailBinding;
import com.divyapath.app.utils.PreferenceManager;
import com.divyapath.app.utils.TtsPlayerManager;

public class ChalisaDetailFragment extends Fragment {

    private FragmentChalisaDetailBinding binding;
    private TtsPlayerManager ttsPlayer;
    private Handler scrollHandler;
    private boolean autoScroll = false;
    private float fontSize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        binding = FragmentChalisaDetailBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        ChalisaViewModel vm = new ViewModelProvider(this).get(ChalisaViewModel.class);
        ttsPlayer = new TtsPlayerManager(requireContext());
        ttsPlayer.setContentType("chalisa");
        scrollHandler = new Handler(Looper.getMainLooper());
        fontSize = new PreferenceManager(requireContext()).getFontSize();
        binding.tvChalisaContent.setTextSize(fontSize);

        int id = getArguments() != null ? getArguments().getInt("contentId", 1) : 1;

        vm.getChalisaById(id).observe(getViewLifecycleOwner(), ch -> {
            if (ch != null) {
                binding.tvChalisaDetailTitle.setText(
                        ch.getTitleHindi() != null ? ch.getTitleHindi() : ch.getTitle());
                binding.tvChalisaContent.setText(ch.getContent());
                ttsPlayer.setText(ch.getContent());
                ttsPlayer.setAudioUrl(ch.getArchiveOrgUrl());

                binding.btnShareChalisa.setOnClickListener(x -> {
                    Intent s = new Intent(Intent.ACTION_SEND);
                    s.setType("text/plain");
                    s.putExtra(Intent.EXTRA_TEXT, ch.getTitle() + "\n\n" + ch.getContent() + "\n\n— via DivyaPath");
                    startActivity(Intent.createChooser(s, "Share"));
                });
            }
        });

        // Seekbar setup for both modes
        ttsPlayer.getIsStreamingMode().observe(getViewLifecycleOwner(), streaming -> {
            if (streaming) {
                binding.seekbarChalisa.setMax(1000);
                binding.tvDurationChalisa.setText("0:00");
            } else {
                binding.seekbarChalisa.setMax(ttsPlayer.getTotalLines());
                binding.tvDurationChalisa.setText(ttsPlayer.getTotalLines() + " lines");
            }
        });

        ttsPlayer.getStreamProgress().observe(getViewLifecycleOwner(), progress -> {
            Boolean streaming = ttsPlayer.getIsStreamingMode().getValue();
            if (streaming != null && streaming) {
                binding.seekbarChalisa.setProgress(progress != null ? progress : 0);
            }
        });
        ttsPlayer.getCurrentTimeText().observe(getViewLifecycleOwner(), time -> {
            Boolean streaming = ttsPlayer.getIsStreamingMode().getValue();
            if (streaming != null && streaming) {
                binding.tvDurationChalisa.setText(time != null ? time : "0:00");
            }
        });

        ttsPlayer.getCurrentLine().observe(getViewLifecycleOwner(), line -> {
            Boolean streaming = ttsPlayer.getIsStreamingMode().getValue();
            if (streaming == null || !streaming) {
                binding.seekbarChalisa.setProgress(line != null ? line : 0);
            }
        });

        binding.seekbarChalisa.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        binding.btnPlayPauseChalisa.setOnClickListener(x -> ttsPlayer.togglePlayPause());

        ttsPlayer.getIsPlaying().observe(getViewLifecycleOwner(), p ->
                binding.btnPlayPauseChalisa.setImageResource(p ? R.drawable.ic_pause : R.drawable.ic_play));

        // Speed button
        binding.btnSpeedChalisa.setOnClickListener(x -> {
            float newSpeed = ttsPlayer.cycleSpeed();
            binding.btnSpeedChalisa.setText(TtsPlayerManager.formatSpeed(newSpeed));
        });

        // Auto scroll
        binding.btnAutoScroll.setOnClickListener(btn -> {
            if (!autoScroll) startAutoScroll();
            else stopAutoScroll();
        });

        // Font controls
        binding.btnFontIncrease.setOnClickListener(x -> {
            fontSize = Math.min(fontSize + 2, 32);
            binding.tvChalisaContent.setTextSize(fontSize);
        });
        binding.btnFontDecrease.setOnClickListener(x -> {
            fontSize = Math.max(fontSize - 2, 12);
            binding.tvChalisaContent.setTextSize(fontSize);
        });

        binding.toolbarChalisaDetail.setNavigationOnClickListener(x -> {
            if (ttsPlayer != null) ttsPlayer.stop();
            androidx.navigation.Navigation.findNavController(v).popBackStack();
        });
    }

    private void startAutoScroll() {
        autoScroll = true;
        scrollHandler.post(new Runnable() {
            @Override
            public void run() {
                if (autoScroll && binding != null) {
                    binding.scrollChalisa.smoothScrollBy(0, 2);
                    scrollHandler.postDelayed(this, 50);
                }
            }
        });
    }

    private void stopAutoScroll() {
        autoScroll = false;
        scrollHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAutoScroll();
        if (ttsPlayer != null) ttsPlayer.release();
        binding = null;
    }
}
