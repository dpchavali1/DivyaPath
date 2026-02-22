package com.divyapath.app.ui.bhajan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;
import com.divyapath.app.data.local.entity.BhajanEntity;
import com.divyapath.app.utils.DeityIconMapper;

public class BhajanListAdapter extends ListAdapter<BhajanEntity, BhajanListAdapter.VH> {

    public interface OnBhajanClickListener {
        void onBhajanClick(BhajanEntity bhajan);
    }

    private final OnBhajanClickListener listener;

    public BhajanListAdapter(OnBhajanClickListener l) {
        super(new DiffUtil.ItemCallback<BhajanEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull BhajanEntity o, @NonNull BhajanEntity n) {
                return o.getId() == n.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull BhajanEntity o, @NonNull BhajanEntity n) {
                return o.getTitle().equals(n.getTitle());
            }
        });
        this.listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_bhajan, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        BhajanEntity b = getItem(pos);
        h.title.setText(b.getTitle());
        h.titleHindi.setText(b.getTitleHindi());
        if (b.getCategory() != null && !b.getCategory().isEmpty()) {
            h.category.setText(b.getCategory());
            h.category.setVisibility(View.VISIBLE);
        } else {
            h.category.setVisibility(View.GONE);
        }
        h.icon.setImageResource(DeityIconMapper.getIconForDeityId(b.getDeityId()));
        h.itemView.setOnClickListener(v -> listener.onBhajanClick(b));
        h.playBtn.setOnClickListener(v -> listener.onBhajanClick(b));
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, titleHindi, category;
        ImageButton playBtn;

        VH(View v) {
            super(v);
            icon = v.findViewById(R.id.iv_bhajan_icon);
            title = v.findViewById(R.id.tv_bhajan_title);
            titleHindi = v.findViewById(R.id.tv_bhajan_title_hindi);
            category = v.findViewById(R.id.tv_bhajan_category);
            playBtn = v.findViewById(R.id.btn_bhajan_play);
        }
    }
}
