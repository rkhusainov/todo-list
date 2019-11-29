package com.khusainov.rinat.todolist.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.khusainov.rinat.todolist.data.database.TaskDbHelper;
import com.khusainov.rinat.todolist.data.database.TasksDbSchema;
import com.khusainov.rinat.todolist.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    private Context mContext;

    public TaskRepository(Context context) {
        mContext = context;
    }

    public List<Task> getData() {

        SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();

        // Определяем данные по колонкам, которые необходимо запросить
        String[] projection = {
                BaseColumns._ID,
                TasksDbSchema.TasksTable.Cols.TITLE,
                TasksDbSchema.TasksTable.Cols.DONE
        };

        // Получаем объект типа курсор
        Cursor cursor = db.query(
                TasksDbSchema.TasksTable.NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<Task> tasks = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                Task task = new Task();
                String title = cursor.getString(cursor.getColumnIndex(TasksDbSchema.TasksTable.Cols.TITLE));
                task.setId(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
                task.setTitle(title);

                int done = cursor.getInt(cursor.getColumnIndex(TasksDbSchema.TasksTable.Cols.DONE));
                task.setDone(done == 1);
                tasks.add(task);
            }
        } finally {
            cursor.close();
        }
        return tasks;
    }


    public void insertData(String taskName) {

        SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();

        // Создаем пары ключ-значения для добавления строки
        ContentValues values = new ContentValues();
        values.put(TasksDbSchema.TasksTable.Cols.TITLE, taskName);
        values.put(TasksDbSchema.TasksTable.Cols.DONE, 0);
        db.insert(TasksDbSchema.TasksTable.NAME, null, values);
    }


    public void updateData(Task task, boolean done) {

        SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BaseColumns._ID, task.getId());
        values.put(TasksDbSchema.TasksTable.Cols.TITLE, task.getTitle());

        int isDone = done ? 1 : 0;
        values.put(TasksDbSchema.TasksTable.Cols.DONE, isDone);

        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};

        db.update(
                TasksDbSchema.TasksTable.NAME,
                values,
                selection,
                selectionArgs);
    }

    public void deleteData(Task task) {

        SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();

        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};
        db.delete(
                TasksDbSchema.TasksTable.NAME,
                selection,
                selectionArgs);
    }
}
