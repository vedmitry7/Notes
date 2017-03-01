package com.example.dmitryvedmed.taskbook;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
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
        listTask.addUncheckedTask("Гармошка");
        listTask.addUncheckedTask("Матрешка");
        listTask.addUncheckedTask("Антошка");

        listTask.addCheckedTask("UN Гармошка");
        listTask.addCheckedTask("UN Матрешка");
        listTask.addCheckedTask("UN Антошка");

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (String task:listTask.getUncheckedTasks()
                ) {
            View view = inflater.inflate(R.layout.item_list_task_activity, null);
            EditText editText = (EditText) view.findViewById(R.id.itemListEditText);
            editText.setText(task);
            editText.setTextColor(Color.BLACK);
            layout.addView(view);
            Log.d("TAG", task);
            Log.d("TAG", String.valueOf(editText.hashCode()));
        }

        for (String task:listTask.getCheckedTasks()
             ) {
            View view = inflater.inflate(R.layout.item_list_task_activity, null);
            EditText editText = (EditText) view.findViewById(R.id.itemListEditText);
            //editText.setText(task);

            String htmlTaggedString  = "<s>" + task + "</s>";
            Spanned textSpan  =  android.text.Html.fromHtml(htmlTaggedString);
            editText.setTextColor(Color.BLACK);
            editText.setText(textSpan);

            layout.addView(view);
            Log.d("TAG", task);
            Log.d("TAG", String.valueOf(editText.hashCode()));
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
