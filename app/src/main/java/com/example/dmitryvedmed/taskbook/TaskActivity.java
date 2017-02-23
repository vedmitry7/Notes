package com.example.dmitryvedmed.taskbook;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import static com.example.dmitryvedmed.taskbook.MainActivity.NAME_PREFERENCES;

public class TaskActivity extends AppCompatActivity {

    int id;

    EditText head,text;
    SharedPreferences sharedPreferences;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initView();


    }
    private void initView() {
        head = ( EditText) findViewById(R.id.headEditText);
        text = ( EditText) findViewById(R.id.taskEditText);

       Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);

        Log.d("TAG", String.valueOf(id));
        if(id>-1)
            loadDate();
        /* String headText = intent.getStringExtra("head");
        String textText = intent.getStringExtra("text");
        position = intent.getIntExtra("pos",42);
        head.setText(headText);
        text.setText(textText);*/
    }

    private void loadDate() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("mytable", null, null, null, null, null, null);

        if(c.moveToFirst()){
            Log.d("TAG", "EEEEEEEEEEEEEE");
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("headline");
            int emailColIndex = c.getColumnIndex("content");

            Log.d("TAG", c.getString(nameColIndex));
            Log.d("TAG", getString(emailColIndex));

     //       head.setText(c.getString(nameColIndex));
          //  text.setText(getString(emailColIndex));
        }
        c.close();
    }

    private void saveDate() {
        String headline = String.valueOf(head.getText());
        String content = String.valueOf(text.getText());
        Log.d("TAG", String.valueOf(headline.length()) + " " + content.length());

        if(headline.length()==0 && content.length()==0) {
            Log.d("TAG", "NON");
            return;
        }
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("headline", headline);
        cv.put("content", content);

        long rowID = db.insert("mytable", null, cv);
        Log.d("TAG", "row inserted, ID = " + rowID);


    }

    private void savePreferences(){
        sharedPreferences = this.getSharedPreferences(NAME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(String.valueOf(position)+"0", String.valueOf(head.getText()));
        editor.putString(String.valueOf(position)+"1", String.valueOf(text.getText()));
        editor.commit();
        System.out.println("SAVE PREF");
        System.out.println("pos = " + position);
        System.out.println(sharedPreferences.getString(String.valueOf(position)+"0"," ---0"));
        System.out.println(sharedPreferences.getString(String.valueOf(position)+"1"," ---1"));
    }

    @Override
    protected void onPause() {
      //  savePreferences();
        saveDate();
        super.onPause();
    }
}