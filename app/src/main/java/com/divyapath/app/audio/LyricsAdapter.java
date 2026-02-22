package com.divyapath.app.audio;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;

/**
 * RecyclerView adapter for synced lyrics display.
 * Active line is highlighted with saffron color and bold text.
 */
public class LyricsAdapter extends ListAdapter<LyricsSyncManager.LyricsLine, LyricsAdapter.LyricsViewHolder> {

    private OnLineClickListener clickListener;

    public interface OnLineClickListener {
        void onLineClicked(int lineIndex);
    }

    public LyricsAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnLineClickListener(OnLineClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public LyricsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lyrics_line, parent, false);
        return new LyricsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LyricsViewHolder holder, int position) {
        LyricsSyncManager.LyricsLine line = getItem(position);
        holder.bind(line);
    }

    class LyricsViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLine;

        LyricsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLine = itemView.findViewById(R.id.tv_lyrics_line);
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        clickListener.onLineClicked(pos);
                    }
                }
            });
        }

        void bind(LyricsSyncManager.LyricsLine line) {
            tvLine.setText(line.getText());

            if (line.isHeader()) {
                tvLine.setTextSize(13);
                tvLine.setTypeface(null, Typeface.ITALIC);
                tvLine.setTextColor(itemView.getContext().getColor(R.color.text_hint));
                tvLine.setAlpha(0.8f);
            } else if (line.isActive()) {
                tvLine.setTextSize(16);
                tvLine.setTypeface(null, Typeface.BOLD);
                tvLine.setTextColor(itemView.getContext().getColor(R.color.saffron_primary));
                tvLine.setAlpha(1.0f);
            } else {
                tvLine.setTextSize(14);
                tvLine.setTypeface(null, Typeface.NORMAL);
                tvLine.setTextColor(itemView.getContext().getColor(R.color.text_primary));
                tvLine.setAlpha(0.7f);
            }
        }
    }

    private static final DiffUtil.ItemCallback<LyricsSyncManager.LyricsLine> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<LyricsSyncManager.LyricsLine>() {
                @Override
                public boolean areItemsTheSame(@NonNull LyricsSyncManager.LyricsLine oldItem,
                                               @NonNull LyricsSyncManager.LyricsLine newItem) {
                    return oldItem.getIndex() == newItem.getIndex();
                }

                @Override
                public boolean areContentsTheSame(@NonNull LyricsSyncManager.LyricsLine oldItem,
                                                  @NonNull LyricsSyncManager.LyricsLine newItem) {
                    return oldItem.getText().equals(newItem.getText())
                            && oldItem.isActive() == newItem.isActive();
                }
            };
}
