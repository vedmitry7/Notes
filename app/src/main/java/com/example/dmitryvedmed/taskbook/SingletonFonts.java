package com.example.dmitryvedmed.taskbook;

import android.content.Context;
import android.graphics.Typeface;

public class SingletonFonts {
    private static Typeface robotoRegular;
    private static Typeface robotoBold;

    public Typeface getRobotoRegular() {
        return robotoRegular;
    }

    public Typeface getRobotoBold() {
        return robotoBold;
    }

    public static void setRobotoRegular(Typeface robotoRegular) {
        SingletonFonts.robotoRegular = robotoRegular;
    }

    public static void setRobotoBold(Typeface robotoBold) {
        SingletonFonts.robotoBold = robotoBold;
    }

    private static volatile SingletonFonts instance;

    private SingletonFonts() {}

    public static SingletonFonts getInstance(Context activity) {
        SingletonFonts localInstance = instance;
        if (localInstance == null) {
            synchronized (SingletonFonts.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SingletonFonts();
                }
            }
            setRobotoRegular(Typeface.createFromAsset(activity.getAssets(), "font/Roboto-Regular.ttf"));
            setRobotoBold(Typeface.createFromAsset(activity.getAssets(), "font/Roboto-Bold.ttf"));
        }
        return localInstance;
    }


}