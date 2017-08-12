package com.example.dmitryvedmed.taskbook.logic;


import java.io.Serializable;

public class SimpleNote extends SuperNote implements Serializable {

    private String headLine, context;

    public SimpleNote(int id, String headLine, String context) {
        this.setId(id);
        this.headLine = headLine;
        this.context = context;
    }

    public SimpleNote() {
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
        return "SimpleNote{" +
                "id=" + getId() +
                ", headLine='" + headLine + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
