package com.divyapath.app.ui.wallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentWallpaperBinding;
import com.divyapath.app.utils.AutoWallpaperWorker;
import com.divyapath.app.utils.NotificationScheduler;
import com.divyapath.app.utils.PreferenceManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class WallpaperFragment extends Fragment {

    private FragmentWallpaperBinding binding;
    private WallpaperAdapter adapter;
    private List<WallpaperItem> allWallpapers;
    private String currentCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWallpaperBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbarWallpaper.setNavigationOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).popBackStack());

        allWallpapers = loadWallpapers();
        adapter = new WallpaperAdapter(allWallpapers, this::onWallpaperClick);
        binding.rvWallpapers.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvWallpapers.setAdapter(adapter);

        setupCategoryFilter();
        setupAutoWallpaper();
    }

    private void setupCategoryFilter() {
        binding.chipGroupWallpaper.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_wp_all) currentCategory = "All";
            else if (id == R.id.chip_wp_ganesh) currentCategory = "Ganesh";
            else if (id == R.id.chip_wp_shiva) currentCategory = "Shiva";
            else if (id == R.id.chip_wp_krishna) currentCategory = "Krishna";
            else if (id == R.id.chip_wp_hanuman) currentCategory = "Hanuman";
            else if (id == R.id.chip_wp_durga) currentCategory = "Durga";
            else if (id == R.id.chip_wp_lakshmi) currentCategory = "Lakshmi";
            else if (id == R.id.chip_wp_vishnu) currentCategory = "Vishnu";
            else if (id == R.id.chip_wp_saraswati) currentCategory = "Saraswati";
            else if (id == R.id.chip_wp_ram) currentCategory = "Ram";
            else if (id == R.id.chip_wp_temples) currentCategory = "Temples";
            filterWallpapers();
        });
    }

    private void setupAutoWallpaper() {
        PreferenceManager pm = new PreferenceManager(requireContext());

        // Restore saved state
        boolean autoEnabled = pm.isAutoWallpaperEnabled();
        binding.switchAutoWallpaper.setChecked(autoEnabled);
        binding.layoutAutoInterval.setVisibility(autoEnabled ? View.VISIBLE : View.GONE);

        // Restore interval selection
        long savedInterval = pm.getAutoWallpaperIntervalMs();
        if (savedInterval <= 60 * 60 * 1000L) {
            binding.chipInterval1hr.setChecked(true);
        } else if (savedInterval <= 6 * 60 * 60 * 1000L) {
            binding.chipInterval6hr.setChecked(true);
        } else if (savedInterval <= 24 * 60 * 60 * 1000L) {
            binding.chipIntervalDaily.setChecked(true);
        } else {
            binding.chipIntervalWeekly.setChecked(true);
        }

        // Switch toggle
        binding.switchAutoWallpaper.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pm.setAutoWallpaperEnabled(isChecked);
            binding.layoutAutoInterval.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            NotificationScheduler.scheduleAutoWallpaper(requireContext());
        });

        // Interval chip selection
        binding.chipGroupInterval.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            long intervalMs;
            if (id == R.id.chip_interval_1hr) intervalMs = 60 * 60 * 1000L;
            else if (id == R.id.chip_interval_6hr) intervalMs = 6 * 60 * 60 * 1000L;
            else if (id == R.id.chip_interval_weekly) intervalMs = 7 * 24 * 60 * 60 * 1000L;
            else intervalMs = 24 * 60 * 60 * 1000L; // daily default

            pm.setAutoWallpaperIntervalMs(intervalMs);
            if (pm.isAutoWallpaperEnabled()) {
                NotificationScheduler.scheduleAutoWallpaper(requireContext());
            }
        });
    }

    private void filterWallpapers() {
        if ("All".equals(currentCategory)) {
            adapter.setWallpapers(allWallpapers);
        } else {
            List<WallpaperItem> filtered = new ArrayList<>();
            for (WallpaperItem w : allWallpapers) {
                if (currentCategory.equals(w.category)) filtered.add(w);
            }
            adapter.setWallpapers(filtered);
        }
    }

    private void onWallpaperClick(WallpaperItem wallpaper) {
        showSetWallpaperDialog(wallpaper);
    }

    private void showSetWallpaperDialog(WallpaperItem wallpaper) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(wallpaper.name)
                .setMessage("Set as wallpaper?")
                .setPositiveButton("Set Wallpaper", (d, w) -> {
                    Toast.makeText(requireContext(), "Downloading wallpaper...", Toast.LENGTH_SHORT).show();
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(wallpaper.imageUrl)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap bmp, @Nullable Transition<? super Bitmap> transition) {
                                    try {
                                        WallpaperManager.getInstance(requireContext()).setBitmap(bmp);
                                        Toast.makeText(requireContext(), "Wallpaper set!", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(requireContext(), "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable android.graphics.drawable.Drawable placeholder) {}

                                @Override
                                public void onLoadFailed(@Nullable android.graphics.drawable.Drawable errorDrawable) {
                                    if (getContext() != null) {
                                        Toast.makeText(getContext(), "Failed to download wallpaper", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private List<WallpaperItem> loadWallpapers() {
        List<WallpaperItem> items = new ArrayList<>();
        List<String[]> all = AutoWallpaperWorker.getAllWallpapers();
        for (String[] entry : all) {
            items.add(new WallpaperItem(entry[0], entry[1], entry[2]));
        }
        return items;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Inner model class
    static class WallpaperItem {
        final String name, category, imageUrl;

        WallpaperItem(String name, String category, String imageUrl) {
            this.name = name;
            this.category = category;
            this.imageUrl = imageUrl;
        }
    }

    // Inner adapter class
    static class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.VH> {
        private List<WallpaperItem> items;
        private final OnWallpaperClick listener;

        interface OnWallpaperClick { void onClick(WallpaperItem item); }

        WallpaperAdapter(List<WallpaperItem> items, OnWallpaperClick listener) {
            this.items = items; this.listener = listener;
        }

        void setWallpapers(List<WallpaperItem> items) {
            this.items = items; notifyDataSetChanged();
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_wallpaper, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            WallpaperItem item = items.get(position);
            holder.bind(item, listener);
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            VH(@NonNull View itemView) { super(itemView); }

            void bind(WallpaperItem item, OnWallpaperClick listener) {
                android.widget.TextView tvName = itemView.findViewById(R.id.tv_wp_name);
                ImageView ivPreview = itemView.findViewById(R.id.iv_wallpaper_preview);

                tvName.setText(item.name);

                Glide.with(itemView.getContext())
                        .load(item.imageUrl)
                        .placeholder(R.drawable.ic_om_symbol)
                        .centerCrop()
                        .into(ivPreview);

                itemView.setOnClickListener(v -> listener.onClick(item));
            }
        }
    }
}
