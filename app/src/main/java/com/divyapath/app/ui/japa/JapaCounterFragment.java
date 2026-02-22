package com.divyapath.app.ui.japa;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.divyapath.app.databinding.FragmentJapaCounterBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

/**
 * Japa Counter (Mala Counter) — count mantra repetitions bead by bead.
 * Each mala = 108 beads. Tracks session, daily, and lifetime totals.
 */
public class JapaCounterFragment extends Fragment {

    private static final String[] MANTRAS = {
            "Om Namah Shivaya",
            "Om Namo Narayanaya",
            "Hare Krishna Maha Mantra",
            "Om Gan Ganapataye Namah",
            "Om Shri Hanumate Namah",
            "Om Aim Saraswatyai Namah",
            "Om Shri Mahalakshmyai Namah",
            "Om Namah Bhagavate Vasudevaya",
            "Ram Ram Ram",
            "Om Shanti Shanti Shanti"
    };

    private FragmentJapaCounterBinding binding;
    private JapaCounterViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentJapaCounterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(JapaCounterViewModel.class);

        setupToolbar();
        setupMantraSpinner();
        setupButtons();
        observeViewModel();
        keepScreenOn(true);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());
    }

    private void setupMantraSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, MANTRAS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spMantra.setAdapter(adapter);

        // Set initial selection from saved mantra
        viewModel.getSelectedMantra().observe(getViewLifecycleOwner(), mantra -> {
            for (int i = 0; i < MANTRAS.length; i++) {
                if (MANTRAS[i].equals(mantra)) {
                    binding.spMantra.setSelection(i);
                    break;
                }
            }
        });

        binding.spMantra.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int pos, long id) {
                viewModel.setMantra(MANTRAS[pos]);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupButtons() {
        // Main count button
        binding.btnCount.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            viewModel.incrementBead();
        });

        // Also allow tapping the ring area
        binding.ivRingProgress.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            viewModel.incrementBead();
        });

        // Reset button
        binding.btnReset.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Reset Counter")
                        .setMessage("Reset bead count and session malas? Today's total will not be affected.")
                        .setPositiveButton("Reset", (d, w) -> viewModel.resetCounter())
                        .setNegativeButton("Cancel", null)
                        .show());

        // Target button
        binding.btnTarget.setOnClickListener(v -> showTargetPicker());
    }

    private void showTargetPicker() {
        String[] targets = {"1 mala", "3 malas", "5 malas", "7 malas", "11 malas", "21 malas"};
        int[] values = {1, 3, 5, 7, 11, 21};

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Set Daily Target")
                .setItems(targets, (d, which) -> viewModel.setTarget(values[which]))
                .show();
    }

    private void observeViewModel() {
        // Bead count
        viewModel.getCurrentBead().observe(getViewLifecycleOwner(), bead -> {
            binding.tvBeadCount.setText(String.valueOf(bead));
            // Update ring progress (level 0-10000 maps to 0-100%)
            int level = (int) ((bead / 108.0) * 10000);
            binding.ivRingProgress.setImageLevel(level);
        });

        // Session malas
        viewModel.getSessionMalas().observe(getViewLifecycleOwner(), malas ->
                binding.tvMalaCount.setText("Session Malas: " + malas));

        // Today total
        viewModel.getTodayTotal().observe(getViewLifecycleOwner(), today -> {
            binding.tvTodayCount.setText(today + " malas");
            updateProgress(today);
        });

        // Target
        viewModel.getTargetMalas().observe(getViewLifecycleOwner(), target -> {
            binding.tvTargetLabel.setText("Target: " + target + " malas");
            Integer today = viewModel.getTodayTotal().getValue();
            if (today != null) {
                updateProgress(today);
            }
        });

        // Lifetime
        viewModel.getLifetimeTotal().observe(getViewLifecycleOwner(), lifetime ->
                binding.tvLifetimeTotal.setText(lifetime + " malas"));

        // Mala completion feedback
        viewModel.getMalaJustCompleted().observe(getViewLifecycleOwner(), completed -> {
            if (Boolean.TRUE.equals(completed)) {
                Snackbar.make(binding.getRoot(),
                        "\uD83D\uDE4F Mala Complete! Hari Om!", Snackbar.LENGTH_SHORT).show();
                viewModel.onMalaCompletionAcknowledged();
            }
        });
    }

    private void updateProgress(int today) {
        Integer target = viewModel.getTargetMalas().getValue();
        int t = target != null ? target : 3;
        int progress = t > 0 ? Math.min(100, (today * 100) / t) : 0;
        binding.progressTarget.setProgress(progress);
    }

    private void keepScreenOn(boolean on) {
        if (getActivity() != null && getActivity().getWindow() != null) {
            if (on) {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    @Override
    public void onDestroyView() {
        keepScreenOn(false);
        super.onDestroyView();
        binding = null;
    }
}
