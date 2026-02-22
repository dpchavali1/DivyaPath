package com.divyapath.app.ui.seva;

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
import com.divyapath.app.utils.SevaData;

public class SevaBrowseAdapter extends ListAdapter<SevaData.SevaItem, SevaBrowseAdapter.ViewHolder> {

    public SevaBrowseAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<SevaData.SevaItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull SevaData.SevaItem oldItem,
                                               @NonNull SevaData.SevaItem newItem) {
                    return oldItem.title.equals(newItem.title);
                }

                @Override
                public boolean areContentsTheSame(@NonNull SevaData.SevaItem oldItem,
                                                  @NonNull SevaData.SevaItem newItem) {
                    return oldItem.title.equals(newItem.title)
                            && oldItem.category.equals(newItem.category);
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seva_browse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SevaData.SevaItem item = getItem(position);
        holder.title.setText(item.title);
        holder.category.setText(item.category);
        holder.icon.setImageResource(item.iconRes);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView category;
        final ImageView icon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_seva_title);
            category = itemView.findViewById(R.id.tv_seva_category);
            icon = itemView.findViewById(R.id.iv_seva_icon);
        }
    }
}
