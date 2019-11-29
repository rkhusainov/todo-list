package com.khusainov.rinat.todolist.presentation.presenter;

import android.annotation.SuppressLint;

import com.khusainov.rinat.todolist.data.model.Task;
import com.khusainov.rinat.todolist.data.repository.TaskRepository;
import com.khusainov.rinat.todolist.presentation.view.TasksView;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.single.SingleFromCallable;
import io.reactivex.schedulers.Schedulers;

public class TaskPresenter {

    private TasksView mTasksView;
    private TaskRepository mTaskRepository;
    private ExecutorService mExecutor;


    public TaskPresenter(TasksView tasksView, TaskRepository taskRepository) {
        mTasksView = tasksView;
        mTaskRepository = taskRepository;
        initExecutorService();
    }

    private void initExecutorService() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    @SuppressLint("CheckResult")
    public void getTasks() {

        SingleFromCallable.fromCallable(new Callable<List<Task>>() {
            @Override
            public List<Task> call() throws Exception {
                return mTaskRepository.getData();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<List<Task>>() {
                            @Override
                            public void accept(List<Task> tasks) throws Exception {
                                mTasksView.showTasks(tasks);
                            }
                        }
                );
    }

    public void writeTask(String name) {
        SingleFromCallable.fromCallable(() -> {
            mTaskRepository.insertData(name);
            return true;
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void updateTask(Task task, boolean done) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mTaskRepository.updateData(task, done);
            }
        });
    }

    public void deleteTask(Task task) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mTaskRepository.deleteData(task);
            }
        });
    }

    public void dispatchDetach() {
        if (mExecutor != null) {
            mExecutor.shutdown();
        }
    }
}
