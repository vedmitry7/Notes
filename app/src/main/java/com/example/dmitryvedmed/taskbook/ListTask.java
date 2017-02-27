package com.example.dmitryvedmed.taskbook;


import java.util.ArrayList;

public class ListTask {
    private int id;
    private String headLine;
    private ArrayList<String> tasks;

    public ListTask() {
        tasks = new ArrayList<>();
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

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }

    public void addTask(String s){
        tasks.add(s);
    }

    public String getTask(int i){
        return tasks.get(i);
    }
}
