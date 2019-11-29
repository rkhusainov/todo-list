package com.khusainov.rinat.todolist.data.model;

public class Task {
    private long mId;
    private String mTitle;
    private boolean mDone;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isDone() {
        return mDone;
    }

    public void setDone(boolean done) {
        mDone = done;
    }
}
