package com.divyapath.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;
import com.divyapath.app.utils.PanchangInsightEngine;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class PanchangInsightAdapter extends RecyclerView.Adapter<PanchangInsightAdapter.InsightViewHolder> {

    private final List<PanchangInsightEngine.PanchangInsight> items = new ArrayList<>();

    public void submitList(List<PanchangInsightEngine.PanchangInsight> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InsightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_panchang_insight, parent, false);
        return new InsightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsightViewHolder holder, int position) {
        PanchangInsightEngine.PanchangInsight insight = items.get(position);

        holder.tvAction.setText(insight.actionText);
        holder.tvExplanation.setText(insight.explanation);
        holder.tvTime.setText(insight.timeWindow);
        holder.ivIcon.setImageResource(insight.iconRes);

        boolean isDo = insight.type == PanchangInsightEngine.TYPE_DO;
        int bgColor = holder.itemView.getContext().getResources().getColor(
                isDo ? R.color.insight_do_bg : R.color.insight_avoid_bg);
        int textColor = holder.itemView.getContext().getResources().getColor(
                isDo ? R.color.abhijit_green : R.color.rahukaal_red);
        int iconBgTint = holder.itemView.getContext().getResources().getColor(
                isDo ? R.color.abhijit_green : R.color.rahukaal_red);

        ((MaterialCardView) holder.itemView).setCardBackgroundColor(bgColor);
        holder.tvAction.setTextColor(textColor);
        holder.tvTime.setTextColor(textColor);
        holder.ivIcon.getBackground().setTint(iconBgTint);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class InsightViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivIcon;
        final TextView tvAction;
        final TextView tvExplanation;
        final TextView tvTime;

        InsightViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_insight_icon);
            tvAction = itemView.findViewById(R.id.tv_insight_action);
            tvExplanation = itemView.findViewById(R.id.tv_insight_explanation);
            tvTime = itemView.findViewById(R.id.tv_insight_time);
        }
    }
}
