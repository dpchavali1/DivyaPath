package com.divyapath.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;
import com.divyapath.app.data.local.entity.AartiEntity;
import com.divyapath.app.utils.DeityIconMapper;

public class AartiHorizontalAdapter extends ListAdapter<AartiEntity, AartiHorizontalAdapter.ViewHolder> {

    private final OnAartiClickListener listener;

    public interface OnAartiClickListener {
        void onAartiClick(AartiEntity aarti);
    }

    public AartiHorizontalAdapter(OnAartiClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<AartiEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<AartiEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull AartiEntity oldItem, @NonNull AartiEntity newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull AartiEntity oldItem, @NonNull AartiEntity newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle());
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_aarti_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AartiEntity aarti = getItem(position);
        holder.bind(aarti, listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivDeity;
        private final TextView tvTitle;
        private final TextView tvSubtitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDeity = itemView.findViewById(R.id.iv_aarti_deity);
            tvTitle = itemView.findViewById(R.id.tv_aarti_title);
            tvSubtitle = itemView.findViewById(R.id.tv_aarti_subtitle);
        }

        void bind(AartiEntity aarti, OnAartiClickListener listener) {
            tvTitle.setText(aarti.getTitleHindi() != null ? aarti.getTitleHindi() : aarti.getTitle());
            tvSubtitle.setText(aarti.getTitle());
            ivDeity.setImageResource(DeityIconMapper.getIconForDeityId(aarti.getDeityId()));
            itemView.setOnClickListener(v -> listener.onAartiClick(aarti));
        }
    }
}
