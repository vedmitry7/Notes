package com.example.dmitryvedmed.taskbook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

public class MainActivity2 extends AppCompatActivity {


    List<Task> values;
    public static DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        dbHelper = new DBHelper(this);
        values = dbHelper.getAllTask();
        initView();
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        RecyclerView.Adapter adapter = new RecyclerAdapter(values, MainActivity2.this);

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
}
