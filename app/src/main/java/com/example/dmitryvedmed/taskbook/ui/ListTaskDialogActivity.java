package com.example.dmitryvedmed.taskbook.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.ListTask;

public class ListTaskDialogActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    private ListTaskDialogRecyclerAdapter listTaskDialogRecyclerAdapter;
    private TextView head;
    private ListTask task;
    private DBHelper5 dbHelper5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_list_task_dialog);
        dbHelper5 = new DBHelper5(this);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        initView();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("TAG", "onNewIntent    action: " + intent.getAction()+" category: "+intent.getCategories());

        super.onNewIntent(intent);
    }

    private void initView() {


        //task = (ListTask) getIntent().getSerializableExtra("ListTask");

        task = (ListTask) dbHelper5.getTask(getIntent().getIntExtra("ListTaskId", 0));

        head = (TextView) findViewById(R.id.listTaskDialogHeadTextView);
        if(task.getHeadLine()==null||task.getHeadLine().length()==0)
            head.setVisibility(View.GONE);
        else
            head.setText(task.getHeadLine());



        recyclerView = (RecyclerView) findViewById(R.id.list_dialog_activity_recycler_view);
        listTaskDialogRecyclerAdapter = new ListTaskDialogRecyclerAdapter(task, ListTaskDialogActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listTaskDialogRecyclerAdapter);

    }

    public void ok(View v){
        Log.d("TAG", "clicccccccccccccccccccccccccccccck ok");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(task.getId());
        finish();
    }
    public void leave(View v){
        Log.d("TAG", "clicccccccccccccccccccccccccccccck leave");
        finish();
    }
}