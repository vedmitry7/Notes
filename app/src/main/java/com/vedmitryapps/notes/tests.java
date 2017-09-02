package com.vedmitryapps.notes;


public class tests {

    public static void main(String[] args) {
        String s = "qwertyu";

        byte[] bytes = s.getBytes();

        String ss = bytes.toString();
        System.out.println(ss);
    }
}
