package com.divyapath.app.ui.aarti;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentAartiListBinding;
import com.divyapath.app.ui.adapters.DeityGridAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AartiListFragment extends Fragment {
    private FragmentAartiListBinding binding;
    private AartiViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        binding = FragmentAartiListBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        vm = new ViewModelProvider(this).get(AartiViewModel.class);

        DeityGridAdapter adapter = new DeityGridAdapter(deity -> {
            Bundle args = new Bundle();
            args.putInt("contentId", deity.getId());
            args.putBoolean("isDeityId", true);
            Navigation.findNavController(v).navigate(R.id.aartiDetailFragment, args);
        });

        binding.rvDeityGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvDeityGrid.setAdapter(adapter);
        vm.getDeities().observe(getViewLifecycleOwner(), adapter::submitList);

        // Load banner ad
        AdView adView = binding.adBannerAarti;
        adView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
