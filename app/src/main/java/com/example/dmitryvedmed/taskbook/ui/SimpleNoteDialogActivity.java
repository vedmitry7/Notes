package com.example.dmitryvedmed.taskbook.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.logic.DBHelper;
import com.example.dmitryvedmed.taskbook.logic.SimpleNote;
import com.example.dmitryvedmed.taskbook.untils.Constants;
import com.example.dmitryvedmed.taskbook.untils.SingletonFonts;

public class SimpleNoteDialogActivity extends AppCompatActivity {

    private TextView mHead, mContext;
    private SimpleNote mNote;
    private DBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new DBHelper(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.simple_note_dialog);
        initView();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    private void initView() {
        mHead = (TextView) findViewById(R.id.headTextView);
        mContext = (TextView) findViewById(R.id.contextTextView);
        mContext.setTypeface(SingletonFonts.getInstance(this).getRobotoRegular());
        mHead.setTypeface(SingletonFonts.getInstance(this).getRobotoBold());

        mNote = (SimpleNote) mDbHelper.getTask(getIntent().getIntExtra(Constants.ID, 0));
        if(mNote.getHeadLine().length()>0)
            mHead.setText(mNote.getHeadLine());
        else
            mHead.setVisibility(View.GONE);
        mContext.setText(mNote.getContext());


        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
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

        Intent intent = new Intent(this, SimpleNoteActivity.class);
        intent.putExtra(Constants.TASK, mNote);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

}
