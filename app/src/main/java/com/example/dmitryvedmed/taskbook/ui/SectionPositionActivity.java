package com.example.dmitryvedmed.taskbook.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.helper.SectionTouchHelperCallback;
import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.Section;

import java.util.List;

public class SectionPositionActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private SectionPositionRecyclerAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback callback;
    private DBHelper5 dbHelper5;
    private List<Section> sections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_position);

        initView();

    }

    private void initView() {
        dbHelper5 = new DBHelper5(this);
        sections = dbHelper5.getAllSections();

        recyclerView = (RecyclerView) findViewById(R.id.section_position_recycler_view);
        adapter = new SectionPositionRecyclerAdapter(sections);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        callback = new SectionTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }
}
