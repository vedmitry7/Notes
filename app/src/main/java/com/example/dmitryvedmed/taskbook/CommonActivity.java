package com.example.dmitryvedmed.taskbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

public class CommonActivity extends AppCompatActivity {
    List<SuperTask> values;
    public static DBHelper4 dbHelper;
    public static RecyclerView recyclerView;
    private CommonRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        dbHelper = new DBHelper4(this);
       // values = dbHelper.getAllTask();
        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);



       // adapter = new RecyclerAdapter(values, CommonActivity.this);

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
        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        startActivity(intent);
    }


    public void clearList(View v){
        dbHelper.clearDB();
        update();
    }

    @Override
    protected void onResume() {
        update();
        super.onResume();
    }

    void update(){
       // values = dbHelper.getAllTask();
      //  adapter.dataChanged(values);
    }
}
