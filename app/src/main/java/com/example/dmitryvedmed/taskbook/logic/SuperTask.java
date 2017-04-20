package com.example.dmitryvedmed.taskbook.logic;


import java.io.Serializable;

public class SuperTask implements Serializable {
    private int id;
    private int position;
    private int color;

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
}
