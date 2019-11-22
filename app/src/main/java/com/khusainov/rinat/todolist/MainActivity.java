package com.khusainov.rinat.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mAddFab;
    private RecyclerView mRecyclerView;
    private TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyAsyncTask myAsyncTask = new MyAsyncTask(this);

        initViews();

        TaskRepository mRepository = new TaskRepository(this);
    }

    private void initViews() {

        mAddFab = findViewById(R.id.fab_add);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTaskAdapter = new TaskAdapter();
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
                Task task = new Task();
                task.setId(1);
                task.setTitle(editText.getText().toString());
            }
        });
        builder.show();
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;

        public MyAsyncTask(Context context) {
            mContext = context.getApplicationContext();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TasksDbSchema.TasksTable.Cols.TITLE, "Task1");
            long newRowId = db.insert(TasksDbSchema.TasksTable.NAME, null, values);
            return null;
        }
    }
}
