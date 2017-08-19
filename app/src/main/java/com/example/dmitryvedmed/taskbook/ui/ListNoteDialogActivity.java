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
import com.example.dmitryvedmed.taskbook.logic.DBHelper;
import com.example.dmitryvedmed.taskbook.logic.ListNote;
import com.example.dmitryvedmed.taskbook.untils.Constants;
import com.example.dmitryvedmed.taskbook.untils.SingletonFonts;

public class ListNoteDialogActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    private ListNoteDialogRecyclerAdapter listNoteDialogRecyclerAdapter;
    private TextView head;
    private ListNote task;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.list_note_dialog);
        dbHelper = new DBHelper(this);
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
        Log.d("TAG", "onNewIntent    action: " + intent.getAction() + " category: "+intent.getCategories());

        super.onNewIntent(intent);
    }

    private void initView() {


        //task = (ListNote) getIntent().getSerializableExtra("ListNote");

        task = (ListNote) dbHelper.getTask(getIntent().getIntExtra(Constants.ID, 0));

        head = (TextView) findViewById(R.id.listTaskDialogHeadTextView);
        head.setTypeface(SingletonFonts.getInstance(this).getRobotoRegular());

        if(task.getHeadLine()==null||task.getHeadLine().length()==0)
            head.setVisibility(View.GONE);
        else
            head.setText(task.getHeadLine());



        recyclerView = (RecyclerView) findViewById(R.id.list_dialog_activity_recycler_view);
        listNoteDialogRecyclerAdapter = new ListNoteDialogRecyclerAdapter(task, ListNoteDialogActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listNoteDialogRecyclerAdapter);

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

        Intent intent1 = new Intent(this, ListNoteActivity.class);
        intent1.putExtra(Constants.LIST_TASK, task);
        this.startActivity(intent1);
        this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }
}