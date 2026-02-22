package com.divyapath.app.ui.bhajan;

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
import com.divyapath.app.databinding.FragmentBhajanListBinding;
import com.google.android.material.tabs.TabLayout;

public class BhajanListFragment extends Fragment {

    private FragmentBhajanListBinding binding;
    private static final String[] CATEGORIES = {"All", "Krishna", "Devi", "Shiva", "Ram", "Kabir", "Mirabai"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        binding = FragmentBhajanListBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        BhajanViewModel vm = new ViewModelProvider(this).get(BhajanViewModel.class);

        BhajanListAdapter adapter = new BhajanListAdapter(bhajan -> {
            Bundle args = new Bundle();
            args.putInt("contentId", bhajan.getId());
            Navigation.findNavController(v).navigate(R.id.bhajanDetailFragment, args);
        });

        binding.rvBhajanList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBhajanList.setAdapter(adapter);

        // Setup category tabs
        for (String category : CATEGORIES) {
            binding.tabBhajanCategories.addTab(binding.tabBhajanCategories.newTab().setText(category));
        }

        binding.tabBhajanCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = tab.getText() != null ? tab.getText().toString() : "All";
                vm.setCategory(category);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        vm.getFilteredBhajans().observe(getViewLifecycleOwner(), adapter::submitList);

        binding.toolbarBhajan.setNavigationOnClickListener(x ->
                Navigation.findNavController(v).popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
