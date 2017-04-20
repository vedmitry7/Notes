package com.example.dmitryvedmed.taskbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.dmitryvedmed.taskbook.helper.SimpleItemTouchHelperCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<SuperTask> values;
    public static DBHelper4 dbHelper;
    public static RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper4(this);
        values = dbHelper.getAllTask();
        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_common);

        adapter = new MainRecyclerAdapter(values, MainActivity.this);

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

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void newSimpleTask(View v){
        Intent intent = new Intent(getApplicationContext(), SimpleTaskActivity.class);
        startActivity(intent);
    }

   public void newListTask(View v){
        Intent intent = new Intent(getApplicationContext(), ListTaskActivity.class);
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
        values = dbHelper.getAllTask();
        adapter.dataChanged(values);
    }
}
