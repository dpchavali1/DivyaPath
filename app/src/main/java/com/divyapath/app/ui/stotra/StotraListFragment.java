package com.divyapath.app.ui.stotra;

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
import com.divyapath.app.databinding.FragmentStotraListBinding;

public class StotraListFragment extends Fragment {

    private FragmentStotraListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        binding = FragmentStotraListBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        StotraViewModel vm = new ViewModelProvider(this).get(StotraViewModel.class);

        StotraListAdapter adapter = new StotraListAdapter(stotra -> {
            Bundle args = new Bundle();
            args.putInt("contentId", stotra.getId());
            Navigation.findNavController(v).navigate(R.id.stotraDetailFragment, args);
        });

        binding.rvStotraList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStotraList.setAdapter(adapter);

        vm.getAllStotras().observe(getViewLifecycleOwner(), adapter::submitList);

        binding.toolbarStotra.setNavigationOnClickListener(x ->
                Navigation.findNavController(v).popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
