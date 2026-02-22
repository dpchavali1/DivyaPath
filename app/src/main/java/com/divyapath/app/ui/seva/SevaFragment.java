package com.divyapath.app.ui.seva;

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

import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentSevaBinding;
import com.divyapath.app.utils.SevaData;

import java.util.List;

public class SevaFragment extends Fragment {

    private FragmentSevaBinding binding;
    private SevaViewModel viewModel;
    private SevaBrowseAdapter browseAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSevaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SevaViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        setupBrowseList();
        observeViewModel();

        binding.btnSevaDone.setOnClickListener(v -> viewModel.markDone());
    }

    private void setupBrowseList() {
        browseAdapter = new SevaBrowseAdapter();
        binding.rvAllSevas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAllSevas.setAdapter(browseAdapter);
    }

    private void observeViewModel() {
        viewModel.getTodaysSeva().observe(getViewLifecycleOwner(), seva -> {
            if (seva != null) {
                binding.tvSevaTitle.setText(seva.title);
                binding.tvSevaTitleHindi.setText(seva.titleHindi);
                binding.tvSevaCategory.setText(seva.category);
                binding.tvSevaDescription.setText(seva.description);
                binding.ivSevaIcon.setImageResource(seva.iconRes);
            }
        });

        viewModel.getIsCompletedToday().observe(getViewLifecycleOwner(), done -> {
            if (Boolean.TRUE.equals(done)) {
                binding.btnSevaDone.setText("Completed!");
                binding.btnSevaDone.setEnabled(false);
                binding.btnSevaDone.setIconResource(R.drawable.ic_check);
            } else {
                binding.btnSevaDone.setText("Mark as Done");
                binding.btnSevaDone.setEnabled(true);
                binding.btnSevaDone.setIconResource(R.drawable.ic_check);
            }
        });

        viewModel.getStreak().observe(getViewLifecycleOwner(), s ->
                binding.tvStreakCount.setText(String.valueOf(s)));

        viewModel.getMonthlyCount().observe(getViewLifecycleOwner(), c ->
                binding.tvMonthlyCount.setText(String.valueOf(c)));

        viewModel.getAllSevas().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                browseAdapter.submitList(list);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
