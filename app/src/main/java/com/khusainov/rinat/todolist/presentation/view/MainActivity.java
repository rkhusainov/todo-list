package com.khusainov.rinat.todolist.presentation.view;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.khusainov.rinat.todolist.R;
import com.khusainov.rinat.todolist.data.model.Task;
import com.khusainov.rinat.todolist.data.repository.TaskRepository;
import com.khusainov.rinat.todolist.presentation.presenter.TaskPresenter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskListener, TasksView {

    private FloatingActionButton mAddFab;
    private RecyclerView mRecyclerView;
    private TaskAdapter mTaskAdapter;

    private TaskPresenter mTaskPresenter;
    private TaskRepository mTaskRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTaskRepository = new TaskRepository(this);
        mTaskPresenter = new TaskPresenter(this, mTaskRepository);
        mTaskPresenter.getTasks();

        initViews();
    }

    private void initViews() {
        mAddFab = findViewById(R.id.fab_add);
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTaskAdapter = new TaskAdapter(this);
        mRecyclerView.setAdapter(mTaskAdapter);

        mAddFab.setOnClickListener(view -> openDialog());
    }

    // Показ диалога и запись в БД
    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);

        builder.setView(editText);
        builder.setTitle(getResources().getString(R.string.enter_task_name));
        builder.setPositiveButton(getResources().getString(R.string.ok), (dialogInterface, i) -> {

            mTaskPresenter.writeTask(editText.getText().toString());

            // Пауза для того чтобы успело добавиться
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Обновляем данные на экране
            mTaskPresenter.getTasks();
        });
        builder.show();
    }

    // Update задачи в БД
    @Override
    public void checkDone(Task task, boolean done) {

        mTaskPresenter.updateTask(task, done);
    }

    // Delete задачи в БД
    @Override
    public void deleteTask(Task task) {

        mTaskPresenter.deleteTask(task);

        // Обновляем данные на экране
        mTaskPresenter.getTasks();
    }

    // Обновление адаптера
    @Override
    public void showTasks(List<Task> tasks) {
        mTaskAdapter.addData(tasks);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTaskPresenter != null) {
            mTaskPresenter.dispatchDetach();
        }
    }
}
