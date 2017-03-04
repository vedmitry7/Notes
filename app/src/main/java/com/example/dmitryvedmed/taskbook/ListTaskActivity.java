package com.example.dmitryvedmed.taskbook;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

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
        listTask = new ListTask();
        listTask.addUncheckedTask("Гармошка");
        listTask.addUncheckedTask("Матрешка");
        listTask.addUncheckedTask("Антошка");
       /* listTask.addUncheckedTask("Гармошка1");
        listTask.addUncheckedTask("Матрешка1");
        listTask.addUncheckedTask("Антошка1");
        listTask.addUncheckedTask("Гармошка2");
        listTask.addUncheckedTask("Матрешка2");
        listTask.addUncheckedTask("Антошка2");*/


       listTask.addCheckedTask("UN Гармошка");
        listTask.addCheckedTask("UN Матрешка");
        listTask.addCheckedTask("UN Антошка");
/*        listTask.addCheckedTask("UN Гармошка1");
        listTask.addCheckedTask("UN Матрешка1");
        listTask.addCheckedTask("UN Антошка1");
        listTask.addCheckedTask("UN Гармошка2");
        listTask.addCheckedTask("UN Матрешка2");
        listTask.addCheckedTask("UN Антошка2");*/
    }
    public void hideDefaultKeyboard() {
        Activity activity = (Activity) context;
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//u have got lot of methods here
    }
}
