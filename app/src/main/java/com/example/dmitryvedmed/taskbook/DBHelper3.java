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

class DBHelper3 extends SQLiteOpenHelper {

    private static final String TABLE = "mytable3";
    private static final String KEY_ID = "id";
    private static final String KEY_TASK = "task";
    private static final String[] COLUMNS = {KEY_ID, KEY_TASK};

    public DBHelper3(Context context) {
        // конструктор суперкласса
        super(context, "myDB3", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("TAG", "--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table mytable3 ("
                + "id integer primary key autoincrement,"
                + "task blob" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int addTask(ListTask task) {
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
        // 3. insert
        long id = db.insert(TABLE, null, values);
        // 4. close
        db.close();
        Log.d("TAG", "  addTask "  + task.toString());
        Log.d("TAG", "  ID "  + id);

        return (int) id;
    }


    public ArrayList<ListTask> getAllTask() {
        ArrayList<ListTask> tasks = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        ListTask task = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(1);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    task = (ListTask) in.readObject();
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

        Log.d("TAG", "getAllTask"  + tasks.toString());

        return tasks;
    }

    public int updateTask(ListTask task) {

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
        Log.d("TAG", "Update " + task.toString());
        return i;
    }
    public void deleteBook(Task task) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE,
                KEY_ID+" = ?",
                new String[] { String.valueOf(task.getId()) });

        // 3. close
        db.close();

        Log.d("TAG", "Delate " + task.toString());
    }

    public void clearDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        int clearCount = db.delete("mytable3", null, null);
    }
}
