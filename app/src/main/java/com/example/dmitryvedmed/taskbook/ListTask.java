package com.example.dmitryvedmed.taskbook;


import java.io.Serializable;
import java.util.ArrayList;

public class ListTask implements Serializable {
    private int id;
    private String headLine;
    private ArrayList<String> uncheckedTasks;
    private ArrayList<String> checkedTasks;

    public ListTask() {
        uncheckedTasks = new ArrayList<>();
        checkedTasks = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public ArrayList<String> getUncheckedTasks() {
        return uncheckedTasks;
    }

    public void setUncheckedTasks(ArrayList<String> tasks) {
        this.uncheckedTasks = tasks;
    }

    public void addUncheckedTask(String s){
        uncheckedTasks.add(s);
    }

    public String getUncheckedTask(int i){
        return uncheckedTasks.get(i);
    }

    public void addCheckedTask(String s){
        checkedTasks.add(s);
    }

    public String getCheckedTask(int i){
        return checkedTasks.get(i);
    }

    public ArrayList<String> getCheckedTasks() {
        return checkedTasks;
    }

    public void setCheckedTasks(ArrayList<String> tasks) {
        this.checkedTasks = tasks;
    }
}
