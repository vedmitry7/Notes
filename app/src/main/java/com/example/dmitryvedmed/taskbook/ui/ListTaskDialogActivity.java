package com.example.dmitryvedmed.taskbook.ui;

import android.app.NotificationManager;
import android.content.Context;
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
import com.example.dmitryvedmed.taskbook.logic.ListTask;

public class ListTaskDialogActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    private ListTaskDialogRecyclerAdapter listTaskDialogRecyclerAdapter;
    private TextView head;
    private ListTask task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task_dialog);

        initView();

    }

    private void initView() {

        task = (ListTask) getIntent().getSerializableExtra("ListTask");

        head = (TextView) findViewById(R.id.listTaskDialogHeadTextView);
        if(task.getHeadLine()==null||task.getHeadLine().length()==0)
            head.setVisibility(View.GONE);
        else
            head.setText(task.getHeadLine());

        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);

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