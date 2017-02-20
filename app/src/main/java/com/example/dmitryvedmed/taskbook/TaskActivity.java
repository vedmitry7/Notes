package com.example.dmitryvedmed.taskbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class TaskActivity extends AppCompatActivity {

    TextView head,text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initView();

    }
    private void initView() {
        head = (TextView) findViewById(R.id.TaskHeadLineText);
        text = (TextView) findViewById(R.id.taskText);

        Intent intent = getIntent();
        String headText = intent.getStringExtra("head");
        String textText = intent.getStringExtra("text");
        head.setText(headText);
        text.setText(textText);
    }
}