package com.example.dmitryvedmed.taskbook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.EditText;

import static com.example.dmitryvedmed.taskbook.MainActivity.dbHelper;

public class ListTaskActivity extends AppCompatActivity {

    private ListTask listTask;
    public static RecyclerView recyclerView;
    private ListTaskRecyclerAdapter listTaskRecyclerAdapter;
    Context context;
    EditText headList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        context = this;
        initTask();
        initView();
    }

    private void initView() {

        headList = (EditText) findViewById(R.id.listHeadEditText);
        Typeface boldTypeFace = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Bold.ttf");
        headList.setTypeface(boldTypeFace);
        headList.setText(listTask.getHeadLine());

        recyclerView = (RecyclerView) findViewById(R.id.list_activity_recycler_view);

        listTaskRecyclerAdapter = new ListTaskRecyclerAdapter(listTask, ListTaskActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listTaskRecyclerAdapter);

    }

    private void initTask() {
        listTask = (ListTask) getIntent().getSerializableExtra("ListTask");
        if(listTask==null) {
            System.out.println("LIST TASK = NULL!");
            listTask = new ListTask();
            listTask.setHeadLine("");
            listTask.setId(-1);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        else
            System.out.println("LIST TASK != NULL, id = " + listTask.getHeadLine());
    }


    public void hideDefaultKeyboard() {
        Activity activity = (Activity) context;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//u have got lot of methods here
    }

    @Override
    protected void onPause() {
        listTask = listTaskRecyclerAdapter.getListTask();
        listTask.setHeadLine(headList.getText().toString());
        if(listTask.getId() == -1)
            dbHelper.addTask(listTask);
        else dbHelper.updateTask(listTask);
        super.onPause();
    }
}
