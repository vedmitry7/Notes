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

public class DBHelper4 extends SQLiteOpenHelper {

    private static final String TABLE = "mytable4";
    private static final String DELETED_TASKS = "deletedTasks2";
    private static final String KEY_ID = "id";
    private static final String KEY_TASK = "task";
    private static final String KEY_KIND = "kind";
    private static final String[] COLUMNS = {KEY_ID, KEY_TASK};

    public DBHelper4(Context context) {
        // конструктор суперкласса
        super(context, "myDB5", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("TAG", "      DBHelper--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table mytable4 ("
                + "id integer primary key autoincrement,"
                + "kind text,"
                + "task blob" + ");");

        db.execSQL("create table deletedTasks2 ("
                + "id integer primary key autoincrement,"
                + "task blob" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
        // 3. insert
        long id = db.insert(TABLE, null, values);
        // 4. close
        db.close();
        Log.d("TAG", "      DBHelper  addTask "  + task.toString());
        Log.d("TAG", "      DBHelper  ID "  + id);

        return (int) id;
    }


    public int addDeletedTask(SuperTask task) {
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
       // values.put(KEY_ID, task.getId());
        values.put(KEY_TASK,  bytes);
        // 3. insert
        long id = db.insert(DELETED_TASKS, null, values);
        // 4. close
        db.close();
        Log.d("TAG", "      DBHelper  addDeletedTask "  + task.toString());
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
                byte[] bytes = cursor.getBlob(1);
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

    public ArrayList<SuperTask> getTasks(String kind) {
        ArrayList<SuperTask> tasks = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE + " WHERE kind = " + kind;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d("TAG", "      DBHelper - getAllTask"  );
        // 3. go over each row, build book and add it to list
        SuperTask task = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(1);
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

    public ArrayList<SuperTask> getDeletedTasks() {
        ArrayList<SuperTask> tasks = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + DELETED_TASKS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d("TAG", "      DBHelper - getDeletedTasks"  );
        // 3. go over each row, build book and add it to list
        SuperTask task = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(1);
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


                // Add book to books
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        Log.d("TAG", "      DBHelper getDeletedTask"  + tasks.toString());

        return tasks;
    }

    public int updateTask(SuperTask task) {

        // 1. get reference to writable DB
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

        // 3. updating row
        int i = db.update(TABLE, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(task.getId()) }); //selection args

        // 4. close
        db.close();
        Log.d("TAG", "      DBHelper Update " + task.toString());
        return i;
    }
    public void deleteBook(SuperTask task) {

        addDeletedTask(task);

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE,
                KEY_ID+" = ?",
                new String[] { String.valueOf(task.getId()) });

        // 3. close
        db.close();

        Log.d("TAG", "      DBHelper Delate " + task.toString());
    }

    public void clearDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        int clearCount = db.delete("mytable4", null, null);
    }
}