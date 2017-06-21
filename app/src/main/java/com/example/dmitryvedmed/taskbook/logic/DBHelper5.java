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

public class DBHelper5 extends SQLiteOpenHelper {

    private static final String TABLE = "mytable7";
    private static final String KEY_ID = "id";
    private static final String KEY_TASK = "task";
    private static final String KEY_KIND = "kind";
    private static final String KEY_REMIND = "remind";
    private static final String[] COLUMNS = {KEY_ID, KEY_TASK};

    public DBHelper5(Context context) {
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

    public int addTask(SuperTask task, String kind) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(task);
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
        values.put(KEY_KIND, kind);
        values.put(KEY_REMIND,task.isRemind() ? 1 : 0);
        // 3. insert
        long id = db.insert(TABLE, null, values);
        // 4. close
        db.close();
        Log.d("TAG", "      DBHelper  addTask "  + task.toString());
        Log.d("TAG", "      DBHelper  ID "  + id);

        return (int) id;
    }

    public int addTask(SuperTask task) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(task);
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
        values.put(KEY_REMIND,task.isRemind() ? 1 : 0);
        // 3. insert
        long id = db.insert(TABLE, null, values);
        // 4. close
        db.close();
        Log.d("TAG", "      DBHelper  addTask "  + task.toString());
        Log.d("TAG", "      DBHelper  ID "  + id);

        return (int) id;
    }



    public ArrayList<SuperTask> getAllTask() {
        ArrayList<SuperTask> tasks = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d("TAG", "      DBHelper - getAllTask"  );
        // 3. go over each row, build book and add it to list
        SuperTask task = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(2);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    task = (SuperTask) in.readObject();
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
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        Log.d("TAG", "      DBHelper    getAllTask"  + tasks.toString());

        return tasks;
    }

    public SuperTask getTask(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE + " WHERE id = '" + id + "'";
        Cursor cursor = db.rawQuery(query, null);
        SuperTask task = null;
        if (cursor.moveToFirst()) {
            byte[] bytes = cursor.getBlob(2);
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                task = (SuperTask) in.readObject();
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

            task.setId(id);

        }
        return task;
    }



    public boolean isRemind(SuperTask t){

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

    public ArrayList<SuperTask> getTasks(String kind) {
        ArrayList<SuperTask> tasks = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE + " WHERE kind = '" + kind + "'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d("TAG", "      DBHelper - getTasks KIND - " + kind);


        Log.d("TAG", "      DBHelper - getTasks KIND - " + kind!=null?kind:"null");
        // 3. go over each row, build book and add it to list
        SuperTask task = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(2);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    task = (SuperTask) in.readObject();
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
                    Log.d("TAG", "      DBHelper    getTasks, task with id = "  + cursor.getInt(0) + " - null");
                    continue;
                }
                task.setId(cursor.getInt(0));

                //Log.d("TAG", "     remind " + cursor.getInt(3));
                Log.d("TAG", "     remind " + task.isRemind() + " " + task.getPosition());

                // Add book to books
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        Log.d("TAG", "      DBHelper    getTasks(kind)"  + tasks.toString());

        return tasks;
    }

    public int updateTask(SuperTask task, String kind) {
        // 1. get reference to writable DB



//        Log.d("TAG", "      DBHelper - UPDATE tasks KIND - " + kind!=null ? kind : "null");

        if(kind!=null && kind.equals(Constants.DELETED)){
            task.setDeletionTime(System.currentTimeMillis());
        }

        SQLiteDatabase db = this.getWritableDatabase();

        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(task);
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
        if(kind != null)
        values.put(KEY_KIND, kind);
        values.put(KEY_REMIND, task.isRemind() ? 1 : 0);

        // 3. updating row
        int i = db.update(TABLE, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[] { String.valueOf(task.getId()) }); //selection args

        // 4. close
        db.close();
        Log.d("TAG", "      DBHelper Update " + task.toString());
        Log.d("TAG", "      DBHelper Update " + kind);

        Log.d("TAG", "      DBHelper Update remaind " + task.isRemind());
        return i;
    }

    public ArrayList<SuperTask> getNotificationTasks() {
        ArrayList<SuperTask> tasks = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE + " WHERE remind = '" + 1 + "'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d("TAG", "      DBHelper - getTasks"  );
        // 3. go over each row, build book and add it to list
        SuperTask task = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(2);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    task = (SuperTask) in.readObject();
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
                    Log.d("TAG", "      DBHelper    getTasks, task with id = "  + cursor.getInt(0) + " - null");
                    continue;
                }
                task.setId(cursor.getInt(0));

                //Log.d("TAG", "     remind " + cursor.getInt(3));
                Log.d("TAG", "     remind " + task.isRemind() + " " + task.getPosition());

                // Add book to books
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        Log.d("TAG", "      DBHelper    getAllTask"  + tasks.toString());

        return tasks;
    }


    public void deleteTask(SuperTask task) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE,
                KEY_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });
        db.close();

        Log.d("TAG", "      DBHelper Delate " + task.toString());
    }

    public void clearDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        int clearCount = db.delete("mytable7", null, null);
    }
}
