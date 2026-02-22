package com.divyapath.app.ui.mantra;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentMantraDetailBinding;
import com.divyapath.app.utils.TtsPlayerManager;

public class MantraDetailFragment extends Fragment {

    private FragmentMantraDetailBinding binding;
    private int malaCount = 0;
    private int malaTarget = 108;
    private TtsPlayerManager ttsPlayer;
    private static final int[] TARGETS = {11, 21, 54, 108, 1008};
    private android.media.MediaPlayer bellPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        binding = FragmentMantraDetailBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        MantraViewModel vm = new ViewModelProvider(this).get(MantraViewModel.class);
        ttsPlayer = new TtsPlayerManager(requireContext());
        ttsPlayer.setContentType("mantra");

        int id = getArguments() != null ? getArguments().getInt("contentId", 1) : 1;

        vm.getMantraById(id).observe(getViewLifecycleOwner(), m -> {
            if (m != null) {
                binding.tvMantraSanskritDetail.setText(m.getSanskrit());
                binding.tvMantraMeaning.setText(m.getHindiMeaning());
                binding.tvMantraTransliteration.setText(m.getEnglishTransliteration());
                binding.tvMantraBenefits.setText(m.getBenefits());

                if (m.getRecommendedCount() > 0) malaTarget = m.getRecommendedCount();
                setupTargetSelector();
                updateMala();

                ttsPlayer.setText(m.getSanskrit());
                ttsPlayer.setAudioUrl(m.getArchiveOrgUrl());

                binding.btnListenMantra.setOnClickListener(x -> ttsPlayer.togglePlayPause());

                ttsPlayer.getIsPlaying().observe(getViewLifecycleOwner(), p -> {
                    binding.btnListenMantra.setText(p ? "Stop" : "Listen");
                    binding.btnListenMantra.setIconResource(p ? R.drawable.ic_pause : R.drawable.ic_play);
                });
            }
        });

        setupTargetSelector();
        updateMala();

        bellPlayer = android.media.MediaPlayer.create(requireContext(), R.raw.bell_tone);

        binding.btnMalaTap.setOnClickListener(x -> {
            if (malaCount < malaTarget) {
                malaCount++;
                updateMala();
                performHaptic(v);

                if (malaCount % 10 == 0 && bellPlayer != null) {
                    try { bellPlayer.seekTo(0); bellPlayer.start(); } catch (Exception ignored) {}
                }

                ObjectAnimator sx = ObjectAnimator.ofFloat(binding.tvMalaCount, "scaleX", 1f, 1.3f, 1f);
                sx.setDuration(250); sx.start();
                ObjectAnimator sy = ObjectAnimator.ofFloat(binding.tvMalaCount, "scaleY", 1f, 1.3f, 1f);
                sy.setDuration(250); sy.start();

                if (malaCount == malaTarget) {
                    performHapticComplete(v);
                    if (bellPlayer != null) {
                        try { bellPlayer.seekTo(0); bellPlayer.start(); } catch (Exception ignored) {}
                    }
                    if (binding.lottieCompletion != null) {
                        binding.lottieCompletion.setVisibility(View.VISIBLE);
                        binding.lottieCompletion.playAnimation();
                        binding.lottieCompletion.addAnimatorListener(new android.animation.AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(android.animation.Animator a) {
                                if (binding != null && binding.lottieCompletion != null)
                                    binding.lottieCompletion.setVisibility(View.GONE);
                            }
                        });
                    }
                    com.google.android.material.snackbar.Snackbar.make(binding.getRoot(),
                            "Mala Complete! " + malaTarget + " recitations done.",
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show();
                }
            }
        });

        binding.btnMalaReset.setOnClickListener(x -> { malaCount = 0; updateMala(); });

        binding.toolbarMantraDetail.setNavigationOnClickListener(x -> {
            if (ttsPlayer != null) ttsPlayer.stop();
            androidx.navigation.Navigation.findNavController(v).popBackStack();
        });
    }

    private void setupTargetSelector() {
        binding.chipTargetGroup.removeAllViews();
        for (int t : TARGETS) {
            com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(requireContext());
            chip.setText(String.valueOf(t));
            chip.setCheckable(true);
            chip.setChecked(t == malaTarget);
            chip.setChipBackgroundColorResource(R.color.cream_background);
            chip.setTextColor(getResources().getColor(R.color.saffron_primary, null));
            chip.setOnClickListener(c -> {
                malaTarget = t;
                malaCount = 0;
                updateMala();
                for (int i = 0; i < binding.chipTargetGroup.getChildCount(); i++) {
                    View child = binding.chipTargetGroup.getChildAt(i);
                    if (child instanceof com.google.android.material.chip.Chip)
                        ((com.google.android.material.chip.Chip) child).setChecked(child == c);
                }
            });
            binding.chipTargetGroup.addView(chip);
        }
    }

    private void performHaptic(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Vibrator vib = (Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vib != null) vib.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        }
    }

    private void performHapticComplete(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Vibrator vib = (Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vib != null) vib.vibrate(VibrationEffect.createWaveform(new long[]{0, 100, 50, 100, 50, 200}, -1));
        } else {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
    }

    private void updateMala() {
        binding.tvMalaCount.setText(String.valueOf(malaCount));
        binding.tvMalaTarget.setText("of " + malaTarget);
        binding.progressMala.setProgress((int) (malaCount * 100f / malaTarget));
        binding.progressMala.setMax(100);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ttsPlayer != null) ttsPlayer.release();
        if (bellPlayer != null) { bellPlayer.release(); bellPlayer = null; }
        binding = null;
    }
}
