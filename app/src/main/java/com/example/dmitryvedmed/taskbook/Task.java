package com.example.dmitryvedmed.taskbook;


public class Task {

    private int id;
    private String headLine, context;

    public Task(int id, String headLine, String context) {
        this.id = id;
        this.headLine = headLine;
        this.context = context;
    }

    public Task() {
    }

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
}
