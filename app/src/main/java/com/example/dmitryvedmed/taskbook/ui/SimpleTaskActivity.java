package com.example.dmitryvedmed.taskbook.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.dmitryvedmed.taskbook.NotifyTaskReceiver;
import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.SimpleTask;
import com.example.dmitryvedmed.taskbook.untils.Constants;
import com.example.dmitryvedmed.taskbook.untils.SingletonFonts;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;

import java.util.Calendar;

public class SimpleTaskActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener  {

    private EditText head, text;
    private SimpleTask task;
    private String currentKind;
    private Toolbar toolbar;
    Context context;

    private Button spinnerButtonTime, spinnerButtonDate, spinnerButtonRepeat;
    private Calendar notificationTime;
    private SharedPreferences sharedPreferences;
    int hours;
    int minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_task);
        context = this;

        initView();
        initTask();
        initDate();
        loadPreferences();
    }

    private void initDate() {
        notificationTime = Calendar.getInstance();
        notificationTime.add(Calendar.DAY_OF_MONTH, 1);
        notificationTime.set(Calendar.HOUR_OF_DAY, hours);
        notificationTime.set(Calendar.MINUTE, minutes);
        notificationTime.set(Calendar.SECOND, 0);
    }

    private void loadPreferences(){
        sharedPreferences = this.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.getString("123","321").equals("321")) {
            editor.putInt("morning_time_hours", 7);
            editor.putInt("morning_time_minutes", 0);
            editor.putInt("afternoon_time_hours", 13);
            editor.putInt("afternoon_time_minutes", 0);
            editor.putInt("evening_time_hours", 19);
            editor.putInt("evening_time_minutes", 0);
        }

        hours = sharedPreferences.getInt("morning_time_hours", 8);
        minutes = sharedPreferences.getInt("morning_time_minutes", 15);
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
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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
                createDialog();
                    break;
            case R.id.cancel_notification:
                cancelNotification();
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
    private void createDialog() {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

        View mViewe = getLayoutInflater().inflate(R.layout.dialog_spiner, null);

        spinnerButtonTime = (Button) mViewe.findViewById(R.id.spinnerButtonTime);
        spinnerButtonTime.setText(getResources().getString(R.string.morning));
        spinnerButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.popup_time);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(SimpleTaskActivity.this);
            }
        });

        spinnerButtonDate = (Button) mViewe.findViewById(R.id.spinnerButtonDate);
        spinnerButtonDate.setText(getResources().getString(R.string.tomorrow));
        spinnerButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.popup_date);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(SimpleTaskActivity.this);
            }
        });


        spinnerButtonRepeat = (Button) mViewe.findViewById(R.id.spinnerButtonRepeat);
        spinnerButtonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.popup_repeat);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(SimpleTaskActivity.this);
            }
        });


        mBuilder.setTitle("Напоминание");
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("TAG", "----------------------OK");
                Log.d("TAG", String.valueOf(notificationTime.get(Calendar.YEAR)));
                Log.d("TAG", String.valueOf(notificationTime.get(Calendar.MONTH)));
                Log.d("TAG", String.valueOf(notificationTime.get(Calendar.DAY_OF_MONTH)));
                Log.d("TAG", String.valueOf(notificationTime.get(Calendar.HOUR_OF_DAY)));
                Log.d("TAG", String.valueOf(notificationTime.get(Calendar.MINUTE)));
                Log.d("TAG", "----------------------");

                Intent intent = new Intent(getApplicationContext(), NotifyTaskReceiver.class);
                intent.setAction("TASK_NOTIFICATION");
                saveTask(true);
                intent.putExtra("id", task.getId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),task.getId(), intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), pendingIntent);

                task.setRemind(true);
                task.setReminderTime(notificationTime.getTimeInMillis());
                saveTask(false);


            }
        });

        mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        mBuilder.setView(mViewe);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }


    private void saveTask(boolean check) {
        String headline = String.valueOf(head.getText());
        String content = String.valueOf(text.getText());

        Log.d("TAG", "SAVE  !!! " + headline + " " + content);
        Log.d("TAG", "SAVE id = " + task.getId() + "???");

        task.setHeadLine(headline);
        task.setContext(content);


        DBHelper5 dbHelper = new DBHelper5(this);
        if (task.getId() == -1) {
            if (head.getText().length() == 0 && text.getText().length() == 0)
                return;
            task.setId(dbHelper.addTask(task));
            Log.d("TAG", "NEW TASK ID = " + task.getId());
        } else {
            Log.d("TAG", "UPDATE id = " + task.getId());
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

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void showTimePickerDialog(){

        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {

                notificationTime.set(Calendar.HOUR_OF_DAY, h);
                notificationTime.set(Calendar.MINUTE, m);
                notificationTime.set(Calendar.SECOND, 0);

                Log.d("TAG", "TIME: " + h + ":" + m);

                long firstTime = calendar.getTimeInMillis();


                task.setRemind(true);
                task.setReminderTime(firstTime);
                saveTask(false);


                String time = notificationTime.get(Calendar.HOUR_OF_DAY) + ":" + notificationTime.get(Calendar.MINUTE);

                spinnerButtonTime.setText(time);
                // cancelNotification();

                //alarmManager1.cancel(pendingIntent);
            }
        }, hour, minute, true);
        timePickerDialog.show();

    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                notificationTime.set(year,month,day);
                spinnerButtonDate.setText(day+":"+month+";"+year);
            }
        },notificationTime.get(Calendar.YEAR),notificationTime.get(Calendar.MONTH), notificationTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.item_morning:
                spinnerButtonTime.setText(getResources().getString(R.string.morning));

                break;
            case R.id.item_afternoon:
                spinnerButtonTime.setText(getResources().getString(R.string.afternoon));
                hours = sharedPreferences.getInt("afternoon_time_hours", 14);
                minutes = sharedPreferences.getInt("afternoon_time_minutes", 15);
                break;
            case R.id.item_evening:
                spinnerButtonTime.setText(getResources().getString(R.string.evening));
                hours = sharedPreferences.getInt("evening_time_hours", 20);
                minutes = sharedPreferences.getInt("evening_time_minutes", 15);
                break;
            case R.id.item_chose_time:
                showTimePickerDialog();
                break;
            case R.id.item_yesterday:
                notificationTime = Calendar.getInstance();
                notificationTime.set(Calendar.HOUR_OF_DAY, hours);
                notificationTime.set(Calendar.MINUTE, minutes);
                Log.d("TAG", "DAY - " + String.valueOf(notificationTime.get(Calendar.DAY_OF_MONTH)));
                spinnerButtonDate.setText(getResources().getString(R.string.testerday));

                break;

            case R.id.item_tomorrow:
                notificationTime = Calendar.getInstance();
                notificationTime.set(Calendar.HOUR_OF_DAY, hours);
                notificationTime.set(Calendar.MINUTE, minutes);
                notificationTime.add(Calendar.DAY_OF_MONTH, 1);
                spinnerButtonDate.setText(getResources().getString(R.string.tomorrow));

                break;
            case R.id.item_chose_date:
                showDatePickerDialog();
                break;
            case R.id.item_every_day:

                break;
            case R.id.item_every_week:

                break;
            case R.id.item_newer:

                break;
        }
        return false;
    }
}