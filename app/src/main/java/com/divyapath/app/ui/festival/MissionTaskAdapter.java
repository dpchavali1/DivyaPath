package com.divyapath.app.ui.festival;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;
import com.divyapath.app.utils.FestivalMissionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MissionTaskAdapter extends RecyclerView.Adapter<MissionTaskAdapter.TaskViewHolder> {

    public interface OnTaskCheckedListener {
        void onTaskChecked(int taskIndex, boolean checked);
    }

    private final List<FestivalMissionData.MissionTask> tasks = new ArrayList<>();
    private Set<Integer> completedIndices;
    private OnTaskCheckedListener listener;

    public void setListener(OnTaskCheckedListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FestivalMissionData.MissionTask> newTasks, Set<Integer> completed) {
        tasks.clear();
        if (newTasks != null) tasks.addAll(newTasks);
        this.completedIndices = completed;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mission_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        FestivalMissionData.MissionTask task = tasks.get(position);
        boolean isCompleted = completedIndices != null && completedIndices.contains(position);

        holder.tvTitle.setText(task.title);
        holder.tvDescription.setText(task.description);
        holder.tvCategory.setText(task.category);

        holder.cbTask.setOnCheckedChangeListener(null);
        holder.cbTask.setChecked(isCompleted);

        if (isCompleted) {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setAlpha(0.5f);
        } else {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setAlpha(1f);
        }

        holder.cbTask.setOnCheckedChangeListener((button, checked) -> {
            if (listener != null) listener.onTaskChecked(holder.getAdapterPosition(), checked);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        final CheckBox cbTask;
        final TextView tvTitle;
        final TextView tvDescription;
        final TextView tvCategory;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbTask = itemView.findViewById(R.id.cb_task);
            tvTitle = itemView.findViewById(R.id.tv_task_title);
            tvDescription = itemView.findViewById(R.id.tv_task_description);
            tvCategory = itemView.findViewById(R.id.tv_task_category);
        }
    }
}
