package com.divyapath.app.ui.shraddha;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.divyapath.app.data.local.entity.ShraddhaEntity;
import com.divyapath.app.databinding.FragmentShraddhaFormBinding;

import java.util.Arrays;

public class ShraddhaFormFragment extends Fragment {

    private FragmentShraddhaFormBinding binding;
    private ShraddhaViewModel viewModel;
    private int editId = -1;
    private int selectedPakshaOffset = 0; // 0 for Shukla, 15 for Krishna
    private int selectedTithiInPaksha = 0; // 0-14

    private static final String[] SHUKLA_TITHIS = {
            "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
            "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
            "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Purnima"
    };
    private static final String[] KRISHNA_TITHIS = {
            "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
            "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
            "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Amavasya"
    };
    private static final String[] MONTHS = {
            "Chaitra", "Vaishakha", "Jyeshtha", "Ashadha",
            "Shravana", "Bhadrapada", "Ashwin", "Kartik",
            "Margashirsha", "Pausha", "Magha", "Phalguna"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShraddhaFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ShraddhaViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        setupDropdowns();

        // Check if editing an existing entry
        if (getArguments() != null) {
            editId = getArguments().getInt("shraddhaId", -1);
        }

        if (editId > 0) {
            binding.toolbar.setTitle("Edit Remembrance");
            viewModel.getShraddhaById(editId).observe(getViewLifecycleOwner(), entity -> {
                if (entity != null) {
                    binding.etName.setText(entity.getName());
                    binding.dropdownRelationship.setText(entity.getRelationship(), false);

                    boolean isKrishna = entity.getTithiIndex() >= 15;
                    selectedPakshaOffset = isKrishna ? 15 : 0;
                    binding.dropdownPaksha.setText(isKrishna ? "Krishna Paksha" : "Shukla Paksha", false);

                    selectedTithiInPaksha = entity.getTithiIndex() % 15;
                    String[] tithis = isKrishna ? KRISHNA_TITHIS : SHUKLA_TITHIS;
                    updateTithiDropdown(tithis);
                    if (selectedTithiInPaksha < tithis.length) {
                        binding.dropdownTithi.setText(tithis[selectedTithiInPaksha], false);
                    }

                    int monthIdx = entity.getLunarMonth() - 1;
                    if (monthIdx >= 0 && monthIdx < MONTHS.length) {
                        binding.dropdownMonth.setText(MONTHS[monthIdx], false);
                    }

                    binding.etNotes.setText(entity.getNotes());
                }
            });
        }

        binding.btnSave.setOnClickListener(v -> saveEntry());
    }

    private void setupDropdowns() {
        // Relationship
        ArrayAdapter<String> relAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, ShraddhaEntity.getRelationships());
        binding.dropdownRelationship.setAdapter(relAdapter);

        // Paksha
        String[] pakshas = {"Shukla Paksha", "Krishna Paksha"};
        ArrayAdapter<String> pakshaAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, pakshas);
        binding.dropdownPaksha.setAdapter(pakshaAdapter);
        binding.dropdownPaksha.setText("Shukla Paksha", false);

        binding.dropdownPaksha.setOnItemClickListener((parent, v, pos, id) -> {
            selectedPakshaOffset = pos == 0 ? 0 : 15;
            String[] tithis = pos == 0 ? SHUKLA_TITHIS : KRISHNA_TITHIS;
            updateTithiDropdown(tithis);
            binding.dropdownTithi.setText("", false);
        });

        // Tithi (default Shukla)
        updateTithiDropdown(SHUKLA_TITHIS);

        binding.dropdownTithi.setOnItemClickListener((parent, v, pos, id) ->
                selectedTithiInPaksha = pos);

        // Month
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, MONTHS);
        binding.dropdownMonth.setAdapter(monthAdapter);
    }

    private void updateTithiDropdown(String[] tithis) {
        ArrayAdapter<String> tithiAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, tithis);
        binding.dropdownTithi.setAdapter(tithiAdapter);
    }

    private void saveEntry() {
        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
        String relationship = binding.dropdownRelationship.getText().toString().trim();
        String monthText = binding.dropdownMonth.getText().toString().trim();
        String notes = binding.etNotes.getText() != null ? binding.etNotes.getText().toString().trim() : "";

        if (name.isEmpty()) {
            binding.tilName.setError("Please enter a name");
            return;
        }
        binding.tilName.setError(null);

        int tithiIndex = selectedPakshaOffset + selectedTithiInPaksha;
        int lunarMonth = Arrays.asList(MONTHS).indexOf(monthText) + 1;
        if (lunarMonth <= 0) lunarMonth = 1;

        ShraddhaEntity entity = new ShraddhaEntity();
        entity.setName(name);
        entity.setRelationship(relationship);
        entity.setTithiIndex(tithiIndex);
        entity.setLunarMonth(lunarMonth);
        entity.setAnnual(true);
        entity.setNotes(notes);
        entity.setCreatedAt(System.currentTimeMillis());

        if (editId > 0) {
            entity.setId(editId);
            viewModel.update(entity);
        } else {
            viewModel.insert(entity);
        }

        Navigation.findNavController(requireView()).navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
