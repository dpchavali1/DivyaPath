package com.divyapath.app.ui.darshan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;
import com.divyapath.app.data.local.entity.TempleEntity;
import com.divyapath.app.utils.PreferenceManager;

public class PilgrimageLogAdapter extends ListAdapter<TempleEntity, PilgrimageLogAdapter.ViewHolder> {

    private final PreferenceManager preferenceManager;

    public PilgrimageLogAdapter(PreferenceManager prefs) {
        super(DIFF_CALLBACK);
        this.preferenceManager = prefs;
    }

    private static final DiffUtil.ItemCallback<TempleEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull TempleEntity o, @NonNull TempleEntity n) {
                    return o.getId() == n.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull TempleEntity o, @NonNull TempleEntity n) {
                    return o.getName().equals(n.getName());
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pilgrimage_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TempleEntity temple = getItem(position);
        holder.name.setText(temple.getName());
        holder.location.setText(temple.getLocation());
        String date = preferenceManager.getSharedPreferences()
                .getString("temple_visit_date_" + temple.getId(), "");
        holder.date.setText(date);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView location;
        final TextView date;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_temple_name);
            location = itemView.findViewById(R.id.tv_temple_location);
            date = itemView.findViewById(R.id.tv_visit_date);
        }
    }
}
