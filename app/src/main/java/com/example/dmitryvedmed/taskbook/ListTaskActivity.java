package com.example.dmitryvedmed.taskbook;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import static com.example.dmitryvedmed.taskbook.CommonActivity.dbHelper;

public class ListTaskActivity extends AppCompatActivity {

    private ListTask listTask;
    public static RecyclerView recyclerView;
    private ListTaskRecyclerAdapter listTaskRecyclerAdapter;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        context = this;
        initTask();
        initView();
    }

    private void initView() {

        recyclerView = (RecyclerView) findViewById(R.id.list_activity_recycler_view);

        listTaskRecyclerAdapter = new ListTaskRecyclerAdapter(listTask, ListTaskActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listTaskRecyclerAdapter);

    }

    private void initTask() {
        listTask = (ListTask) getIntent().getSerializableExtra("ListTask");
        if(listTask==null){
            System.out.println("LIST TASK = NULL!");
            listTask = new ListTask();
            listTask.setId(-1);}
        else
        System.out.println("LIST TASK != NULL, id = " + listTask.getId());
    }


    public void hideDefaultKeyboard() {
        Activity activity = (Activity) context;
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//u have got lot of methods here
    }

    @Override
    protected void onPause() {
        listTask = listTaskRecyclerAdapter.getListTask();
        if(listTask.getId() == -1)
            dbHelper.addTask(listTask);
        else dbHelper.updateTask(listTask);
        super.onPause();
    }
}
