package com.example.dmitryvedmed.taskbook.logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dmitryvedmed.taskbook.untils.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TABLE = "mytable7";
    private static final String KEY_ID = "id";
    private static final String KEY_TASK = "task";
    private static final String KEY_KIND = "kind";
    private static final String KEY_REMIND = "remind";
    private static final String[] COLUMNS = {KEY_ID, KEY_TASK};

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "myDB8", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("TAG", "      DBHelper--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table mytable7 ("
                + "id integer primary key autoincrement,"
                + "kind text,"
                + "task blob,"
                + "remind integer default 0" + ");");


        db.execSQL("create table sections ("
                + "id integer primary key autoincrement,"
                + "kind text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void deleteSection(Section section){
        Log.d("TAG", "      DBHelper Delate Section " + section.getName());

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("sections",
                KEY_ID + " = ?",
                new String[] { String.valueOf(section.getId()) });
        db.close();

        Log.d("TAG", "      DBHelper Delate Section " + section.getName());
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
                // ignore close exception
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_KIND, bytes);
        // 3. insert
        long id = db.insert("sections", null, values);
        // 4. close
        db.close();
        Log.d("TAG", "      DBHelper  add Section"  + id);
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
                // ignore close exception
            }
        }
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues(); // get title
        values.put(KEY_KIND, bytes); // get author

        // 3. updating row
        int i = db.update("sections", //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[] { String.valueOf(section.getId()) }); //selection args

        // 4. close
        db.close();
    }

    public ArrayList<Section> getAllSections() {
        ArrayList<Section> sections = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + "sections";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d("TAG", "      DBHelper - getAllsections"  );
        // 3. go over each row, build book and add it to list
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
                        // ignore close exception
                    }
                }

                // Add book to books
                sections.add(section);
            } while (cursor.moveToNext());
        }


        return sections;
    }

    public int addNote(SuperNote note, String section) {
        // 1. get reference to writable DB

        note.setSection(section);

        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(note);
            out.flush();
            bytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_TASK,  bytes);
        values.put(KEY_KIND, section);
        values.put(KEY_REMIND,note.isRemind() ? 1 : 0);
        // 3. insert
        long id = db.insert(TABLE, null, values);
        // 4. close
        db.close();
        Log.d("TAG", "      DBHelper  addNote "  + note.toString());
        Log.d("TAG", "      DBHelper  ID "  + id);

        return (int) id;
    }

    public int addNote(SuperNote note) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(note);
            out.flush();
            bytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_TASK,  bytes);
        values.put(KEY_KIND, Constants.UNDEFINED);
        values.put(KEY_REMIND,note.isRemind() ? 1 : 0);
        // 3. insert
        long id = db.insert(TABLE, null, values);
        // 4. close
        db.close();
        Log.d("TAG", "      DBHelper  addNote "  + note.toString());
        Log.d("TAG", "      DBHelper  ID "  + id);

        return (int) id;
    }

    public ArrayList<SuperNote> getAllNote() {
        ArrayList<SuperNote> notes = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d("TAG", "      DBHelper - getAllNote"  );
        // 3. go over each row, build book and add it to list
        SuperNote task = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(2);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    task = (SuperNote) in.readObject();
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
                        // ignore close exception
                    }
                }

                task.setId(Integer.parseInt(cursor.getString(0)));

                // Add book to books
                notes.add(task);
            } while (cursor.moveToNext());
        }

        Log.d("TAG", "      DBHelper    getAllNote"  + notes.toString());

        return notes;
    }

    public SuperNote getNote(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE + " WHERE id = '" + id + "'";
        Cursor cursor = db.rawQuery(query, null);
        SuperNote note = null;
        if (cursor.moveToFirst()) {
            byte[] bytes = cursor.getBlob(2);
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                note = (SuperNote) in.readObject();
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
                    // ignore close exception
                }
            }

            note.setId(id);

        }
        return note;
    }



    public boolean isRemind(SuperNote t){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE + " WHERE id = '" + t.getId()+ "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            Log.d("TAG", "      DBHelper - IS REMIND ID = " + t.getId() + " "  );

            int i = cursor.getInt(3);
            if(i==0){
                Log.d("TAG", "      DBHelper - IS REMIND ID = " + t.getId() + " FALSE "  );
                return false;
            }
            else {
                Log.d("TAG", "      DBHelper - IS REMIND ID = " + t.getId() + " TRUE"  );
                return true;
            }
        }
        Log.d("TAG", "      DBHelper - IS REMIND NOT HAS THIS TASK " );
        return false;
    }

    public ArrayList<SuperNote> getNotes(String section) {
        ArrayList<SuperNote> tasks = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE + " WHERE kind = '" + section + "'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d("TAG", "      DBHelper - getNotes KIND - " + section);


        Log.d("TAG", "      DBHelper - getNotes KIND - " + section!=null?section:"null");
        // 3. go over each row, build book and add it to list
        SuperNote task = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(2);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    task = (SuperNote) in.readObject();
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
                        // ignore close exception
                    }
                }

                int id = cursor.getInt(0);
                System.out.println("DB          ID - " + id);
                System.out.println(task==null);
                if(task==null) {
                    Log.d("TAG", "      DBHelper    getNotes, task with id = "  + cursor.getInt(0) + " - null");
                    continue;
                }
                task.setId(cursor.getInt(0));

                //Log.d("TAG", "     remind " + cursor.getInt(3));
                Log.d("TAG", "     remind " + task.isRemind() + " " + task.getPosition());

                // Add book to books
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        Log.d("TAG", "      DBHelper    getNotes(kind)"  + tasks.toString());

        return tasks;
    }

    public int updateNote(SuperNote note, String section) {

        if(section!=null && section.equals(Constants.DELETED)){
            note.setDeletionTime(System.currentTimeMillis());
        }

        note.setSection(section);

        SQLiteDatabase db = this.getWritableDatabase();

        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(note);
            out.flush();
            bytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues(); // get title
        values.put(KEY_TASK, bytes); // get author
        if(section != null)
        values.put(KEY_KIND, section);
        values.put(KEY_REMIND, note.isRemind() ? 1 : 0);

        // 3. updating row
        int i = db.update(TABLE, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[] { String.valueOf(note.getId()) }); //selection args

        // 4. close
        db.close();
        Log.d("TAG", "      DBHelper Update " + note.toString());
        Log.d("TAG", "      DBHelper Update " + section);

        Log.d("TAG", "      DBHelper Update remaind " + note.isRemind());
        return i;
    }

    public ArrayList<SuperNote> getNotificationNotes() {
        ArrayList<SuperNote> notes = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE + " WHERE remind = '" + 1 + "'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d("TAG", "      DBHelper - getNotes"  );
        // 3. go over each row, build book and add it to list
        SuperNote note = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(2);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    note = (SuperNote) in.readObject();
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
                        // ignore close exception
                    }
                }

                int id = cursor.getInt(0);
                System.out.println("DB          ID - " + id);
                System.out.println(note==null);
                if(note==null) {
                    Log.d("TAG", "      DBHelper    getNotes, note with id = "  + cursor.getInt(0) + " - null");
                    continue;
                }
                note.setId(cursor.getInt(0));

                //Log.d("TAG", "     remind " + cursor.getInt(3));
                Log.d("TAG", "     remind " + note.isRemind() + " " + note.getPosition());

                // Add book to books
                notes.add(note);
            } while (cursor.moveToNext());
        }

        Log.d("TAG", "      DBHelper    getAllNote"  + notes.toString());

        return notes;
    }


    public void deleteNote(SuperNote note) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE,
                KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
        db.close();

        Log.d("TAG", "      DBHelper Delate " + note.toString());
    }

    public void clearDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        int clearCount = db.delete("mytable7", null, null);
    }
}
