package com.example.dmitryvedmed.taskbook.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.dmitryvedmed.taskbook.NotifyTaskReceiver;
import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.helper.ListTaskItemTouchHelperCallback;
import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.ListTask;
import com.example.dmitryvedmed.taskbook.untils.Constants;

import java.util.Calendar;


public class ListTaskActivity extends AppCompatActivity {

    private ListTask task;
    public static RecyclerView recyclerView;
    private ListTaskRecyclerAdapter listTaskRecyclerAdapter;
    private Context context;
    private EditText headList;
    private String currentKind;
    private Toolbar toolbar;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback callback;

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
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

 /*       headList = (EditText) findViewById(R.id.listHeadEditText);

        headList.setTypeface(SingletonFonts.getInstance(this).getRobotoBold());
        headList.setText(task.getHeadLine());
        headList.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if( keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                   listTaskRecyclerAdapter.setFocusToEditText();
                    return true;
                }
                return false;
            }
        });*/

        recyclerView = (RecyclerView) findViewById(R.id.list_activity_recycler_view);

        listTaskRecyclerAdapter = new ListTaskRecyclerAdapter(task, ListTaskActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listTaskRecyclerAdapter);


        callback = new ListTaskItemTouchHelperCallback(listTaskRecyclerAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        if(task.getColor() != 0)
            toolbar.setBackgroundColor(task.getColor());

        setItemMovement(false);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cek", "home selected");
                finish();
            }
        });

    }

    private void initTask() {
        task = (ListTask) getIntent().getSerializableExtra("ListTask");
        if(task == null) {
            System.out.println("LIST TASK = NULL!");
            task = new ListTask();
            task.setId(-1);
            task.setPosition(getIntent().getIntExtra("position", 0));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        else{
            System.out.println("LIST TASK != NULL, id = " + task.getHeadLine());
            Log.d("TAG", "COLOR - " + task.getColor());
        }
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
        int color;
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.green:
                color = ContextCompat.getColor(this, R.color.taskColorGreen);
                toolbar.setBackgroundColor(color);
                task.setColor(color);
                Log.d("TAG", "COLOR " + color);
                break;
            case R.id.red:
                color = ContextCompat.getColor(this, R.color.taskColorRed);
                toolbar.setBackgroundColor(color);
                task.setColor(color);
                Log.d("TAG", "COLOR " + color);
                break;
            case R.id.blue:
                color = ContextCompat.getColor(this, R.color.taskColorBlue);
                toolbar.setBackgroundColor(color);
                task.setColor(color);
                Log.d("TAG", "COLOR " + color);
                break;
            case R.id.yellow:
                color = ContextCompat.getColor(this, R.color.taskColorYellow);
                toolbar.setBackgroundColor(color);
                task.setColor(color);
                Log.d("TAG", "COLOR " + color);
                break;
            case R.id.white:
                toolbar.setBackgroundColor(Color.WHITE);
                task.setColor(0);
                break;
            case R.id.notify:
          /*      Intent intent = new Intent("TASK_NOTIFICATION");
                saveTask();
                intent.putExtra("id", task.getId());
                sendBroadcast(intent);
                */

                final Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {

                        calendar.set(Calendar.HOUR_OF_DAY, h);
                        calendar.set(Calendar.MINUTE, m);
                        calendar.set(Calendar.SECOND, 0);

                        Log.d("TAG", "TIME: " + h + ":" + m);

                        long firstTime = calendar.getTimeInMillis();

                        Intent intent = new Intent(getApplicationContext(), NotifyTaskReceiver.class);
                        intent.setAction("TASK_NOTIFICATION");
                        saveTask(true);
                        intent.putExtra("id", task.getId());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),task.getId(), intent, 0);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, pendingIntent);

                        task.setRemind(true);
                        task.setReminderTime(firstTime);
                        saveTask(false);

                       // cancelNotification();

                        //alarmManager1.cancel(pendingIntent);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_colors, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void cancelNotification(){

        Intent intent1 = new Intent(getApplicationContext(), NotifyTaskReceiver.class);
        intent1.setAction("TASK_NOTIFICATION");
        //intent1.putExtra("id", task.getId());
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), task.getId(), intent1, 0);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager1.cancel(sender);

        task.setRemind(false);
    }

    private void saveTask(boolean check) {
        task = listTaskRecyclerAdapter.getListTask();

       // task.setHeadLine(headList.getText().toString());

        DBHelper5 dbHelper = new DBHelper5(this);
        if(task.getId() == -1){
            task.setId(dbHelper.addTask(task));
        }
        else {
            if(check)
                if(!dbHelper.isRemind(task))
                    task.setRemind(false);
            dbHelper.updateTask(task, currentKind);
        }
    }

    public void scroll(final int position){
       /* recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "SCROLL  " + position);
                recyclerView.scrollToPosition(position);
            }
        }, 100);
        //recyclerView.scrollToPosition(position);
        //recyclerView.smoothScrollToPosition(position+5);*/
    }

    public void setItemMovement(boolean b){
        Log.d("TAG", "MOVEMENT  " + b);
        ((ListTaskItemTouchHelperCallback)callback).setCanMovement(b);
    }

    @Override
    protected void onPause() {
       saveTask(true);
        super.onPause();
    }
}
