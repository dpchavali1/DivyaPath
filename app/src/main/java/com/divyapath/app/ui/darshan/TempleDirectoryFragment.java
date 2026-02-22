package com.divyapath.app.ui.darshan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;
import com.divyapath.app.utils.PreferenceManager;

public class TempleDirectoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        return i.inflate(R.layout.fragment_temple_directory, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        DarshanViewModel vm = new ViewModelProvider(this).get(DarshanViewModel.class);
        PreferenceManager prefs = new PreferenceManager(requireContext());

        TempleAdapter adapter = new TempleAdapter(false, t -> {
            if (t.getYoutubeUrl() != null && !t.getYoutubeUrl().isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(t.getYoutubeUrl())));
            }
        });
        adapter.setPreferences(prefs.getSharedPreferences());
        adapter.setOnCardClickListener(t -> {
            Bundle args = new Bundle();
            args.putInt("templeId", t.getId());
            try {
                // Use parent fragment's view to find NavController (child of ViewPager2)
                NavController nav = Navigation.findNavController(requireParentFragment().requireView());
                nav.navigate(R.id.templeDetailFragment, args);
            } catch (Exception e) {
                // Fallback: try Activity-level NavController
                try {
                    NavController nav = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    nav.navigate(R.id.templeDetailFragment, args);
                } catch (Exception ignored) {}
            }
        });

        RecyclerView rv = v.findViewById(R.id.rv_all_temples);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);
        vm.getAllTemples().observe(getViewLifecycleOwner(), adapter::submitList);
    }
}
