package com.example.dmitryvedmed.taskbook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ListTaskActivity extends AppCompatActivity {

    private ListTask listTask;
    public static RecyclerView recyclerView;
    private ListTaskRecyclerAdapter listTaskRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);

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

        listTask = new ListTask();
        listTask.addUncheckedTask("Гармошка");
        listTask.addUncheckedTask("Матрешка");
        listTask.addUncheckedTask("Антошка");

        listTask.addCheckedTask("UN Гармошка");
        listTask.addCheckedTask("UN Матрешка");
        listTask.addCheckedTask("UN Антошка");
    }
}
