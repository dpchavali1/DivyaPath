package com.divyapath.app.ui.darshan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.divyapath.app.R;
import com.divyapath.app.data.local.entity.TempleEntity;
import com.divyapath.app.databinding.FragmentTempleDetailBinding;
import com.divyapath.app.utils.PreferenceManager;
import com.divyapath.app.utils.TempleChecklistData;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TempleDetailFragment extends Fragment {

    private FragmentTempleDetailBinding binding;
    private PreferenceManager preferenceManager;
    private int templeId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTempleDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferenceManager = new PreferenceManager(requireContext());
        templeId = getArguments() != null ? getArguments().getInt("templeId", -1) : -1;

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        if (templeId != -1) {
            DarshanViewModel vm = new ViewModelProvider(this).get(DarshanViewModel.class);
            vm.getTempleById(templeId).observe(getViewLifecycleOwner(), this::populateTemple);
        }
    }

    private void populateTemple(TempleEntity temple) {
        if (temple == null) return;

        binding.tvTempleName.setText(temple.getName());
        binding.tvTempleNameHindi.setText(temple.getNameHindi());
        binding.tvTempleLocation.setText(temple.getLocation());
        binding.tvTempleDesc.setText(temple.getDescription());
        binding.tvTempleTimings.setText("Temple timings: " + temple.getTimings());
        binding.toolbar.setTitle(temple.getName());

        // Checklist data
        TempleChecklistData.TempleChecklist checklist =
                TempleChecklistData.getChecklist(temple.getName());

        binding.tvBestTimings.setText(checklist.bestTimings);
        binding.tvDressCode.setText(checklist.dressCode);

        // Populate checklist items
        binding.layoutChecklist.removeAllViews();
        for (String item : checklist.itemsToCarry) {
            CheckBox cb = new CheckBox(requireContext());
            cb.setText(item);
            cb.setTextSize(14);
            cb.setButtonTintList(
                    android.content.res.ColorStateList.valueOf(
                            requireContext().getColor(R.color.saffron_primary)));

            // Restore check state
            String key = "checklist_" + templeId + "_" + item;
            cb.setChecked(preferenceManager.getSharedPreferences().getBoolean(key, false));
            cb.setOnCheckedChangeListener((buttonView, isChecked) ->
                    preferenceManager.getSharedPreferences().edit()
                            .putBoolean(key, isChecked).apply());

            binding.layoutChecklist.addView(cb);
        }

        // Nearby temples
        if (checklist.nearbyTemples != null && !checklist.nearbyTemples.isEmpty()) {
            binding.cardNearby.setVisibility(View.VISIBLE);
            binding.chipGroupNearby.removeAllViews();
            for (String nearby : checklist.nearbyTemples) {
                Chip chip = new Chip(requireContext());
                chip.setText(nearby);
                chip.setTextSize(12);
                chip.setChipBackgroundColorResource(R.color.saffron_50);
                chip.setTextColor(requireContext().getColor(R.color.saffron_primary));
                binding.chipGroupNearby.addView(chip);
            }
        } else {
            binding.cardNearby.setVisibility(View.GONE);
        }

        // Visited state
        updateVisitedState(temple);
    }

    private void updateVisitedState(TempleEntity temple) {
        boolean visited = preferenceManager.getSharedPreferences()
                .getBoolean("temple_visited_" + templeId, false);

        if (visited) {
            binding.btnVisited.setText("Visited");
            binding.btnVisited.setIconResource(R.drawable.ic_check);
            String dateStr = preferenceManager.getSharedPreferences()
                    .getString("temple_visit_date_" + templeId, "");
            if (!dateStr.isEmpty()) {
                binding.tvVisitDate.setVisibility(View.VISIBLE);
                binding.tvVisitDate.setText("Visited on " + dateStr);
            }
        } else {
            binding.btnVisited.setText("Mark as Visited");
            binding.btnVisited.setIconResource(R.drawable.ic_check);
            binding.tvVisitDate.setVisibility(View.GONE);
        }

        binding.btnVisited.setOnClickListener(v -> {
            boolean nowVisited = !preferenceManager.getSharedPreferences()
                    .getBoolean("temple_visited_" + templeId, false);
            String date = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(new Date());
            preferenceManager.getSharedPreferences().edit()
                    .putBoolean("temple_visited_" + templeId, nowVisited)
                    .putString("temple_visit_date_" + templeId, nowVisited ? date : "")
                    .apply();
            updateVisitedState(temple);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
