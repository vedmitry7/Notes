package com.vedmitryapps.notes.logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vedmitryapps.notes.json.SuperNoteDeserializerForDb;
import com.vedmitryapps.notes.json.SuperNoteSerializerForDb;
import com.vedmitryapps.notes.untils.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TABLE = "mytable";
    private static final String KEY_ID = "id";
    private static final String KEY_NOTE = "note";
    private static final String KEY_SECTION = "section";
    private static final String KEY_REMIND = "remind";

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table mytable ("
                + "id integer primary key autoincrement,"
                + "section text,"
                + "note blob,"
                + "remind integer default 0" + ");");


        db.execSQL("create table sections ("
                + "id integer primary key autoincrement,"
                + "section text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void deleteSection(Section section){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("sections",
                KEY_ID + " = ?",
                new String[] { String.valueOf(section.getId()) });
        db.close();
    }

    public void clearSectionTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        int clearCount = db.delete("sections", null, null);
    }

    public int addSection(Section section){

        SQLiteDatabase db = this.getWritableDatabase();

        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(section);
            out.flush();
            bytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_SECTION, bytes);
        long id = db.insert("sections", null, values);
        db.close();
        return (int) id;
    }

    public void updateSection(Section section){

        SQLiteDatabase db = this.getWritableDatabase();

        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(section);
            out.flush();
            bytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
            }
        }
        ContentValues values = new ContentValues();
        values.put(KEY_SECTION, bytes);

        int i = db.update("sections",
                values,
                KEY_ID + " = ?",
                new String[] { String.valueOf(section.getId()) });

        db.close();
    }

    public ArrayList<Section> getAllSections() {
        ArrayList<Section> sections = new ArrayList<>();

        String query = "SELECT  * FROM " + "sections";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Section section = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(1);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    section = (Section) in.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                    }
                }

                sections.add(section);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return sections;
    }

    public int addNote(SuperNote note, String section) {

        note.setSection(section);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(SimpleNote.class, new SuperNoteSerializerForDb())
                .registerTypeAdapter(ListNote.class, new SuperNoteSerializerForDb())
                .setPrettyPrinting()
                .create();

        String parsedNote = gson.toJson(note);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE,  parsedNote);
        values.put(KEY_SECTION, section);
        values.put(KEY_REMIND, note.isRemind() ? 1 : 0);
        long id = db.insert(TABLE, null, values);
        db.close();

        return (int) id;
    }

    public int addNote(SuperNote note) {
        note.setSection(Constants.UNDEFINED);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(SimpleNote.class, new SuperNoteSerializerForDb())
                .registerTypeAdapter(ListNote.class, new SuperNoteSerializerForDb())
                .setPrettyPrinting()
                .create();

        String parsedNote = gson.toJson(note);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE,  parsedNote);
        values.put(KEY_SECTION, Constants.UNDEFINED);
        values.put(KEY_REMIND,note.isRemind() ? 1 : 0);
        long id = db.insert(TABLE, null, values);
        db.close();

        return (int) id;
    }

    public ArrayList<SuperNote> getAllNote() {
        ArrayList<SuperNote> notes = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);


        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(SuperNote.class, new SuperNoteDeserializerForDb())
                .setPrettyPrinting()
                .create();


        SuperNote note = null;


        if (cursor.moveToFirst()) {
            do {
                String noteString = cursor.getString(2);
                note = gson.fromJson(noteString, SuperNote.class);

                note.setId(Integer.parseInt(cursor.getString(0)));

                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return notes;
    }

    public SuperNote getNote(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE + " WHERE id = '" + id + "'";
        Cursor cursor = db.rawQuery(query, null);
        SuperNote note = null;


        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(SuperNote.class, new SuperNoteDeserializerForDb())
                .setPrettyPrinting()
                .create();

        if (cursor.moveToFirst()) {
            String noteString = cursor.getString(2);
            note = gson.fromJson(noteString, SuperNote.class);

            note.setId(id);
        }

        cursor.close();
        return note;
    }



    public boolean isRemind(SuperNote t){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE + " WHERE id = '" + t.getId()+ "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {

            int i = cursor.getInt(3);
            if(i==0){
                return false;
            } else {
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public ArrayList<SuperNote> getNotes(String section) {
        ArrayList<SuperNote> notes = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE + " WHERE section = '" + section + "'";

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(SuperNote.class, new SuperNoteDeserializerForDb())
                .setPrettyPrinting()
                .create();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        SuperNote note = null;


        if (cursor.moveToFirst()) {
            do {
                String noteString = cursor.getString(2);
                note = gson.fromJson(noteString, SuperNote.class);

                int id = cursor.getInt(0);
                if(note==null) {
                    continue;
                }
                note.setId(cursor.getInt(0));

                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public int updateNote(SuperNote note, String section) {

        Log.i("TAG", "Update: remind -" + note.isRemind());

        if(section!=null && section.equals(Constants.DELETED)){
            note.setDeletionTime(System.currentTimeMillis());
        }

        if(section == null){
            note.setSection(Constants.CURRENT_KIND);
        } else {
            note.setSection(section);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(SimpleNote.class, new SuperNoteSerializerForDb())
                .registerTypeAdapter(ListNote.class, new SuperNoteSerializerForDb())
                .setPrettyPrinting()
                .create();

        String parsedNote = gson.toJson(note);

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, parsedNote);
        if(section != null)
        values.put(KEY_SECTION, section);
        values.put(KEY_REMIND, note.isRemind() ? 1 : 0);

        int i = db.update(TABLE,
                values,
                KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });

        db.close();

        return i;
    }

    public ArrayList<SuperNote> getNotificationNotes() {
        ArrayList<SuperNote> notes = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE + " WHERE remind = '" + 1 + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.i("TAG", "getNotificationNotes(): count - " + cursor.getCount());

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(SuperNote.class, new SuperNoteDeserializerForDb())
                .setPrettyPrinting()
                .create();

        SuperNote note = null;

        if (cursor.moveToFirst()) {
            do {
                String noteString = cursor.getString(2);
                note = gson.fromJson(noteString, SuperNote.class);

                note.setId(Integer.parseInt(cursor.getString(0)));

                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.i("TAG", "getNotificationNotes(): size - " + notes.size());

        return notes;
    }


    public void deleteNote(SuperNote note) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE,
                KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
        db.close();
    }

    public void clearDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        int clearCount = db.delete("mytable", null, null);
    }
}
