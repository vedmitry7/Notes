package com.example.dmitryvedmed.taskbook;

import android.app.Application;

public class MyApplication extends Application {

    private String myStringState;
    private Integer myIntState;
    private Mode mode;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public static enum Mode {
        NORMAL, SELECTION_MODE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
