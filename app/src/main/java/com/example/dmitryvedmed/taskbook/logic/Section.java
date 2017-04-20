package com.example.dmitryvedmed.taskbook.logic;

import java.io.Serializable;


public class Section implements Serializable {
    String name;
    int position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
