package com.divyapath.app.ui.stotra;

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
import com.divyapath.app.data.local.entity.StotraEntity;
import com.divyapath.app.utils.DeityIconMapper;

public class StotraListAdapter extends ListAdapter<StotraEntity, StotraListAdapter.VH> {

    public interface OnStotraClickListener {
        void onStotraClick(StotraEntity stotra);
    }

    private final OnStotraClickListener listener;

    public StotraListAdapter(OnStotraClickListener l) {
        super(new DiffUtil.ItemCallback<StotraEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull StotraEntity o, @NonNull StotraEntity n) {
                return o.getId() == n.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull StotraEntity o, @NonNull StotraEntity n) {
                return o.getTitle().equals(n.getTitle());
            }
        });
        this.listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_stotra, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        StotraEntity s = getItem(pos);
        h.title.setText(s.getTitle());
        h.titleHindi.setText(s.getTitleHindi());
        h.verseCount.setText(s.getVerseCount() + " verses");
        int mins = s.getDuration() / 60;
        if (mins > 0) {
            h.duration.setText(mins + " min");
            h.duration.setVisibility(View.VISIBLE);
        } else {
            h.duration.setVisibility(View.GONE);
        }
        h.icon.setImageResource(DeityIconMapper.getIconForDeityId(s.getDeityId()));
        h.itemView.setOnClickListener(v -> listener.onStotraClick(s));
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, titleHindi, verseCount, duration;

        VH(View v) {
            super(v);
            icon = v.findViewById(R.id.iv_stotra_icon);
            title = v.findViewById(R.id.tv_stotra_title);
            titleHindi = v.findViewById(R.id.tv_stotra_title_hindi);
            verseCount = v.findViewById(R.id.tv_stotra_verse_count);
            duration = v.findViewById(R.id.tv_stotra_duration);
        }
    }
}
