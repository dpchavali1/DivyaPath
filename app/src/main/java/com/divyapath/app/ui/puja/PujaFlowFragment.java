package com.divyapath.app.ui.puja;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentPujaFlowBinding;
import com.divyapath.app.utils.PujaFlowData;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PujaFlowFragment extends Fragment {

    private FragmentPujaFlowBinding binding;
    private PujaFlowViewModel viewModel;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPujaFlowBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PujaFlowViewModel.class);

        String deityName = getArguments() != null ? getArguments().getString("deityName", "") : "";
        String tier = getArguments() != null ? getArguments().getString("tier", PujaFlowData.TIER_QUICK) : PujaFlowData.TIER_QUICK;

        if (viewModel.getTotalSteps() == 0) {
            viewModel.init(deityName, tier);
        }

        binding.toolbar.setTitle(deityName + " — " + PujaFlowData.getTierDisplayName(tier));
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        setupObservers();
        setupClickListeners();
    }

    private void setupObservers() {
        viewModel.getCurrentStep().observe(getViewLifecycleOwner(), step -> {
            if (step == null) return;

            binding.tvStepTitle.setText(step.title);
            binding.tvStepTitleHindi.setText(step.titleHindi);
            binding.tvInstruction.setText(step.instruction);
            binding.tvInstructionHindi.setText(step.instructionHindi);

            // Items needed
            binding.chipGroupItems.removeAllViews();
            if (step.itemsNeeded != null && !step.itemsNeeded.isEmpty()) {
                binding.layoutItemsNeeded.setVisibility(View.VISIBLE);
                for (String item : step.itemsNeeded) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(item);
                    chip.setChipBackgroundColorResource(R.color.saffron_50);
                    chip.setClickable(false);
                    binding.chipGroupItems.addView(chip);
                }
            } else {
                binding.layoutItemsNeeded.setVisibility(View.GONE);
            }

            // Audio cue
            if (step.audioCueResId != 0) {
                binding.btnAudioCue.setVisibility(View.VISIBLE);
            } else {
                binding.btnAudioCue.setVisibility(View.GONE);
            }
        });

        viewModel.getCurrentStepIndex().observe(getViewLifecycleOwner(), index -> {
            int total = viewModel.getTotalSteps();
            binding.tvStepCounter.setText("Step " + (index + 1) + " of " + total);
            binding.progressSteps.setMax(total);
            binding.progressSteps.setProgress(index + 1);

            // Show/hide previous button
            binding.btnPrevious.setEnabled(index > 0);
            binding.btnPrevious.setAlpha(index > 0 ? 1f : 0.4f);

            // Change next button text on last step
            if (index == total - 1) {
                binding.btnNext.setText("Complete");
            } else {
                binding.btnNext.setText("Next");
            }
        });

        viewModel.getIsComplete().observe(getViewLifecycleOwner(), complete -> {
            if (Boolean.TRUE.equals(complete)) {
                showCompletionDialog();
            }
        });
    }

    private void setupClickListeners() {
        binding.btnNext.setOnClickListener(v -> viewModel.nextStep());
        binding.btnPrevious.setOnClickListener(v -> viewModel.previousStep());

        binding.btnAudioCue.setOnClickListener(v -> {
            PujaFlowData.PujaStep step = viewModel.getCurrentStep().getValue();
            if (step != null && step.audioCueResId != 0) {
                playAudioCue(step.audioCueResId);
            }
        });
    }

    private void playAudioCue(int resId) {
        releaseMediaPlayer();
        try {
            mediaPlayer = MediaPlayer.create(requireContext(), resId);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
                mediaPlayer.start();
            }
        } catch (Exception ignored) {}
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }

    private void showCompletionDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Puja Complete")
                .setMessage("You have completed the " + viewModel.getDeityName() +
                        " puja. May the blessings of the divine be with you.\n\nHar Har Mahadev!")
                .setPositiveButton("Done", (d, w) ->
                        Navigation.findNavController(requireView()).navigateUp())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseMediaPlayer();
        binding = null;
    }
}
