package com.example.dmitryvedmed.taskbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

class DBHelper5 extends SQLiteOpenHelper {

    private static final String TABLE = "mytable5";
    private static final String KEY_ID = "id";
    private static final String KEY_TASK = "task";
    private static final String KEY_KIND = "kind";
    private static final String[] COLUMNS = {KEY_ID, KEY_TASK};

    public DBHelper5(Context context) {
        // конструктор суперкласса
        super(context, "myDB6", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("TAG", "      DBHelper--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table mytable5 ("
                + "id integer primary key autoincrement,"
                + "kind text,"
                + "task blob" + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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

    public ArrayList<SuperTask> getTasks(String kind) {
        ArrayList<SuperTask> tasks = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE + " WHERE kind = '" + kind + "'";

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

    public int updateTask(SuperTask task, String kind) {
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
        values.put(KEY_KIND, kind);

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

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE,
                KEY_ID+" = ?",
                new String[] { String.valueOf(task.getId()) });
        db.close();

        Log.d("TAG", "      DBHelper Delate " + task.toString());
    }

    public void clearDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        int clearCount = db.delete("mytable5", null, null);
    }
}
