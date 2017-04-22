package com.example.dmitryvedmed.taskbook.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.dmitryvedmed.taskbook.NotifyTaskReceiver;
import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.SimpleTask;
import com.example.dmitryvedmed.taskbook.untils.Constants;
import com.example.dmitryvedmed.taskbook.untils.SingletonFonts;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;

public class SimpleTaskActivity extends AppCompatActivity {

    private int id;
    private EditText head, text;
    private SimpleTask task;
    private String currentKind;
    Color color;
    private Toolbar toolbar;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_task);

        initView();
        initTask();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.st_toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.taskColorGreen)));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        head = (EditText) findViewById(R.id.headEditText);
        text = (EditText) findViewById(R.id.taskEditText);



        text.setTypeface(SingletonFonts.getInstance(this).getRobotoRegular());
        head.setTypeface(SingletonFonts.getInstance(this).getRobotoBold());

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
        task = (SimpleTask) getIntent().getSerializableExtra("Task");
        if (task == null) {
            task = new SimpleTask();
            task.setId(-1);
            task.setPosition(getIntent().getIntExtra("position", 0));
            Log.d("TAG", "TAAAAAAASKA  NEEEET");
            Log.d("TAG", "ID = " + task.getId());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            head.requestFocus();
        } else {
            Log.d("TAG", "TAAAAAAASK EST'!!!!!!!!");
            Log.d("TAG", "ID = " + task.getId());
            if (task.getColor() != 0)
                toolbar.setBackgroundColor(task.getColor());
        }

        currentKind = getIntent().getStringExtra("kind");
        if (currentKind == null)
            currentKind = Constants.UNDEFINED;

        // Log.d("TAG", "Eah, I've get task - " + String.valueOf(id));

      /*  if(id==-1){
            task = new Task();
            task.setId(id);
            return;
        }*/
        // task = dbHelper.getTask(id);
        head.setText(task.getHeadLine());
        text.setText(task.getContext());

        head.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d("TAG", "                                  ENTER!");
                    text.requestFocus();
                    return true;
                }
                return false;
            }
        });

        head.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    text.requestFocus();
                }
                return false;
            }
        });

       /* text.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if( keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL
                        && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                    Log.d("TAG", "                                  DEL!" + text.getText());

                    if(text.getText().toString().equals("")){
                        head.requestFocus();
                    }
                    return true;
                }
                return true;
            }
        });*/
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (text.getText().toString().equals(""))
                        head.requestFocus();
                }
                return false;
            }
        });

    /*    text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "EDITTWXT                  CLICK");
                text.setSelection(text.getText().length());
            }
        });*/
        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Always use a TextKeyListener when clearing a TextView to prevent android
                    // warnings in the log
                    Log.d("TAG", "EDITTWXT                  HAS FOKUS");
                    text.setSelection(text.getText().length());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int color;
        switch (item.getItemId()) {
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
                        alarmManager.set(AlarmManager.RTC_WAKEUP, firstTime, pendingIntent);

                        task.setRemind(true);
                        task.setReminderTime(firstTime);
                        saveTask(false);

                       //cancelNotification();

                        //alarmManager1.cancel(pendingIntent);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
                    break;
        }
        return super.onOptionsItemSelected(item);
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
        String headline = String.valueOf(head.getText());
        String content = String.valueOf(text.getText());

        Log.d("TAG", "SAVE  !!! " + headline + " " + content);
        Log.d("TAG", "SAVE id = " + id + "???");

        task.setHeadLine(headline);
        task.setContext(content);


        DBHelper5 dbHelper = new DBHelper5(this);
        if (task.getId() == -1) {
            if (head.getText().length() == 0 && text.getText().length() == 0)
                return;
            id = dbHelper.addTask(task);
            task.setId(id);
            Log.d("TAG", "NEW TASK ID = " + id);
        } else {
            Log.d("TAG", "UPDATE id = " + id);
            if(check)
                if(!dbHelper.isRemind(task))
                    task.setRemind(false);
            dbHelper.updateTask(task, currentKind);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_colors, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        saveTask(true);
        super.onPause();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Task Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}