package com.example.dmitryvedmed.taskbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import static com.example.dmitryvedmed.taskbook.MainActivity.NAME_PREFERENCES;

public class TaskActivity extends AppCompatActivity {

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
        String headText = intent.getStringExtra("head");
        String textText = intent.getStringExtra("text");
        position = intent.getIntExtra("pos",42);
        head.setText(headText);
        text.setText(textText);
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
        savePreferences();
        super.onPause();
    }
}