package com.example.dmitryvedmed.taskbook.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.KeyEvent;
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
import com.example.dmitryvedmed.taskbook.logic.DBHelper;
import com.example.dmitryvedmed.taskbook.logic.SimpleNote;
import com.example.dmitryvedmed.taskbook.untils.Constants;
import com.example.dmitryvedmed.taskbook.untils.SingletonFonts;

import java.util.Calendar;

public class SimpleNoteActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener  {

    private EditText mHead, mText;
    private SimpleNote mNote;
    private String mCurrentKind;
    private Toolbar mToolbar;
    private String mRepeating;
    private AlertDialog mDialog;

    private Context mContext;

    private Button mSpinnerButtonTime, mSpinnerButtonDate, spinnerButtonRepeat;
    private Calendar mNotificationTime;
    private SharedPreferences sharedPreferences;
    private int mHours;
    private int mMinutes;
    Button mCancelNotification, mMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_note);
        mContext = this;

        loadPreferences();
        initView();
        initTask();
        initDate();
        mRepeating = "";

    }

    private void initDate() {
        mNotificationTime = Calendar.getInstance();
        mNotificationTime.add(Calendar.DAY_OF_MONTH, 1);
        mNotificationTime.set(Calendar.HOUR_OF_DAY, mHours);
        mNotificationTime.set(Calendar.MINUTE, mMinutes);
        mNotificationTime.set(Calendar.SECOND, 0);
    }

    private void loadPreferences(){
        sharedPreferences = this.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        mHours = sharedPreferences.getInt(Constants.MORNING_TIME_HOURS, 8);
        mMinutes = sharedPreferences.getInt(Constants.MORNING_TIME_MINUTES, 15);
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.st_toolbar);
        mToolbar.setTitle("");
        mToolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        mCancelNotification = (Button) findViewById(R.id.cancelNotifButton);
        mMenuButton = (Button) findViewById(R.id.menuButton);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mHead = (EditText) findViewById(R.id.headEditText);
        mText = (EditText) findViewById(R.id.taskEditText);

        mText.setTypeface(SingletonFonts.getInstance(this).getRobotoRegular());
        mHead.setTypeface(SingletonFonts.getInstance(this).getRobotoBold());

        mText.setTextSize(sharedPreferences.getInt(Constants.TASK_FONT_SIZE, 17));
        mHead.setTextSize(sharedPreferences.getInt(Constants.TASK_FONT_SIZE, 17));

        mToolbar.setNavigationIcon(R.drawable.ic_back);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }

    private void initTask() {
        mNote = (SimpleNote) getIntent().getSerializableExtra(Constants.TASK);
        if (mNote == null) {
            mNote = new SimpleNote();
            mNote.setId(-1);
            mNote.setPosition(getIntent().getIntExtra(Constants.POSITION, 0));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            mHead.requestFocus();
        }

        if(mNote.isRemind()){
            mCancelNotification.setVisibility(View.VISIBLE);
        } else {
            mCancelNotification.setVisibility(View.GONE);
        }

        if (mNote.getColor() != 0){
            mToolbar.setBackgroundColor(mNote.getColor());
            setWhiteNavIconColor();
        } else {
            setBlackNavIconColor();
        }

        mCurrentKind = getIntent().getStringExtra(Constants.KIND);
        if (mCurrentKind == null)
            mCurrentKind = Constants.UNDEFINED;

        mHead.setText(mNote.getHeadLine());
        mText.setText(mNote.getContent());

        mHead.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    mText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mHead.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    mText.requestFocus();
                }
                return false;
            }
        });

        mText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mText.getText().toString().equals(""))
                        mHead.requestFocus();
                }
                return false;
            }
        });

        mText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mText.setSelection(mText.getText().length());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setColor(View v){
        int color;

        switch (v.getId()){
            case R.id.green:
                color = ContextCompat.getColor(this, R.color.taskColorGreen);
                mToolbar.setBackgroundColor(color);
                setWhiteNavIconColor();
                mNote.setColor(color);
                break;
            case R.id.red:
                color = ContextCompat.getColor(this, R.color.taskColorRed);
                mToolbar.setBackgroundColor(color);
                setWhiteNavIconColor();
                mNote.setColor(color);
                break;
            case R.id.blue:
                color = ContextCompat.getColor(this, R.color.taskColorBlue);
                mToolbar.setBackgroundColor(color);
                setWhiteNavIconColor();
                mNote.setColor(color);
                break;
            case R.id.yellow:
                color = ContextCompat.getColor(this, R.color.taskColorYellow);
                mToolbar.setBackgroundColor(color);
                setWhiteNavIconColor();
                mNote.setColor(color);
                break;
            case R.id.white:
                mToolbar.setBackgroundColor(Color.WHITE);
                setBlackNavIconColor();
                mNote.setColor(0);
                break;
        }
        mDialog.dismiss();
    }

    private void setBlackNavIconColor(){
        int color = ContextCompat.getColor(this, android.R.color.black);
        mCancelNotification.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bell_outline_black_36dp));
        mMenuButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_dots_vertical_black_36dp));
        mToolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void setWhiteNavIconColor(){
        int color = ContextCompat.getColor(this, android.R.color.white);
        mCancelNotification.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bell_outline_white_36dp));
        mMenuButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_dots_vertical_white_36dp));
        mCancelNotification.setFocusable(false);
        mToolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void cancelNotification(){

        Intent intent1 = new Intent(getApplicationContext(), NotifyTaskReceiver.class);
        intent1.setAction(Constants.ACTION_NOTIFICATION);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), mNote.getId(), intent1, 0);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager1.cancel(sender);

        mNote.setRemind(false);
    }

    public void cancelNotification(View v){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // alert.setTitle("Очистить корзину?");
        alert.setMessage(R.string.question_delete_notification);

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelNotification();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(mNote.getId());
                mCancelNotification.setVisibility(View.GONE);
                mNote.setRepeating(false);
                saveTask(false);
            }
        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        final AlertDialog.Builder inform = new AlertDialog.Builder(this);
        inform.setTitle(getResources().getString(R.string.notification));

        if(mNote.getReminderTime() < System.currentTimeMillis()){
            long period = mNote.getRepeatingPeriod();
            while (mNote.getReminderTime() < System.currentTimeMillis()){
                mNote.setReminderTime(mNote.getReminderTime() + period);
            }
            saveTask(false);
        }

        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(mContext);
        String formattedTime = timeFormat.format(mNote.getReminderTime());

        java.text.DateFormat dateFormat = DateFormat.getDateFormat(mContext);
        String formattedDate = dateFormat.format(mNote.getReminderTime());


        String repeating = "\r\n" + getResources().getString(R.string.repeat)  + " : ";

        if(mNote.getRepeatingPeriod() == Constants.PERIOD_ONE_DAY){
            repeating = repeating + getResources().getString(R.string.every_day);
        } else if(mNote.getRepeatingPeriod() == Constants.PERIOD_WEEK){
            repeating = repeating + getResources().getString(R.string.every_week);
        } else if(mNote.getRepeatingPeriod() == Constants.PERIOD_MONTH){
            repeating = repeating + getResources().getString(R.string.every_month);
        } else {
            repeating = "";
        }

        inform.setMessage(getResources().getString(R.string.date) + " : " + formattedDate + "\r\n" +
                getResources().getString(R.string.time) + " : " + formattedTime + repeating);

        inform.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        inform.setNegativeButton(R.string.act_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.show();
            }
        });
        AlertDialog informDialog = inform.create();
        informDialog.show();
    }


    public void menuButton(View v){

        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu_note);
        if(sharedPreferences.getString(Constants.CURRENT_KIND, Constants.UNDEFINED).equals(Constants.DELETED)){
            MenuItem notify = popupMenu.getMenu().findItem(R.id.notify);
            notify.setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.set_color2:
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(R.layout.dialog_choose_color);
                        mDialog = builder.create();
                        mDialog.show();
                        break;
                    case R.id.notify:
                        createDialog();
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void createDialog() {

        mRepeating = "";

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);

        View mView = getLayoutInflater().inflate(R.layout.dialog_spiner, null);

        mSpinnerButtonTime = (Button) mView.findViewById(R.id.spinnerButtonTime);
        mSpinnerButtonTime.setText(R.string.morning);
        mSpinnerButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.inflate(R.menu.popup_time);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(SimpleNoteActivity.this);
            }
        });

        mSpinnerButtonDate = (Button) mView.findViewById(R.id.spinnerButtonDate);
        mSpinnerButtonDate.setText(R.string.tomorrow);
        mSpinnerButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.inflate(R.menu.popup_date);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(SimpleNoteActivity.this);
            }
        });


        spinnerButtonRepeat = (Button) mView.findViewById(R.id.spinnerButtonRepeat);
        spinnerButtonRepeat.setText(R.string.never);
        spinnerButtonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.inflate(R.menu.popup_repeat);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(SimpleNoteActivity.this);
            }
        });


        mBuilder.setTitle(R.string.notification);
        mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final Intent intent = new Intent(getApplicationContext(), NotifyTaskReceiver.class);
                intent.setAction(Constants.ACTION_NOTIFICATION);
                saveTask(true);
                intent.putExtra(Constants.ID, mNote.getId());

                final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if(mRepeating.equals("")) {

                    mNote.setRepeatingPeriod(0);
                    mNote.setRepeating(false);

                    if(mNotificationTime.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                        mBuilder.setMessage(R.string.reminder_past_time);
                        mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), mNote.getId(), intent, 0);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                            }
                        });

                        mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        return;
                    }

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), mNote.getId(), intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, mNotificationTime.getTimeInMillis(), pendingIntent);
                } else {

                    intent.putExtra(Constants.REPEATING, true);
                    intent.putExtra(Constants.PERIOD, mRepeating);

                    mNote.setRepeating(true);

                    switch (mRepeating){
                        case Constants.EVERY_DAY:
                            mNote.setRepeatingPeriod(Constants.PERIOD_ONE_DAY);
                            while (mNotificationTime.getTimeInMillis() < System.currentTimeMillis()) {
                                mNotificationTime.add(Calendar.DAY_OF_MONTH, 1);
                            }
                            PendingIntent pi1 = PendingIntent.getBroadcast(getApplicationContext(), mNote.getId(), intent, 0);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mNotificationTime.getTimeInMillis(), Constants.PERIOD_ONE_DAY, pi1);
                            break;
                        case Constants.EVERY_WEEK:
                            mNote.setRepeatingPeriod(Constants.PERIOD_WEEK);
                            while (mNotificationTime.getTimeInMillis() < System.currentTimeMillis()) {
                                mNotificationTime.add(Calendar.WEEK_OF_MONTH, 1);
                            }
                            PendingIntent pi2 = PendingIntent.getBroadcast(getApplicationContext(), mNote.getId(), intent, 0);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mNotificationTime.getTimeInMillis(), Constants.PERIOD_WEEK, pi2);
                            break;
                        case Constants.EVERY_MONTH:
                            mNote.setRepeatingPeriod(Constants.PERIOD_MONTH);
                            while (mNotificationTime.getTimeInMillis() < System.currentTimeMillis()) {
                                mNotificationTime.add(Calendar.MONTH, 1);
                            }
                            PendingIntent pi3 = PendingIntent.getBroadcast(getApplicationContext(), mNote.getId(), intent, 0);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mNotificationTime.getTimeInMillis(), Constants.PERIOD_MONTH, pi3);
                            break;
                    }
                }

                mNote.setRemind(true);
                mNote.setReminderTime(mNotificationTime.getTimeInMillis());
                saveTask(false);
                mCancelNotification.setVisibility(View.VISIBLE);
            }
        });

        mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }


    private void saveTask(boolean check) {
        String headline = String.valueOf(mHead.getText());
        String content = String.valueOf(mText.getText());

        mNote.setHeadLine(headline);
        mNote.setContent(content);


        DBHelper dbHelper = new DBHelper(this);
        if (mNote.getId() == -1) {
            if (mHead.getText().length() == 0 && mText.getText().length() == 0)
                return;
            mNote.setId(dbHelper.addTask(mNote, mCurrentKind));
        } else {
            if(check)
                if(!dbHelper.isRemind(mNote))
                    mNote.setRemind(false);
            dbHelper.updateTask(mNote, mCurrentKind);
        }
    }

    @Override
    protected void onPause() {

        saveTask(true);
        super.onPause();
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

                mNotificationTime.set(Calendar.SECOND, 0);
                mNotificationTime.set(Calendar.HOUR_OF_DAY, h);
                mNotificationTime.set(Calendar.MINUTE, m);

                long firstTime = calendar.getTimeInMillis();


                mNote.setRemind(true);
                mNote.setReminderTime(firstTime);
                saveTask(false);

                java.text.DateFormat timeFormat = DateFormat.getTimeFormat(mContext);
                String formattedTime = timeFormat.format(mNotificationTime.getTimeInMillis());
                mSpinnerButtonTime.setText(formattedTime);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mNotificationTime.set(year, month, day);

                java.text.DateFormat dateFormat = DateFormat.getDateFormat(mContext);
                String formattedDate = dateFormat.format(mNotificationTime.getTimeInMillis());
                mSpinnerButtonDate.setText(formattedDate);
            }
        }, mNotificationTime.get(Calendar.YEAR), mNotificationTime.get(Calendar.MONTH), mNotificationTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.item_morning:
                mSpinnerButtonTime.setText(getResources().getString(R.string.morning));
                mHours = sharedPreferences.getInt(Constants.MORNING_TIME_HOURS, 14);
                mMinutes = sharedPreferences.getInt(Constants.MORNING_TIME_MINUTES, 15);
                mNotificationTime.set(Calendar.HOUR_OF_DAY, mHours);
                mNotificationTime.set(Calendar.MINUTE, mMinutes);
                break;
            case R.id.item_afternoon:
                mSpinnerButtonTime.setText(getResources().getString(R.string.afternoon));
                mHours = sharedPreferences.getInt(Constants.AFTERNOON_TIME_HOURS, 14);
                mMinutes = sharedPreferences.getInt(Constants.AFTERNOON_TIME_MINUTES, 15);
                mNotificationTime.set(Calendar.HOUR_OF_DAY, mHours);
                mNotificationTime.set(Calendar.MINUTE, mMinutes);
                break;
            case R.id.item_evening:
                mSpinnerButtonTime.setText(getResources().getString(R.string.evening));
                mHours = sharedPreferences.getInt(Constants.EVENING_TIME_HOURS, 20);
                mMinutes = sharedPreferences.getInt(Constants.EVENING_TIME_MINUTES, 15);
                mNotificationTime.set(Calendar.HOUR_OF_DAY, mHours);
                mNotificationTime.set(Calendar.MINUTE, mMinutes);
                break;
            case R.id.item_chose_time:
                showTimePickerDialog();
                break;
            case R.id.item_today:
                mNotificationTime = Calendar.getInstance();
                mNotificationTime.set(Calendar.HOUR_OF_DAY, mHours);
                mNotificationTime.set(Calendar.MINUTE, mMinutes);
                mSpinnerButtonDate.setText(getResources().getString(R.string.today));
                break;
            case R.id.item_tomorrow:
                mNotificationTime = Calendar.getInstance();
                mNotificationTime.set(Calendar.HOUR_OF_DAY, mHours);
                mNotificationTime.set(Calendar.MINUTE, mMinutes);
                mNotificationTime.add(Calendar.DAY_OF_MONTH, 1);
                mSpinnerButtonDate.setText(getResources().getString(R.string.tomorrow));
                break;
            case R.id.item_chose_date:
                showDatePickerDialog();
                break;
            case R.id.item_every_day:
                mRepeating = Constants.EVERY_DAY;
                spinnerButtonRepeat.setText(R.string.every_day);
                break;
            case R.id.item_every_week:
                mRepeating = Constants.EVERY_WEEK;
                spinnerButtonRepeat.setText(R.string.every_week);
                break;
            case R.id.item_every_month:
                mRepeating = Constants.EVERY_MONTH;
                spinnerButtonRepeat.setText(R.string.every_month);
                break;
            case R.id.item_newer:
                mRepeating = "";
                break;
        }
        return false;
    }
}