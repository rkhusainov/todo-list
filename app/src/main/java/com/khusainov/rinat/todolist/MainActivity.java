package com.khusainov.rinat.todolist;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.single.SingleFromCallable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskListener {

    private static final String TAG = "MainActivity";

    private FloatingActionButton mAddFab;
    private RecyclerView mRecyclerView;
    private TaskAdapter mTaskAdapter;
    private SQLiteDatabase mDatabase;
    private List<Task> mTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        getTasks();
    }

    private void initViews() {

        mAddFab = findViewById(R.id.fab_add);
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTaskAdapter = new TaskAdapter(this);
        mRecyclerView.setAdapter(mTaskAdapter);

        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);

        builder.setView(editText);
        builder.setTitle("Enter Task Name");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                writeTask(editText.getText().toString());

                // Пауза для того чтобы успело удалиться
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                getTasks();
            }
        });
        builder.show();
    }

    @SuppressLint("CheckResult")
    private void getTasks() {
        SingleFromCallable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                selectData();
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mTaskAdapter.addData(mTasks);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void selectData() {

        // Получаем базу данных для чтения
        mDatabase = new TaskDbHelper(this).getWritableDatabase();

        // Определяем данные по колонкам, которые необходимо запросить
        String[] projection = {
                BaseColumns._ID,
                TasksDbSchema.TasksTable.Cols.TITLE,
                TasksDbSchema.TasksTable.Cols.DONE
        };

        // Получаем объект типа курсор
        Cursor cursor = mDatabase.query(
                TasksDbSchema.TasksTable.NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        mTasks.clear();

        try {
            while (cursor.moveToNext()) {
                Task task = new Task();
                String title = cursor.getString(cursor.getColumnIndex(TasksDbSchema.TasksTable.Cols.TITLE));
                task.setId(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
                task.setTitle(title);

                int done = cursor.getInt(cursor.getColumnIndex(TasksDbSchema.TasksTable.Cols.DONE));
                task.setDone(done == 1);
                mTasks.add(task);
                Log.d(TAG, "selectData: " + task.isDone());
            }
        } finally {
            cursor.close();
        }
    }

    private void writeTask(String name) {
        SingleFromCallable.fromCallable(() -> {
            MainActivity.this.insertData(name);
            return true;
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void insertData(String taskName) {
        mDatabase = new TaskDbHelper(this).getWritableDatabase();
        // Получаем базу данных для записи
        mDatabase = new TaskDbHelper(this).getWritableDatabase();

        // Создаем пары ключ-значения для добавления строки
        ContentValues values = new ContentValues();
        values.put(TasksDbSchema.TasksTable.Cols.TITLE, taskName);
        values.put(TasksDbSchema.TasksTable.Cols.DONE, 0);
        mDatabase.insert(TasksDbSchema.TasksTable.NAME, null, values);
    }


    @Override
    public void deleteTask(Task task) {
        mDatabase = new TaskDbHelper(this).getWritableDatabase();
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};
        mDatabase.delete(
                TasksDbSchema.TasksTable.NAME,
                selection,
                selectionArgs);

        // Пауза для того чтобы успело удалиться
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getTasks();
    }

    @Override
    public void checkDone(Task task, boolean done) {
        ContentValues values = new ContentValues();
        values.put(BaseColumns._ID,task.getId());
        values.put(TasksDbSchema.TasksTable.Cols.TITLE, task.getTitle());

        int isDone = done ? 1 : 0;
        values.put(TasksDbSchema.TasksTable.Cols.DONE, isDone);
        Log.d(TAG, "updateDb: " + task.isDone() + ", done: " + isDone);

        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};

        mDatabase.update(
                TasksDbSchema.TasksTable.NAME,
                values,
                selection,
                selectionArgs);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }
}
