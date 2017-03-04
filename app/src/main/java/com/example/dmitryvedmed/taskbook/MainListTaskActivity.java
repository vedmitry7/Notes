package com.example.dmitryvedmed.taskbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainListTaskActivity extends AppCompatActivity {

    List<ListTask> values;
    public static DBHelper3 dbHelper;
    public static RecyclerView recyclerView;
    private MainListActivityRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list_task);

        dbHelper = new DBHelper3(this);
        //values = dbHelper.getAllTask();
         ListTask listTask = new ListTask();
        listTask.addUncheckedTask("Гармошка");
        listTask.addUncheckedTask("Матрешка");
        ListTask listTask2 = new ListTask();
        listTask2.addUncheckedTask("Гармошка2");
        listTask2.addUncheckedTask("Матрешка2");

        values = new ArrayList<>();
        values.add(listTask);
        values.add(listTask2);
        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view23);

        adapter = new MainListActivityRecyclerAdapter(values, MainListTaskActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("qq " );
            }
        });
        System.out.println("eeeah");

    }

    public void newTask(View v){
        Intent intent = new Intent(getApplicationContext(), ListTaskActivity.class);
        startActivity(intent);
    }


    public void clearList(View v){
        dbHelper.clearDB();
        update();
    }

    @Override
    protected void onResume() {
       // update();
        super.onResume();
    }

    void update(){
        values = dbHelper.getAllTask();
        adapter.dataChanged(values);
    }
}
