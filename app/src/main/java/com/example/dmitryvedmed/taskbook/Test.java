package com.example.dmitryvedmed.taskbook;


import com.example.dmitryvedmed.taskbook.animals.Animal;
import com.example.dmitryvedmed.taskbook.animals.AnimalDeserializer;
import com.example.dmitryvedmed.taskbook.animals.AnimalSerializer;
import com.example.dmitryvedmed.taskbook.animals.Cat;
import com.example.dmitryvedmed.taskbook.animals.Dog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Test {

    public static void main(String[] args) {

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

    }
}
