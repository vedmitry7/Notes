package com.example.dmitryvedmed.taskbook.json;

import com.example.dmitryvedmed.taskbook.logic.ListNote;
import com.example.dmitryvedmed.taskbook.logic.SimpleNote;
import com.example.dmitryvedmed.taskbook.logic.SuperNote;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


public class SuperNoteDeserializer implements JsonDeserializer<SuperNote> {
    @Override
    public SuperNote deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        SuperNote result = null;

        switch (jsonObject.get("type").getAsString()){
            case "simple note":
                SimpleNote simpleNote = new SimpleNote();

                simpleNote.setHeadLine(jsonObject.get("headline").getAsString());
                simpleNote.setContent(jsonObject.get("content").getAsString());

                result = simpleNote;
                break;
            case "list note":
                ListNote listNote = new ListNote();
                listNote.setHeadLine(jsonObject.get("headline").getAsString());

                JsonArray jsonArrayUnchecked =  jsonObject.getAsJsonArray("unchecked items");
                for (int i = 0; i < jsonArrayUnchecked.size(); i++) {
                    listNote.addUncheckedItem(jsonArrayUnchecked.get(i).getAsString());
                }
                JsonArray jsonArrayChecked =  jsonObject.getAsJsonArray("checked items");
                for (int i = 0; i < jsonArrayChecked.size(); i++) {
                    listNote.addCheckedItem(jsonArrayChecked.get(i).getAsString());
                }
                result = listNote;
                break;
        }

        result.setSection(jsonObject.get("section").getAsString());
        result.setColor(jsonObject.get("color").getAsInt());

        return result;
    }
}
