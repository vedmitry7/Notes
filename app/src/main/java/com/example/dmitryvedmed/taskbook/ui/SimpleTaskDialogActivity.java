package com.example.dmitryvedmed.taskbook.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.SimpleTask;
import com.example.dmitryvedmed.taskbook.untils.Constants;

public class SimpleTaskDialogActivity extends AppCompatActivity {

    private TextView head, context;
    private SimpleTask task;
    private DBHelper5 dbHelper5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper5 = new DBHelper5(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_simple_task_dialog);
        initView();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    private void initView() {
        head = (TextView) findViewById(R.id.headTextView);
        context = (TextView) findViewById(R.id.contextTextView);

        //task = (SimpleTask) getIntent().getSerializableExtra("Task");

        task = (SimpleTask) dbHelper5.getTask(getIntent().getIntExtra(Constants.ID, 0));
        if(task.getHeadLine().length()>0)
            head.setText(task.getHeadLine());
        else
            head.setVisibility(View.GONE);
        context.setText(task.getContext());


        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
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

    public void edit(View v){
        Log.d("TAG", "clicccccccccccccccccccccccccccccck edit");

        Intent intent = new Intent(this, SimpleTaskActivity.class);
        intent.putExtra("Task", task);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

}
