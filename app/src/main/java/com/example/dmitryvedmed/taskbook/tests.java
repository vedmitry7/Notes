package com.example.dmitryvedmed.taskbook;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class tests {

    public static void main(String[] args) {

        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateFormatted = formatter.format(date);
        System.out.println(dateFormatted);
        String s = "17:26";

        try {
            Date d = formatter.parse(s);
            System.out.println(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String h = s.substring(0,s.indexOf(":"));
        String m = s.substring(s.indexOf(":")+1);
        System.out.println(h);
        System.out.println(m);
    }

}
