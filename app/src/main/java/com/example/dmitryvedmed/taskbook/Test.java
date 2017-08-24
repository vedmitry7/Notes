package com.example.dmitryvedmed.taskbook;


import android.util.Base64;

import com.example.dmitryvedmed.taskbook.json.SuperNoteDeserializer;
import com.example.dmitryvedmed.taskbook.json.SuperNoteSerializer;
import com.example.dmitryvedmed.taskbook.logic.ListNote;
import com.example.dmitryvedmed.taskbook.logic.SimpleNote;
import com.example.dmitryvedmed.taskbook.logic.SuperNote;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Test {

    public static void main(String[] args) {

        SimpleNote simpleNote = new SimpleNote();
        simpleNote.setColor(123);
        simpleNote.setHeadLine("headlineSN");
        simpleNote.setContent("ContentSN");
        simpleNote.setSection("deleted");

        SuperNote superNote1 = simpleNote;


        System.out.println("&&& " + (superNote1 instanceof SimpleNote));

        ListNote listNote = new ListNote();
        listNote.setHeadLine("fghd");
        listNote.addUncheckedItem("qwerty");
        listNote.addUncheckedItem("ytrewq");
        listNote.addCheckedItem("checked");

        listNote.setColor(4567);
        listNote.setSection("unknown");

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(SimpleNote.class, new SuperNoteSerializer())
                .registerTypeAdapter(ListNote.class, new SuperNoteSerializer())
                .setPrettyPrinting()
                .create();

        System.out.println(gson.toJson(superNote1));
        System.out.println(gson.toJson(listNote));

        String textOut = "[\n" + gson.toJson(simpleNote) + "," + "\n" + gson.toJson(listNote)+"\n"+"]";
        System.out.println(textOut);


        write("out.json", textOut);



            System.out.println("OUT");
        try {
            System.out.println(read("out.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        GsonBuilder builder2 = new GsonBuilder();
        Gson gson2 = builder
                .registerTypeAdapter(SuperNote.class, new SuperNoteDeserializer())
                .setPrettyPrinting()
                .create();

        String mass = "[{\"section\": \"deleted\",\n" +
                "  \"color\": 123,\n" +
                "  \"type\": \"simple note\",\n" +
                "  \"headLine\": \"headlineSN\",\n" +
                "  \"content\": \"ContentSN\"\n" +
                "},\n" +
                "{\n" +
                "  \"section\": \"unknown\",\n" +
                "  \"color\": 4567,\n" +
                "  \"type\": \"list note\",\n" +
                "  \"headline\": \"fghd\",\n" +
                "  \"unchecked items\": [\n" +
                "    \"qwerty\",\n" +
                "    \"ytrewq\"\n" +
                "  ],\n" +
                "  \"checked items\": [\n" +
                "    \"checked\"\n" +
                "  ]\n" +
                "}]";

        String supermass = "[{\n" +
                "  \"section\": \"undefined\",\n" +
                "  \"color\": 0,\n" +
                "  \"type\": \"simple note\",\n" +
                "  \"headline\": \" доп лала залп Адамов Адама \",\n" +
                "  \"content\": \"Лада взвивa leg Roger Ed \\nEh did ridge fbfj\"\n" +
                "},\n" +
                "{\n" +
                "  \"section\": \"undefined\",\n" +
                "  \"color\": 0,\n" +
                "  \"type\": \"list note\",\n" +
                "  \"headline\": \"Huff \",\n" +
                "  \"unchecked items\": [\n" +
                "    \"Shushed \",\n" +
                "    \"\"\n" +
                "  ],\n" +
                "  \"checked items\": [\n" +
                "    \"I didn\\u0027t \"\n" +
                "  ]\n" +
                "},\n" +
                "{\n" +
                "  \"section\": \"archive_code\",\n" +
                "  \"color\": 0,\n" +
                "  \"type\": \"list note\",\n" +
                "  \"headline\": \"USB DJ\\u0027s \",\n" +
                "  \"unchecked items\": [\n" +
                "    \"Xjdufдала \",\n" +
                "    \"Ща шалаша \",\n" +
                "    \"Плащ шага \",\n" +
                "    \"Шалаш ал-Ала \",\n" +
                "    \"Ослвлал\"\n" +
                "  ],\n" +
                "  \"checked items\": [\n" +
                "    \"I didn\\u0027t \"\n" +
                "  ]\n" +
                "},\n" +
                "{\n" +
                "  \"section\": \"Цйцйцйцйц\",\n" +
                "  \"color\": 0,\n" +
                "  \"type\": \"simple note\",\n" +
                "  \"headline\": \"Ствола \",\n" +
                "  \"content\": \"Балаково \"\n" +
                "}\n" +
                "]";

        String s = "{\n" +
                "  \"section\": \"unknown\",\n" +
                "  \"color\": 4567,\n" +
                "  \"type\": \"list note\",\n" +
                "  \"headline\": \"fghd\",\n" +
                "  \"unchecked items\": [\n" +
                "    \"qwerty\",\n" +
                "    \"ytrewq\"\n" +
                "  ],\n" +
                "  \"checked items\": [\n" +
                "    \"checked\"\n" +
                "  ]\n" +
                "}";


        JsonArray jsonArray = gson2.fromJson(supermass, JsonArray.class);


 /*       for (JsonElement e:jsonArray
                ) {
            SuperNote superNote = gson.fromJson(e, SuperNote.class);
            if(superNote instanceof ListNote) {
                System.out.println(((ListNote) superNote).getUncheckedItems().get(0));
                System.out.println(((ListNote) superNote).getUncheckedItems().get(1));
                System.out.println(((ListNote) superNote).getCheckedItems().get(0));
                System.out.println(superNote.getSection());
            }
            if(superNote instanceof SimpleNote) {
                System.out.println(((SimpleNote) superNote).getHeadLine());
                System.out.println(((SimpleNote) superNote).getContent());
                System.out.println(superNote.getSection());
            }
        }
*/


        for (JsonElement e:jsonArray
                ) {


            SuperNote superNote = gson2.fromJson(e, SuperNote.class);
            System.out.println("&&&SN " + (superNote instanceof SimpleNote));

            if (superNote instanceof SimpleNote) {
                System.out.println(((SimpleNote) superNote).getHeadLine());
                System.out.println(((SimpleNote) superNote).getContent());
                System.out.println(superNote.getSection());
            } else {
                System.out.println("&&&LN " + (superNote instanceof ListNote));
                System.out.println(((ListNote) superNote).getUncheckedItems().get(0));
                System.out.println(((ListNote) superNote).getUncheckedItems().get(1));
                System.out.println(((ListNote) superNote).getCheckedItems().get(0));
            }

        }


        List<String> sectionNames = new ArrayList<>();


        String s1 = "sdfsd";
        sectionNames.add(s1);
        s1 = "qqqqq";
        sectionNames.add(s1);

        for (String ssss:sectionNames
             ) {
            System.out.println(ssss);
        }

        System.out.println(sectionNames.contains(s1));
        s1 = "qewrty";
        System.out.println(sectionNames.contains(s1));


        String key = "123www";
        String initVector = "RandomInitVector";
        String code = "qwerty";
        String coded = encrypt(key, initVector,code);
        System.out.println(coded);

        String decoded = decrypt(key,initVector,coded);

        System.out.println(decoded);


      /*  Type collectionType = new TypeToken<Collection<SuperNote>>(){}.getType();
        Collection<SuperNote> users = gson2.fromJson(mass, collectionType);

        List<SuperNote> list = new ArrayList<>();

        list.addAll(users);

        SuperNote sss = list.get(1);

        ListNote lsls = (ListNote) sss;*/

        // System.out.println(((ListNote) sss).getUncheckedItems(1));
/*

        Cat cat = new Cat("Вася", 7 , "Red cat");
        cat.addProperties("Комок шерсти");
        cat.addProperties("Банан");
        Dog dog = new Dog("Шарик", 4, "Сапог");

        Animal animalCat = cat;
        Animal animalDog = dog;

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(Dog.class, new AnimalSerializer())
                .registerTypeAdapter(Cat.class, new AnimalSerializer())
                .setPrettyPrinting()
                .create();

        System.out.println(gson.toJson(animalCat));
        System.out.println(gson.toJson(animalDog));

        String stringCat = "{\n" +
                "  \"name\": \"Вася\",\n" +
                "  \"age\": 7,\n" +
                "  \"empty\": \"Red cat\",\n" +
                "  \"type\": \"Cat\"\n" +
                "}";

        String stringDog = "{\n" +
                "  \"name\": \"Шарик\",\n" +
                "  \"age\": 4,\n" +
                "  \"toy\": \"Сапог\",\n" +
                "  \"type\": \"Dog\"\n" +
                "}";

        String difCat = "{\n" +
                "  \"name\": \"Вася\",\n" +
                "  \"age\": 7,\n" +
                "  \"empty\": \"Red cat\",\n" +
                "  \"type\": \"Cat\",\n" +
                "  \"properties\": [\n" +
                "    \"Комок шерсти\",\n" +
                "    \"Банан\"\n" +
                "  ]\n" +
                "}";

        GsonBuilder builder2 = new GsonBuilder();
        Gson gson2 = builder
                .registerTypeAdapter(Animal.class, new AnimalDeserializer())
                .create();

        Animal animal = gson2.fromJson(difCat, Animal.class);

        System.out.println(animal.getAge() + " " + animal.getName() + " " + ((Cat)animal).getEmpty() + " \r\n" + ((Cat)animal).getProperties().toString());
*/

    }

    public static void write(String fileName, String text) {
        //Определяем файл
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем текст у файл
                out.print(text);
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String read(String fileName) throws FileNotFoundException {
        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();

        exists(fileName);

        try {
            //Объект для чтения файла в буфер
            BufferedReader in = new BufferedReader(new FileReader( fileName));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                //Также не забываем закрыть файл
                in.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        //Возвращаем полученный текст с файла
        return sb.toString();
    }

    private static void exists(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()){
            throw new FileNotFoundException(file.getName());
        }
    }

    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encodeToString(encrypted,11));

            return Base64.encodeToString(encrypted,11);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(encrypted, 11));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
