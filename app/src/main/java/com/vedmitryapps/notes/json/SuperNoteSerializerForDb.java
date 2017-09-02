package com.vedmitryapps.notes.json;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.vedmitryapps.notes.logic.ListNote;
import com.vedmitryapps.notes.logic.SimpleNote;
import com.vedmitryapps.notes.logic.SuperNote;

import java.lang.reflect.Type;

public class SuperNoteSerializerForDb implements JsonSerializer<SuperNote> {

    @Override
    public JsonElement serialize(SuperNote src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject result = new JsonObject();

        result.addProperty("section", src.getSection());
        result.addProperty("color", src.getColor());

        result.addProperty("remind", src.isRemind());
        result.addProperty("repeating", src.isRepeating());
        result.addProperty("reminder_time", src.getReminderTime());
        result.addProperty("repeating_period", src.getRepeatingPeriod());
        result.addProperty("deletion_time", src.getDeletionTime());

        if(src instanceof SimpleNote){
            result.addProperty("type", "simple note");
            result.addProperty("headline", ((SimpleNote) src).getHeadLine());
            result.addProperty("content", ((SimpleNote) src).getContent());
        } else
        if(src instanceof ListNote){
            result.addProperty("type", "list note");
            result.addProperty("headline", ((ListNote) src).getHeadLine());

            JsonArray jsonArrayUnchecked = new JsonArray();
            for (int i = 0; i < ((ListNote)src).getUncheckedItems().size(); i++) {
                jsonArrayUnchecked.add(((ListNote)src).getUncheckedItems(i));
            }
            result.add("unchecked items", jsonArrayUnchecked);

            JsonArray jsonArrayChecked = new JsonArray();
            for (int i = 0; i < ((ListNote)src).getCheckedItems().size(); i++) {
                jsonArrayChecked.add(((ListNote)src).getCheckedItem(i));
            }
            result.add("checked items", jsonArrayChecked);
        }
        return result;
    }
}
