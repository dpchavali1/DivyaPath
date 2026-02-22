package com.divyapath.app.ui.darshan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.divyapath.app.R;
import com.divyapath.app.data.local.entity.TempleEntity;
import com.divyapath.app.databinding.FragmentPilgrimageLogBinding;
import com.divyapath.app.utils.PreferenceManager;
import com.divyapath.app.utils.ShareHelper;

import java.util.ArrayList;
import java.util.List;

public class PilgrimageLogFragment extends Fragment {

    private FragmentPilgrimageLogBinding binding;
    private PilgrimageLogAdapter adapter;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPilgrimageLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferenceManager = new PreferenceManager(requireContext());

        adapter = new PilgrimageLogAdapter(preferenceManager);
        binding.rvPilgrimage.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPilgrimage.setAdapter(adapter);

        DarshanViewModel vm = new ViewModelProvider(this).get(DarshanViewModel.class);
        vm.getAllTemples().observe(getViewLifecycleOwner(), temples -> {
            if (temples == null) return;

            List<TempleEntity> visited = new ArrayList<>();
            for (TempleEntity t : temples) {
                if (preferenceManager.getSharedPreferences()
                        .getBoolean("temple_visited_" + t.getId(), false)) {
                    visited.add(t);
                }
            }

            if (visited.isEmpty()) {
                binding.rvPilgrimage.setVisibility(View.GONE);
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.tvVisitedCount.setText("0 temples visited");
            } else {
                binding.rvPilgrimage.setVisibility(View.VISIBLE);
                binding.tvEmpty.setVisibility(View.GONE);
                binding.tvVisitedCount.setText(visited.size() + " temples visited");
                adapter.submitList(visited);
            }
        });

        binding.btnShareLog.setOnClickListener(v -> shareLog());
    }

    private void shareLog() {
        List<TempleEntity> list = adapter.getCurrentList();
        if (list.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append("My Pilgrimage Journey (DivyaPath)\n\n");
        for (TempleEntity t : list) {
            String date = preferenceManager.getSharedPreferences()
                    .getString("temple_visit_date_" + t.getId(), "");
            sb.append("- ").append(t.getName());
            if (!date.isEmpty()) sb.append(" (").append(date).append(")");
            sb.append("\n");
        }
        sb.append("\nTotal: ").append(list.size()).append(" temples");
        ShareHelper.shareText(requireContext(), "My Pilgrimage Log", sb.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
