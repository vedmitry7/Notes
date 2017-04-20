package com.example.dmitryvedmed.taskbook.logic;


import java.io.Serializable;

public class SimpleTask extends SuperTask implements Serializable {

    private String headLine, context;

    public SimpleTask(int id, String headLine, String context) {
        this.setId(id);
        this.headLine = headLine;
        this.context = context;
    }

    public SimpleTask() {
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


    @Override
    public String toString() {
        return "SimpleTask{" +
                "id=" + getId() +
                ", headLine='" + headLine + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
