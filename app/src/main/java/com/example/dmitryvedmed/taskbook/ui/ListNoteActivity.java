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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TimePicker;

import com.example.dmitryvedmed.taskbook.NotifyTaskReceiver;
import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.helper.ListNoteItemTouchHelperCallback;
import com.example.dmitryvedmed.taskbook.logic.DBHelper;
import com.example.dmitryvedmed.taskbook.logic.ListNote;
import com.example.dmitryvedmed.taskbook.untils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class ListNoteActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ListNote task;
    public static RecyclerView recyclerView;
    private ListNoteRecyclerAdapter listNoteRecyclerAdapter;
    private Context context;
    private String currentKind;
    private Toolbar toolbar;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback callback;
    private Button spinnerButtonTime, spinnerButtonDate, spinnerButtonRepeat;
    private List<String> entries = new ArrayList<>();
    private Calendar notificationTime;
    private SharedPreferences sharedPreferences;
    int hours;
    int minutes;
    private String repeating;
    MenuItem deleteCheckedTasks;
    Button cancelNotification, menuButton;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        context = this;

        loadPreferences();
        initTask();
        initView();
        initDate();
        repeating = "";
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

        hours = sharedPreferences.getInt(Constants.MORNING_TIME_HOURS, 7);
        minutes = sharedPreferences.getInt(Constants.MORNING_TIME_MINUTES, 0);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.lt_toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        cancelNotification = (Button) findViewById(R.id.cancelNotifButton);
        menuButton = (Button) findViewById(R.id.menuButton);

        recyclerView = (RecyclerView) findViewById(R.id.list_activity_recycler_view);

        listNoteRecyclerAdapter = new ListNoteRecyclerAdapter(task, ListNoteActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listNoteRecyclerAdapter);


        callback = new ListNoteItemTouchHelperCallback(listNoteRecyclerAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        setItemMovement(false);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cek", "home selected");
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        if (task.getColor() != 0){
            toolbar.setBackgroundColor(task.getColor());
            setWhiteNavIconColor();
        } else {
            setBlackNavIconColor();
        }

        if(task.isRemind()){
            Log.d("TAG", "is REMIND FALSE");
            cancelNotification.setVisibility(View.VISIBLE);
        } else {
            Log.d("TAG", "is REMIND TRUE");
            cancelNotification.setVisibility(View.GONE);
        }

    }

    private void initTask() {
        task = (ListNote) getIntent().getSerializableExtra(Constants.LIST_TASK);
        if(task == null) {
            System.out.println("LIST TASK = NULL!");
            task = new ListNote();
            task.setId(-1);
            task.setPosition(getIntent().getIntExtra(Constants.POSITION, 0));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        else {
            System.out.println("LIST TASK != NULL, id = " + task.getHeadLine());
            Log.d("TAG", "COLOR - " + task.getColor());
        }
        currentKind = getIntent().getStringExtra(Constants.KIND);
        if(currentKind==null)
            currentKind = Constants.UNDEFINED;
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
                toolbar.setBackgroundColor(color);
                setWhiteNavIconColor();
                task.setColor(color);
                Log.d("TAG", "COLOR " + color);
                break;
            case R.id.red:
                color = ContextCompat.getColor(this, R.color.taskColorRed);
                toolbar.setBackgroundColor(color);
                setWhiteNavIconColor();
                task.setColor(color);
                Log.d("TAG", "COLOR " + color);
                break;
            case R.id.blue:
                color = ContextCompat.getColor(this, R.color.taskColorBlue);
                toolbar.setBackgroundColor(color);
                setWhiteNavIconColor();
                task.setColor(color);
                Log.d("TAG", "COLOR " + color);
                break;
            case R.id.yellow:
                color = ContextCompat.getColor(this, R.color.taskColorYellow);
                toolbar.setBackgroundColor(color);
                setWhiteNavIconColor();
                task.setColor(color);
                Log.d("TAG", "COLOR " + color);
                break;
            case R.id.white:
                toolbar.setBackgroundColor(Color.WHITE);
                setBlackNavIconColor();
                task.setColor(0);
                break;
        }
        dialog.dismiss();
    }

    private void setBlackNavIconColor(){
        Log.d("TAG", "COLOR  BLAAAAAAAACK" );
        int color = ContextCompat.getColor(this, android.R.color.black);
        cancelNotification.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bell_outline_black_36dp));
        menuButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_dots_vertical_black_36dp));
        toolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void setWhiteNavIconColor(){
        Log.d("TAG", "COLOR  WHIIIIIIIIIITE" );
        int color = ContextCompat.getColor(this, android.R.color.white);
        cancelNotification.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bell_outline_white_36dp));
        menuButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_dots_vertical_white_36dp));
        cancelNotification.setFocusable(false);
        toolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void createDialog() {

        repeating = "";

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

        View mViewe = getLayoutInflater().inflate(R.layout.dialog_spiner, null);

        spinnerButtonTime = (Button) mViewe.findViewById(R.id.spinnerButtonTime);
        spinnerButtonTime.setText(R.string.morning);
        spinnerButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.popup_time);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(ListNoteActivity.this);
            }
        });

        spinnerButtonDate = (Button) mViewe.findViewById(R.id.spinnerButtonDate);
        spinnerButtonDate.setText(R.string.tomorrow);
        spinnerButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.popup_date);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(ListNoteActivity.this);
            }
        });


        spinnerButtonRepeat = (Button) mViewe.findViewById(R.id.spinnerButtonRepeat);
        spinnerButtonRepeat.setText(R.string.never);
        spinnerButtonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.popup_repeat);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(ListNoteActivity.this);
            }
        });

        mBuilder.setTitle(R.string.notification);
        mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("TAG", "----------------------OK");
                Log.d("TAG", String.valueOf(notificationTime.get(Calendar.YEAR)));
                Log.d("TAG", String.valueOf(notificationTime.get(Calendar.MONTH)));
                Log.d("TAG", String.valueOf(notificationTime.get(Calendar.DAY_OF_MONTH)));
                Log.d("TAG", String.valueOf(notificationTime.get(Calendar.HOUR_OF_DAY)));
                Log.d("TAG", String.valueOf(notificationTime.get(Calendar.MINUTE)));
                Log.d("TAG", "----------------------");

                final Intent intent = new Intent(getApplicationContext(), NotifyTaskReceiver.class);
                intent.setAction(Constants.ACTION_NOTIFICATION);
                saveTask(true);
                intent.putExtra(Constants.ID, task.getId());

                final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if(repeating.equals("")) {

                    task.setRepeatingPeriod(0);
                    task.setRepeating(false);

                    if(notificationTime.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                        mBuilder.setMessage(R.string.reminder_past_time);
                        mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),task.getId(), intent, 0);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                                Log.d("TAG", " SET IN PAAAAAAAAAAST!!!!!!!!!!!!! ALARM ALARM!");
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

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),task.getId(), intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), pendingIntent);
                }
                if(repeating.equals(Constants.EVERY_DAY)) {
                    Log.d("TAG", "REPEATING every day " + repeating);

                    Log.d("TAG", "difference 1 " + (notificationTime.getTimeInMillis() - System.currentTimeMillis()));

                    intent.putExtra(Constants.REPEATING, true);
                    intent.putExtra(Constants.PERIOD, Constants.PERIOD_ONE_DAY);
                    task.setRepeatingPeriod(Constants.PERIOD_ONE_DAY);
                    task.setRepeating(true);

                    while (notificationTime.getTimeInMillis() < System.currentTimeMillis()) {

                        notificationTime.add(Calendar.DAY_OF_MONTH, 1);
                    }

                    Log.d("TAG", "difference 2 " + (notificationTime.getTimeInMillis() - System.currentTimeMillis()));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),task.getId(), intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), Constants.PERIOD_ONE_DAY, pendingIntent);
                }
                if(repeating.equals(Constants.EVERY_WEEK)) {
                    Log.d("TAG", "REPEATING every WEEK " + repeating);
                    intent.putExtra(Constants.REPEATING, true);
                    intent.putExtra(Constants.PERIOD, Constants.PERIOD_WEEK);
                    task.setRepeatingPeriod(Constants.PERIOD_WEEK);
                    task.setRepeating(true);

                    while (notificationTime.getTimeInMillis()<System.currentTimeMillis()) {
                        notificationTime.add(Calendar.WEEK_OF_MONTH, 1);
                    }

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),task.getId(), intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), Constants.PERIOD_WEEK, pendingIntent);
                }

                if(repeating.equals(Constants.EVERY_MONTH)) {
                    Log.d("TAG", "REPEATING every MOUTH " + repeating);
                    intent.putExtra(Constants.REPEATING, true);
                    intent.putExtra(Constants.PERIOD, Constants.PERIOD_MONTH);
                    task.setRepeatingPeriod(Constants.PERIOD_MONTH);
                    task.setRepeating(true);

                    while(notificationTime.getTimeInMillis()<System.currentTimeMillis()) {
                        notificationTime.add(Calendar.MONTH, 1);
                    }

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),task.getId(), intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), Constants.PERIOD_MONTH, pendingIntent);
                }

                task.setRemind(true);
                task.setReminderTime(notificationTime.getTimeInMillis());
                saveTask(false);
                cancelNotification.setVisibility(View.VISIBLE);
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

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_colors, menu);
        deleteCheckedTasks = menu.findItem(R.id.delete_checked_tasks);

        //changeMenuItemVisibility(task.getCheckedTasks().size());

        return super.onCreateOptionsMenu(menu);
    }*/

    public void cancelNotification(View v){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // alert.setTitle("Очистить корзину?");
        alert.setMessage(R.string.question_delete_notification);

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelNotification();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(task.getId());
                cancelNotification.setVisibility(View.GONE);
            }
        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        final AlertDialog.Builder inform = new AlertDialog.Builder(this);
        inform.setTitle(getResources().getString(R.string.notification));

        if(task.getReminderTime() < System.currentTimeMillis()){
            long period = task.getRepeatingPeriod();
            while (task.getReminderTime() < System.currentTimeMillis()){
                task.setReminderTime(task.getReminderTime() + period);
            }
            saveTask(false);
        }

        String dateString = DateFormat.format("dd/MM/yyyy", new Date(task.getReminderTime())).toString();
        String timeString = DateFormat.format("H:mm", new Date(task.getReminderTime())).toString();
        String repeating;

        if(task.getRepeatingPeriod() == Constants.PERIOD_ONE_DAY){
            repeating = getResources().getString(R.string.every_day);
        } else if(task.getRepeatingPeriod() == Constants.PERIOD_WEEK){
            repeating = getResources().getString(R.string.every_week);
        } else if(task.getRepeatingPeriod() == Constants.PERIOD_MONTH){
            repeating = getResources().getString(R.string.every_month);
        } else {
            repeating = "None";
        }

        inform.setMessage(getResources().getString(R.string.date) + " : " + dateString + "\r\n" +
                "Время : " + timeString+ "\r\n" + "Повтор : " + repeating);

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
        popupMenu.inflate(R.menu.menu_colors);
        deleteCheckedTasks = popupMenu.getMenu().findItem(R.id.delete_checked_tasks);
        changeMenuItemVisibility(task.getCheckedTasks().size());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.set_color2:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        //builder.setTitle("Выберете цвет");
                        builder.setView(R.layout.dialog_choose_color);
                        dialog = builder.create();
                        dialog.show();
                        break;
                    case R.id.notify:
                        createDialog();
                        break;
                    case R.id.delete_checked_tasks:
                        listNoteRecyclerAdapter.deleteCheckedTasks();
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
        //intent1.putExtra("id", task.getId());
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), task.getId(), intent1, 0);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager1.cancel(sender);

        task.setRemind(false);
    }

    private void saveTask(boolean check) {
        task = listNoteRecyclerAdapter.getListNote();

        // task.setHeadLine(headList.getText().toString());

        DBHelper dbHelper = new DBHelper(this);
        if(task.getId() == -1){
            task.setId(dbHelper.addTask(task, currentKind ));
        }
        else {
            if(check)
                if(!dbHelper.isRemind(task))
                    task.setRemind(false);
            dbHelper.updateTask(task, currentKind);
        }
    }

    public void setItemMovement(boolean b){
        Log.d("TAG", "MOVEMENT  " + b);
        ((ListNoteItemTouchHelperCallback)callback).setCanMovement(b);
    }

    @Override
    protected void onPause() {
        if(!(task.getHeadLine().length() == 0 &&
                task.getCheckedTasks().size() == 0 &&
                task.getUncheckedTasks().size() == 1 &&
                task.getUncheckedTasks().get(0).length() == 0))
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
                hours = sharedPreferences.getInt(Constants.MORNING_TIME_HOURS, 14);
                minutes = sharedPreferences.getInt(Constants.MORNING_TIME_MINUTES, 15);
                notificationTime.set(Calendar.HOUR_OF_DAY, hours);
                notificationTime.set(Calendar.MINUTE, minutes);
                break;
            case R.id.item_afternoon:
                spinnerButtonTime.setText(getResources().getString(R.string.afternoon));
                hours = sharedPreferences.getInt(Constants.AFTERNOON_TIME_HOURS, 14);
                minutes = sharedPreferences.getInt(Constants.AFTERNOON_TIME_MINUTES, 15);
                notificationTime.set(Calendar.HOUR_OF_DAY, hours);
                notificationTime.set(Calendar.MINUTE, minutes);
                break;
            case R.id.item_evening:
                spinnerButtonTime.setText(getResources().getString(R.string.evening));
                hours = sharedPreferences.getInt(Constants.EVENING_TIME_HOURS, 20);
                minutes = sharedPreferences.getInt(Constants.EVENING_TIME_MINUTES, 15);
                notificationTime.set(Calendar.HOUR_OF_DAY, hours);
                notificationTime.set(Calendar.MINUTE, minutes);
                break;
            case R.id.item_chose_time:
                showTimePickerDialog();
                break;
            case R.id.item_today:
                notificationTime = Calendar.getInstance();
                notificationTime.set(Calendar.HOUR_OF_DAY, hours);
                notificationTime.set(Calendar.MINUTE, minutes);
                Log.d("TAG", "DAY - " + String.valueOf(notificationTime.get(Calendar.DAY_OF_MONTH)));
                spinnerButtonDate.setText(getResources().getString(R.string.today));

                break;

            case R.id.item_tomorrow:
                notificationTime = Calendar.getInstance();
                notificationTime.set(Calendar.HOUR_OF_DAY, hours);
                notificationTime.set(Calendar.MINUTE, minutes);
                notificationTime.add(Calendar.DAY_OF_MONTH, 1);
                spinnerButtonDate.setText(getResources().getString(R.string.tomorrow));

                break;
            case R.id.item_newer:
                repeating = "";
                break;
            case R.id.item_chose_date:
                showDatePickerDialog();
                break;

            case R.id.item_every_day:
                repeating = Constants.EVERY_DAY;
                spinnerButtonRepeat.setText(R.string.every_day);
                break;
            case R.id.item_every_week:
                repeating = Constants.EVERY_WEEK;
                spinnerButtonRepeat.setText(R.string.every_week);
                break;
            case R.id.item_every_month:
                repeating = Constants.EVERY_MONTH;
                spinnerButtonRepeat.setText(R.string.every_month);
                break;
        }
        return false;
    }

    public void changeMenuItemVisibility(int size) {
        if(deleteCheckedTasks!=null) {
            if (size == 0)
                deleteCheckedTasks.setVisible(false);
            else
                deleteCheckedTasks.setVisible(true);
        }
    }
}
