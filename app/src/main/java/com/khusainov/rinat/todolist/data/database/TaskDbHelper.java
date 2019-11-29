package com.khusainov.rinat.todolist.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TaskDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "TaskDatabase.db";

    public TaskDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TasksDbSchema.TasksTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TasksDbSchema.TasksTable.Cols.TITLE + " text, " +
                TasksDbSchema.TasksTable.Cols.DONE + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {
            sqLiteDatabase.execSQL("ALTER TABLE " + TasksDbSchema.TasksTable.NAME + " ADD done integer");
        }
    }
}
