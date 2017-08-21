package com.example.dmitryvedmed.taskbook.animals;


import java.util.ArrayList;
import java.util.List;

public class Cat extends Animal {

    public static final String type = "Cat";

    private String empty;

    List<String> properties;

    public Cat(String name, int age, String empty) {
        super(name, age);
        this.empty = empty;
        properties = new ArrayList<>();
    }

    public void addProperties(String s){
        properties.add(s);
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    public String getEmpty() {
        return empty;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }
}
