package com.example.dmitryvedmed.taskbook.animals;


import java.util.ArrayList;
import java.util.List;

public class Dog extends Animal {

    private String toy;

    List<Integer> times;

    public Dog(String name, int age, String toy) {
        super(name, age);
        this.toy = toy;
        times = new ArrayList<>();
    }

    public List<Integer> getTimes() {
        return times;
    }

    public void setTimes(List<Integer> times) {
        this.times = times;
    }

    public String getToy() {
        return toy;
    }

    public void setToy(String toy) {
        this.toy = toy;
    }
}
