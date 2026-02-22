package com.divyapath.app.ui.festival;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.divyapath.app.databinding.FragmentFestivalCountdownBinding;
import com.google.android.material.tabs.TabLayout;

public class FestivalCountdownFragment extends Fragment {

    private FragmentFestivalCountdownBinding binding;
    private FestivalCountdownViewModel viewModel;
    private MissionTaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFestivalCountdownBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FestivalCountdownViewModel.class);

        String festivalName = getArguments() != null ? getArguments().getString("festivalName", "") : "";
        int daysRemaining = getArguments() != null ? getArguments().getInt("daysRemaining", 7) : 7;

        binding.toolbar.setTitle(festivalName + " Prep");
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        if (viewModel.getTotalDays() == 0) {
            viewModel.init(festivalName, daysRemaining);
        }

        setupRecyclerView();
        setupTabs();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new MissionTaskAdapter();
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTasks.setAdapter(adapter);

        adapter.setListener((taskIndex, checked) -> {
            Integer day = viewModel.getSelectedDay().getValue();
            if (day != null) {
                viewModel.setTaskCompleted(day, taskIndex, checked);
            }
        });
    }

    private void setupTabs() {
        int totalDays = viewModel.getTotalDays();
        for (int i = 0; i < totalDays; i++) {
            binding.tabDays.addTab(binding.tabDays.newTab().setText("Day " + (i + 1)));
        }

        binding.tabDays.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.selectDay(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                adapter.submitList(tasks, viewModel.getCompletedTasks().getValue());
            }
        });

        viewModel.getCompletedTasks().observe(getViewLifecycleOwner(), completed -> {
            if (viewModel.getCurrentTasks().getValue() != null) {
                adapter.submitList(viewModel.getCurrentTasks().getValue(), completed);
            }
        });

        viewModel.getOverallProgress().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null) {
                int done = progress[0];
                int total = progress[1];
                binding.progressOverall.setMax(total);
                binding.progressOverall.setProgress(done);
                binding.tvProgressText.setText(done + " of " + total + " tasks completed");
            }
        });

        int daysRemaining = viewModel.getDaysRemaining();
        if (daysRemaining > 0) {
            binding.tvCountdown.setText(daysRemaining + " Days to Go!");
        } else if (daysRemaining == 0) {
            binding.tvCountdown.setText("Today is the Day!");
        } else {
            binding.tvCountdown.setText("Festival Prep");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
