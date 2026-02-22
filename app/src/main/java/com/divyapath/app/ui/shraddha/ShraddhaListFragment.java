package com.divyapath.app.ui.shraddha;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentShraddhaListBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ShraddhaListFragment extends Fragment {

    private FragmentShraddhaListBinding binding;
    private ShraddhaViewModel viewModel;
    private ShraddhaListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShraddhaListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ShraddhaViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        setupRecyclerView();
        observeViewModel();

        binding.fabAdd.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_shraddhaList_to_shraddhaForm));
    }

    private void setupRecyclerView() {
        adapter = new ShraddhaListAdapter();
        binding.rvShraddha.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvShraddha.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Bundle args = new Bundle();
            args.putInt("shraddhaId", item.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_shraddhaList_to_shraddhaForm, args);
        });

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                var item = adapter.getCurrentList().get(pos);
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Entry")
                        .setMessage("Remove " + item.getName() + " from your Shraddha calendar?")
                        .setPositiveButton("Delete", (d, w) -> viewModel.delete(item))
                        .setNegativeButton("Cancel", (d, w) -> adapter.notifyItemChanged(pos))
                        .setOnCancelListener(d -> adapter.notifyItemChanged(pos))
                        .show();
            }
        }).attachToRecyclerView(binding.rvShraddha);
    }

    private void observeViewModel() {
        viewModel.getAllShraddha().observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                binding.rvShraddha.setVisibility(View.VISIBLE);
                binding.tvEmpty.setVisibility(View.GONE);
                adapter.submitList(list);
            } else {
                binding.rvShraddha.setVisibility(View.GONE);
                binding.tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
