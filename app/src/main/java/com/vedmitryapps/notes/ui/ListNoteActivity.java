package com.vedmitryapps.notes.ui;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TimePicker;

import com.vedmitryapps.notes.NotifyTaskReceiver;
import com.vedmitryapps.notes.R;
import com.vedmitryapps.notes.helper.ListNoteItemTouchHelperCallback;
import com.vedmitryapps.notes.logic.DBHelper;
import com.vedmitryapps.notes.logic.ListNote;
import com.vedmitryapps.notes.untils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ListNoteActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ListNote mNote;
    public static RecyclerView sRecyclerView;
    private ListNoteRecyclerAdapter mListNoteRecyclerAdapter;
    private Context mContext;
    private String mCurrentKind;
    private Toolbar mToolbar;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback mCallback;
    private Button mSpinnerButtonTime, mSpinnerButtonDate, mSpinnerButtonRepeat;
    private List<String> entries = new ArrayList<>();
    private Calendar mNotificationTime;
    private SharedPreferences mSharedPreferences;
    private int mHours;
    private int mMinutes;
    private String mRepeating;
    MenuItem mDeleteCheckedTasks;
    Button nCancelNotification, mMenuButton;
    private AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        mContext = this;

        loadPreferences();
        initTask();
        initView();
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
        mSharedPreferences = this.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        mHours = mSharedPreferences.getInt(Constants.MORNING_TIME_HOURS, 7);
        mMinutes = mSharedPreferences.getInt(Constants.MORNING_TIME_MINUTES, 0);
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.lt_toolbar);
        mToolbar.setTitle("");
        mToolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nCancelNotification = (Button) findViewById(R.id.cancelNotifButton);
        mMenuButton = (Button) findViewById(R.id.menuButton);

        sRecyclerView = (RecyclerView) findViewById(R.id.list_activity_recycler_view);

        mListNoteRecyclerAdapter = new ListNoteRecyclerAdapter(mNote, ListNoteActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        sRecyclerView.setLayoutManager(layoutManager);

        sRecyclerView.setHasFixedSize(true);
        sRecyclerView.setAdapter(mListNoteRecyclerAdapter);


        mCallback = new ListNoteItemTouchHelperCallback(mListNoteRecyclerAdapter);
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(sRecyclerView);


        setItemMovement(false);

        mToolbar.setNavigationIcon(R.drawable.ic_back);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        if (mNote.getColor() != 0){
            mToolbar.setBackgroundColor(mNote.getColor());
            setWhiteNavIconColor();
        } else {
            setBlackNavIconColor();
        }

        if(mNote.isRemind()){
            nCancelNotification.setVisibility(View.VISIBLE);
        } else {
            nCancelNotification.setVisibility(View.GONE);
        }

    }

    private void initTask() {
        mNote = (ListNote) getIntent().getSerializableExtra(Constants.LIST_TASK);
        if(mNote == null) {
            mNote = new ListNote();
            mNote.setId(-1);
            mNote.setPosition(getIntent().getIntExtra(Constants.POSITION, 0));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        mCurrentKind = getIntent().getStringExtra(Constants.KIND);
        if(mCurrentKind ==null)
            mCurrentKind = Constants.UNDEFINED;
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
        nCancelNotification.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bell_outline_black_36dp));
        mMenuButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_dots_vertical_black_36dp));
        mToolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void setWhiteNavIconColor(){
        int color = ContextCompat.getColor(this, android.R.color.white);
        nCancelNotification.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bell_outline_white_36dp));
        mMenuButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_dots_vertical_white_36dp));
        nCancelNotification.setFocusable(false);
        mToolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void createDialog() {

        mRepeating = "";

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);

        View mViewe = getLayoutInflater().inflate(R.layout.dialog_spiner, null);

        mSpinnerButtonTime = (Button) mViewe.findViewById(R.id.spinnerButtonTime);
        mSpinnerButtonTime.setText(R.string.morning);
        mSpinnerButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.inflate(R.menu.popup_time);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(ListNoteActivity.this);
            }
        });

        mSpinnerButtonDate = (Button) mViewe.findViewById(R.id.spinnerButtonDate);
        mSpinnerButtonDate.setText(R.string.tomorrow);
        mSpinnerButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.inflate(R.menu.popup_date);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(ListNoteActivity.this);
            }
        });


        mSpinnerButtonRepeat = (Button) mViewe.findViewById(R.id.spinnerButtonRepeat);
        mSpinnerButtonRepeat.setText(R.string.never);
        mSpinnerButtonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.inflate(R.menu.popup_repeat);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(ListNoteActivity.this);
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
                nCancelNotification.setVisibility(View.VISIBLE);
            }
        });

        mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        mBuilder.setView(mViewe);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    public void cancelNotification(View v){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.question_delete_notification);

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelNotification();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(mNote.getId());
                nCancelNotification.setVisibility(View.GONE);
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

        if(mSharedPreferences.getString(Constants.CURRENT_KIND, Constants.UNDEFINED).equals(Constants.DELETED)){
            MenuItem notify = popupMenu.getMenu().findItem(R.id.notify);
            notify.setVisible(false);
        }
        mDeleteCheckedTasks = popupMenu.getMenu().findItem(R.id.delete_checked_tasks);
        changeMenuItemVisibility(mNote.getCheckedItems().size());

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
                        break;
                    case R.id.delete_checked_tasks:
                        mListNoteRecyclerAdapter.deleteCheckedTasks();
                        changeMenuItemVisibility(0);
                        break;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void cancelNotification(){

        Intent intent1 = new Intent(getApplicationContext(), NotifyTaskReceiver.class);
        intent1.setAction(Constants.ACTION_NOTIFICATION);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), mNote.getId(), intent1, 0);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager1.cancel(sender);

        mNote.setRemind(false);
    }

    private void saveTask(boolean check) {
        mNote = mListNoteRecyclerAdapter.getListNote();

        DBHelper dbHelper = new DBHelper(this);
        if(mNote.getId() == -1){
            mNote.setId(dbHelper.addNote(mNote, mCurrentKind));
        }
        else {
            if(check)
                if(!dbHelper.isRemind(mNote))
                    mNote.setRemind(false);
            dbHelper.updateNote(mNote, mCurrentKind);
        }
    }

    public void setItemMovement(boolean b){
        ((ListNoteItemTouchHelperCallback) mCallback).setCanMovement(b);
    }

    @Override
    protected void onPause() {
        if(!(mNote.getHeadLine().length() == 0 &&
                mNote.getCheckedItems().size() == 0 &&
                mNote.getUncheckedItems().size() == 1 &&
                mNote.getUncheckedItems().get(0).length() == 0))
            saveTask(true);
        super.onPause();
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

                mNotificationTime.set(year,month,day);

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
                mHours = mSharedPreferences.getInt(Constants.MORNING_TIME_HOURS, 14);
                mMinutes = mSharedPreferences.getInt(Constants.MORNING_TIME_MINUTES, 15);
                mNotificationTime.set(Calendar.HOUR_OF_DAY, mHours);
                mNotificationTime.set(Calendar.MINUTE, mMinutes);
                break;
            case R.id.item_afternoon:
                mSpinnerButtonTime.setText(getResources().getString(R.string.afternoon));
                mHours = mSharedPreferences.getInt(Constants.AFTERNOON_TIME_HOURS, 14);
                mMinutes = mSharedPreferences.getInt(Constants.AFTERNOON_TIME_MINUTES, 15);
                mNotificationTime.set(Calendar.HOUR_OF_DAY, mHours);
                mNotificationTime.set(Calendar.MINUTE, mMinutes);
                break;
            case R.id.item_evening:
                mSpinnerButtonTime.setText(getResources().getString(R.string.evening));
                mHours = mSharedPreferences.getInt(Constants.EVENING_TIME_HOURS, 20);
                mMinutes = mSharedPreferences.getInt(Constants.EVENING_TIME_MINUTES, 15);
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
            case R.id.item_newer:
                mRepeating = "";
                break;
            case R.id.item_chose_date:
                showDatePickerDialog();
                break;

            case R.id.item_every_day:
                mRepeating = Constants.EVERY_DAY;
                mSpinnerButtonRepeat.setText(R.string.every_day);
                break;
            case R.id.item_every_week:
                mRepeating = Constants.EVERY_WEEK;
                mSpinnerButtonRepeat.setText(R.string.every_week);
                break;
            case R.id.item_every_month:
                mRepeating = Constants.EVERY_MONTH;
                mSpinnerButtonRepeat.setText(R.string.every_month);
                break;
        }
        return false;
    }

    public void changeMenuItemVisibility(int size) {
        if(mDeleteCheckedTasks !=null) {
            if (size == 0)
                mDeleteCheckedTasks.setVisible(false);
            else
                mDeleteCheckedTasks.setVisible(true);
        }
    }
}
