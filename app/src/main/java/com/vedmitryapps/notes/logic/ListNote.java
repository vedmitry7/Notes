package com.vedmitryapps.notes.logic;


import java.io.Serializable;
import java.util.ArrayList;

public class ListNote extends SuperNote implements Serializable {

    private String headLine;
    private ArrayList<String> uncheckedItems;
    private ArrayList<String> checkedItems;

    public ListNote() {
        uncheckedItems = new ArrayList<>();
        checkedItems = new ArrayList<>();
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public ArrayList<String> getUncheckedItems() {
        return uncheckedItems;
    }

    public void setUncheckedItems(ArrayList<String> tasks) {
        this.uncheckedItems = tasks;
    }

    public void addUncheckedItem(String s){
        uncheckedItems.add(s);
    }

    public String getUncheckedItems(int i){
        return uncheckedItems.get(i);
    }

    public void addCheckedItem(String s){
        checkedItems.add(s);
    }

    public String getCheckedItem(int i){
        return checkedItems.get(i);
    }

    public ArrayList<String> getCheckedItems() {
        return checkedItems;
    }

    public void setCheckedItems(ArrayList<String> tasks) {
        this.checkedItems = tasks;
    }
}
