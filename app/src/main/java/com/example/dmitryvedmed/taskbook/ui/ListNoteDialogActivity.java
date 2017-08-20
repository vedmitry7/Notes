package com.example.dmitryvedmed.taskbook.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    public static RecyclerView sRecyclerView;
    private ListNoteDialogRecyclerAdapter mListNoteDialogRecyclerAdapter;
    private TextView mHead;
    private ListNote mNote;
    private DBHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.list_note_dialog);
        mDbHelper = new DBHelper(this);
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

        super.onNewIntent(intent);
    }

    private void initView() {


        //mNote = (ListNote) getIntent().getSerializableExtra("ListNote");

        mNote = (ListNote) mDbHelper.getTask(getIntent().getIntExtra(Constants.ID, 0));

        mHead = (TextView) findViewById(R.id.listTaskDialogHeadTextView);
        mHead.setTypeface(SingletonFonts.getInstance(this).getRobotoRegular());

        if(mNote.getHeadLine()==null|| mNote.getHeadLine().length()==0)
            mHead.setVisibility(View.GONE);
        else
            mHead.setText(mNote.getHeadLine());



        sRecyclerView = (RecyclerView) findViewById(R.id.list_dialog_activity_recycler_view);
        mListNoteDialogRecyclerAdapter = new ListNoteDialogRecyclerAdapter(mNote, ListNoteDialogActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        sRecyclerView.setLayoutManager(layoutManager);

        sRecyclerView.setHasFixedSize(true);
        sRecyclerView.setAdapter(mListNoteDialogRecyclerAdapter);

    }

    public void ok(View v){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mNote.getId());
        finish();
    }
    public void leave(View v){
        finish();
    }

    public void edit(View v){

        Intent intent1 = new Intent(this, ListNoteActivity.class);
        intent1.putExtra(Constants.LIST_TASK, mNote);
        this.startActivity(intent1);
        this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }
}