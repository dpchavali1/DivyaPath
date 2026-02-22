package com.divyapath.app.ui.shraddha;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;
import com.divyapath.app.data.local.entity.ShraddhaEntity;

public class ShraddhaListAdapter extends ListAdapter<ShraddhaEntity, ShraddhaListAdapter.ShraddhaViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ShraddhaEntity item);
    }

    private OnItemClickListener listener;

    public ShraddhaListAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ShraddhaEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ShraddhaEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull ShraddhaEntity a, @NonNull ShraddhaEntity b) {
                    return a.getId() == b.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ShraddhaEntity a, @NonNull ShraddhaEntity b) {
                    return a.getName() != null && a.getName().equals(b.getName())
                            && a.getTithiIndex() == b.getTithiIndex()
                            && a.getLunarMonth() == b.getLunarMonth();
                }
            };

    @NonNull
    @Override
    public ShraddhaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shraddha, parent, false);
        return new ShraddhaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShraddhaViewHolder holder, int position) {
        ShraddhaEntity item = getItem(position);

        holder.tvName.setText(item.getName());
        holder.tvRelationship.setText(item.getRelationship());
        holder.tvTithi.setText(
                ShraddhaEntity.getPakshaName(item.getTithiIndex()) + " " +
                        ShraddhaEntity.getTithiName(item.getTithiIndex()));
        holder.tvMonth.setText(ShraddhaEntity.getLunarMonthName(item.getLunarMonth()));

        if (item.getNotes() != null && !item.getNotes().isEmpty()) {
            holder.tvNotes.setVisibility(View.VISIBLE);
            holder.tvNotes.setText(item.getNotes());
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    static class ShraddhaViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvRelationship;
        final TextView tvTithi;
        final TextView tvMonth;
        final TextView tvNotes;

        ShraddhaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_shraddha_name);
            tvRelationship = itemView.findViewById(R.id.tv_shraddha_relationship);
            tvTithi = itemView.findViewById(R.id.tv_shraddha_tithi);
            tvMonth = itemView.findViewById(R.id.tv_shraddha_month);
            tvNotes = itemView.findViewById(R.id.tv_shraddha_notes);
        }
    }
}
