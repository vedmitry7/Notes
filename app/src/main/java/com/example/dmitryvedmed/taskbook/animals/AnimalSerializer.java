package com.example.dmitryvedmed.taskbook.animals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;


public class AnimalSerializer implements JsonSerializer<Animal> {
    @Override
    public JsonElement serialize(Animal src, Type typeOfSrc, JsonSerializationContext context) {

        System.out.println("work!");
        JsonObject result = new JsonObject();

        result.addProperty("name", src.getName());
        result.addProperty("age", src.getAge());

        if(src instanceof Dog){
            result.addProperty("toy", ((Dog) src).getToy());
            result.addProperty("type", "Dog");
        } else
        if(src instanceof Cat){
            result.addProperty("empty", ((Cat) src).getEmpty());
            result.addProperty("type", "Cat");
            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < ((Cat) src).getProperties().size(); i++) {
                jsonArray.add(((Cat) src).getProperties().get(i));
            }
            result.add("properties", jsonArray);

        } else
        if(src instanceof Animal){
            result.addProperty("type", "Just Animal");
        }

        return result;
    }
}
