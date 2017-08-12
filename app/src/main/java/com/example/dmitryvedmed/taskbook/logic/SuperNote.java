package com.example.dmitryvedmed.taskbook.logic;


import java.io.Serializable;

public class SuperNote implements Serializable {
    private int id;
    private int position;
    private int color;
    private long reminderTime;
    private long deletionTime;
    private boolean remind;
    private long repeatingPeriod;
    private boolean repeating;


    public long getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(long deletionTime) {
        this.deletionTime = deletionTime;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public long getRepeatingPeriod() {
        return repeatingPeriod;
    }

    public void setRepeatingPeriod(long repeatingPeriod) {
        this.repeatingPeriod = repeatingPeriod;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isRemind() {
        return remind;
    }

    public void setRemind(boolean remind) {
        this.remind = remind;
    }

}
