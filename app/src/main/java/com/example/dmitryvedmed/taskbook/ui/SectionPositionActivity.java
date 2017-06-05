package com.example.dmitryvedmed.taskbook.ui;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

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


        Toolbar toolbar = (Toolbar) findViewById(R.id.section_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  toolbar.setTitle(R.string.settings);
        getSupportActionBar().setTitle(R.string.settings);

        int color = ContextCompat.getColor(this, R.color.common_google_signin_btn_text_dark);
        toolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {

        sections = adapter.getSections();
        for (Section s : sections
                ) {
            dbHelper5.updateSection(s);
        }
        super.onPause();
    }

}
