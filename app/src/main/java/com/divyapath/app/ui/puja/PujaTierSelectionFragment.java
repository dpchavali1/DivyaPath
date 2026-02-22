package com.divyapath.app.ui.puja;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentPujaTierSelectionBinding;
import com.divyapath.app.utils.PujaFlowData;

import java.util.List;

public class PujaTierSelectionFragment extends Fragment {

    private FragmentPujaTierSelectionBinding binding;
    private String selectedDeity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPujaTierSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        // Deity dropdown
        List<String> deities = PujaFlowData.getDeityNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, deities);
        AutoCompleteTextView dropdown = binding.dropdownDeity;
        dropdown.setAdapter(adapter);

        // Default selection
        selectedDeity = deities.get(0);
        dropdown.setText(selectedDeity, false);
        updateStepCounts();

        dropdown.setOnItemClickListener((parent, v, pos, id) -> {
            selectedDeity = deities.get(pos);
            updateStepCounts();
        });

        // Tier click listeners
        binding.cardTierQuick.setOnClickListener(v -> navigateToFlow(PujaFlowData.TIER_QUICK));
        binding.cardTierStandard.setOnClickListener(v -> navigateToFlow(PujaFlowData.TIER_STANDARD));
        binding.cardTierFull.setOnClickListener(v -> navigateToFlow(PujaFlowData.TIER_FULL));
    }

    private void updateStepCounts() {
        int quick = PujaFlowData.getSteps(selectedDeity, PujaFlowData.TIER_QUICK).size();
        int standard = PujaFlowData.getSteps(selectedDeity, PujaFlowData.TIER_STANDARD).size();
        int full = PujaFlowData.getSteps(selectedDeity, PujaFlowData.TIER_FULL).size();

        binding.tvQuickSteps.setText(quick + " steps");
        binding.tvStandardSteps.setText(standard + " steps");
        binding.tvFullSteps.setText(full + " steps");
    }

    private void navigateToFlow(String tier) {
        Bundle args = new Bundle();
        args.putString("deityName", selectedDeity);
        args.putString("tier", tier);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_pujaTierSelection_to_pujaFlow, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
