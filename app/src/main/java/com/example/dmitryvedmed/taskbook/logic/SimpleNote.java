package com.example.dmitryvedmed.taskbook.logic;


import java.io.Serializable;

public class SimpleNote extends SuperNote implements Serializable {

    private String headLine, content;

    public SimpleNote(int id, String headLine, String content) {
        this.setId(id);
        this.headLine = headLine;
        this.content = content;
    }

    public SimpleNote() {
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "SimpleNote{" +
                "id=" + getId() +
                ", headLine='" + headLine + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
