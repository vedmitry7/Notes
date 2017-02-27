package com.example.dmitryvedmed.taskbook;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ListTaskActivity extends AppCompatActivity {

    LinearLayout layout;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);

        initView();
    }

    private void initView() {

        layout = (LinearLayout) findViewById(R.id.activity_list_task_layout);

        ListTask listTask = new ListTask();
        listTask.addTask("Гармошка");
        listTask.addTask("Матрешка");
        listTask.addTask("Антошка");

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (String task:listTask.getTasks()
             ) {
            View view = inflater.inflate(R.layout.item_list_task_activity, null);
            EditText editText = (EditText) view.findViewById(R.id.itemListEditText);
            editText.setText(task);
            layout.addView(view);
        }
    }

    public void newItem(View v){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_list_task_activity, null);
        EditText editText = (EditText) view.findViewById(R.id.itemListEditText);
        layout.addView(view);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }
}
