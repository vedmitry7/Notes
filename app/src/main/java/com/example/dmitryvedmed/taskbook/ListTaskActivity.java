package com.example.dmitryvedmed.taskbook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;


public class ListTaskActivity extends AppCompatActivity {

    private ListTask listTask;
    public static RecyclerView recyclerView;
    private ListTaskRecyclerAdapter listTaskRecyclerAdapter;
    private Context context;
    private EditText headList;
    private String currentKind;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        context = this;
        initTask();
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.lt_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            listTask.setPosition(getIntent().getIntExtra("position", 0));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        else
            System.out.println("LIST TASK != NULL, id = " + listTask.getHeadLine());
        currentKind = getIntent().getStringExtra("kind");
        if(currentKind==null)
            currentKind = Constants.UNDEFINED;
    }


    public void hideDefaultKeyboard() {
        Activity activity = (Activity) context;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//u have got lot of methods here
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_colors, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onPause() {
        listTask = listTaskRecyclerAdapter.getListTask();
        System.out.println("UN");
        for (String s:listTask.getUncheckedTasks()
                ) {
            System.out.println(s);
        }
        System.out.println("CHECK");
        for (String s:listTask.getCheckedTasks()
                ) {
            System.out.println(s);
        }
        listTask.setHeadLine(headList.getText().toString());
        DBHelper5 dbHelper = new DBHelper5(this);
        if(listTask.getId() == -1)
           listTask.setId(dbHelper.addTask(listTask));
        else dbHelper.updateTask(listTask, currentKind);
        super.onPause();
    }
}
