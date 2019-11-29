package com.khusainov.rinat.todolist.presentation.view;

import com.khusainov.rinat.todolist.data.model.Task;

import java.util.List;

public interface TasksView {
    void showTasks(List<Task> tasks);
}
