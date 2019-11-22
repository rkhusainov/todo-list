package com.khusainov.rinat.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    private List<Task> mTasks = new ArrayList<>();

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_item, parent, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Task task = mTasks.get(position);
        holder.bind(task);
    }

    public void addData(List<Task> tasks) {
        mTasks.clear();
        mTasks.addAll(tasks);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    class TaskHolder extends RecyclerView.ViewHolder {

        private TextView mTaskNameTextView;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            mTaskNameTextView = itemView.findViewById(R.id.tv_task_name);
        }

        private void bind(Task task) {
            mTaskNameTextView.setText(task.getTitle());
        }
    }
}
