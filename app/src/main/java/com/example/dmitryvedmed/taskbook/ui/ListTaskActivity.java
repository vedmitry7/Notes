package com.example.dmitryvedmed.taskbook.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dmitryvedmed.taskbook.NotifyTaskReceiver;
import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.helper.ListTaskItemTouchHelperCallback;
import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.ListTask;
import com.example.dmitryvedmed.taskbook.untils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


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
    List<String> entries = new ArrayList<>();
    String time;
    boolean isSetTime;
    MyAdapter  myAdapter;
    int lastChose;

    String[] cities = new String[]{"Утром", "Днем", "Вечером", "Другое время"};
    String[] exactTimes = new String[]{"7:01", "14:00", "19:00", ""};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        context = this;
        initTask();
        initView();
        entries.add("now");
        entries.add("tomorrow");
        entries.add("Выбрать время");
        myAdapter = new MyAdapter(context);
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
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int color;
        switch (item.getItemId()){
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
          /*      Intent intent = new Intent("TASK_NOTIFICATION");
                saveTask();
                intent.putExtra("id", task.getId());
                sendBroadcast(intent);
                */
/*
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
                timePickerDialog.show();*/
                showDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_colors, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class MyAdapter extends ArrayAdapter<String> {

        Context context;



        public MyAdapter(Context context) {
            super(context, R.layout.adapter_item, ListTaskActivity.this.cities);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);

        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent)
        {  LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_item, parent, false);

            TextView time = (TextView) convertView.findViewById(R.id.timeText);
            time.setText(cities[position]);


            TextView exactTime = (TextView) convertView.findViewById(R.id.exactTimeText);
            Log.d("TAG", "exactTime TEXT do =  " + exactTime.getText());
            exactTime.setText(exactTimes[position]);
            Log.d("TAG", "exactTime TEXT posle =  " + exactTime.getText());

            return convertView;}
    }

    private void showDialog(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spiners, null);
        mBuilder.setTitle("Напоминание");

        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinnerDate);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.dates));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        final Spinner mSpinner2 = (Spinner) mView.findViewById(R.id.spinnerTime);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item,
                entries);
        // getResources().getStringArray(R.array.times));
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner2.setAdapter(myAdapter);
        mSpinner2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    Log.d("TAG", "DOWNNNNNN");
                /*        if(entries.size()==1){
                            entries.clear();
                            entries.add("now");
                            entries.add("tomorrow");
                            entries.add("Выбрать время");
                            adapter2.notifyDataSetChanged();
                        }*/
                }
                return false;
            }
        });


        AdapterView.OnItemSelectedListener itemSelectedListener2 = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                final String item = (String)parent.getItemAtPosition(position);
                Log.d("TAG", "ID " + id);
                Log.d("TAG",  item);
                if(item.equals("Другое время")){
                    final Calendar calendar = Calendar.getInstance();
                    final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    final int minute = calendar.get(Calendar.MINUTE);


                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int h, int m) {

                            calendar.set(Calendar.HOUR_OF_DAY, h);
                            calendar.set(Calendar.MINUTE, m);
                            calendar.set(Calendar.SECOND, 0);
                            Log.d("TAG", "TIME: " + h + ":" + m);

                            long firstTime = calendar.getTimeInMillis();

                            /*Intent intent = new Intent(getApplicationContext(), NotifyTaskReceiver.class);
                            intent.setAction("TASK_NOTIFICATION");
                            saveTask(true);
                            intent.putExtra("id", task.getId());
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),task.getId(), intent, 0);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, pendingIntent);

                            task.setRemind(true);
                            task.setReminderTime(firstTime);
                            saveTask(false);
*/
                            // cancelNotification();

                            //alarmManager1.cancel(pendingIntent);
                            if(entries.size()>3)
                                entries.remove(0);
                            // entries.clear();
                            entries.add(0, calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                            time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                            exactTimes[3] = time;
                            myAdapter.notifyDataSetChanged();
                            adapter2.notifyDataSetChanged();
                            isSetTime = true;
                        }
                    }, hour, minute, true);
                    timePickerDialog.setCancelable(false);
                    timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            mSpinner2.setSelection(0);
                            Log.d("TAG", "CANCEL TIME PICKER");

                        }
                    });
                    timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {

                            Log.d("TAG", "DISMISS TIME PICKER");
                            if(!isSetTime)
                            mSpinner2.setSelection(lastChose);


                            if(dialogInterface.equals(DialogInterface.BUTTON_NEGATIVE)) {
                                Log.d("TAG", "DISMISS TIME PICKER");
                            }
                            if(dialogInterface.equals(DialogInterface.BUTTON_POSITIVE)) {
                                Log.d("TAG", "SET TIME PICKER");
                            }
                            if(dialogInterface.equals(DialogInterface.BUTTON_NEUTRAL)) {
                                Log.d("TAG", "NEUTRAL TIME PICKER");
                            }
                        }
                    });
                    timePickerDialog.setCanceledOnTouchOutside(false);
                    timePickerDialog.show();
                }

                else {
                    isSetTime = false;
                    lastChose = position;
                    exactTimes[3]="";
                    myAdapter.notifyDataSetChanged();
                }

            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("TAG", "NOTHING SELECTED");
         /*       if(time!=null&&!time.equals("")) {
                    entries.clear();
                    entries.add(time);
                    adapter2.notifyDataSetChanged();
                }*/

            }
        };
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

            Calendar calendar = Calendar.getInstance();

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String item = (String)adapterView.getItemAtPosition(position);
                if(item.equals("Выбрать дату")) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        }
                    }, calendar.get(Calendar.YEAR), 4, 25);
                    datePickerDialog.show();
                } else {
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

      //  mSpinner.setOnItemSelectedListener(itemSelectedListener);
        mSpinner2.setOnItemSelectedListener(itemSelectedListener2);

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mSpinner.getSelectedItem().toString().equals("Сегодня")){
                    Toast.makeText(context,"Ctujlyz", Toast.LENGTH_LONG).show();
                }
            }
        });
        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mSpinner.getSelectedItem().toString().equals("Сегодня")){
                    Toast.makeText(context,"Ctujlyz", Toast.LENGTH_LONG).show();
                }
            }
        });
        mBuilder.setView(mView);
        mBuilder.setCancelable(false);
        AlertDialog dialog = mBuilder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
        if(!(task.getHeadLine().length() == 0 &&
                task.getCheckedTasks().size() == 0 &&
                task.getUncheckedTasks().size() == 1 &&
                task.getUncheckedTasks().get(0).length() == 0))
            saveTask(true);
        super.onPause();
    }
}
