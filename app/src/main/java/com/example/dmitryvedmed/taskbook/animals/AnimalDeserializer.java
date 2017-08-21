package com.example.dmitryvedmed.taskbook.animals;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class AnimalDeserializer implements JsonDeserializer<Animal> {
    @Override
    public Animal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        Animal result = null;
        System.out.println("Deserialize");
        switch (jsonObject.get("type").getAsString()){
            case "Dog":
                Dog dog = new Dog(jsonObject.get("name").getAsString(),
                        jsonObject.get("age").getAsInt(),
                        jsonObject.get("toy").getAsString());
                result = dog;
                break;
            case "Cat":
                Cat cat = new Cat(jsonObject.get("name").getAsString(),
                        jsonObject.get("age").getAsInt(),
                        jsonObject.get("empty").getAsString());
                        JsonArray jsonArray =  jsonObject.getAsJsonArray("properties");
                for (int i = 0; i < jsonArray.size(); i++) {
                    cat.addProperties(jsonArray.get(i).getAsString());
                }
                result = cat;
                break;
        }

        return result;
    }
}
