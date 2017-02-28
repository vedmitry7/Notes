package com.example.dmitryvedmed.taskbook;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Task implements Parcelable, Serializable {

    private int id;
    private String headLine, context;

    public Task(int id, String headLine, String context) {
        this.id = id;
        this.headLine = headLine;
        this.context = context;
    }

    public Task() {
    }

    protected Task(Parcel in) {
        id = in.readInt();
        headLine = in.readString();
        context = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", headLine='" + headLine + '\'' +
                ", context='" + context + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(headLine);
        parcel.writeString(context);
    }
}
