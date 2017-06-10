package com.example.dmitryvedmed.taskbook.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.untils.Constants;


public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {


    SharedPreferences sharedPreferences;
    TextView taskFontLabel;
    TextView cardFontLabel;
    TextView taskFontValue;
    TextView cardFontValue;
    TextView deletionPeriodLabel;
    TextView swipeAction;
    TextView morningTime;
    TextView afternoonTime;
    TextView eveningTime;
    SeekBar taskFontSeekBar;
    SeekBar cardFontSeekBar;
    SharedPreferences.Editor editor;
    RelativeLayout setDeletionPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  toolbar.setTitle(R.string.settings);
        getSupportActionBar().setTitle(R.string.settings);

        int color = ContextCompat.getColor(this, R.color.common_google_signin_btn_text_dark);
        toolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);


        cardFontLabel = (TextView) findViewById(R.id.labelCardFont);
        taskFontLabel = (TextView) findViewById(R.id.labelTaskFont);

        cardFontValue = (TextView) findViewById(R.id.textCardFontValue);
        taskFontValue = (TextView) findViewById(R.id.textTaskFontValue);

       setTime();


        taskFontSeekBar = (SeekBar) findViewById(R.id.seekBarTaskFont);
        taskFontSeekBar.setOnSeekBarChangeListener(this);
        cardFontSeekBar = (SeekBar) findViewById(R.id.seekBarCardFont);
        cardFontSeekBar.setOnSeekBarChangeListener(this);

        deletionPeriodLabel = (TextView) findViewById(R.id.deletionPeriodValue);
        Long deletionPeriod = sharedPreferences.getLong(Constants.DELETION_PERIOD,Constants.PERIOD_WEEK);

        if (deletionPeriod.equals(Constants.PERIOD_AT_ONCE)) {
            deletionPeriodLabel.setText(R.string.at_once);

        } else if (deletionPeriod.equals(Constants.PERIOD_ONE_DAY)) {
            deletionPeriodLabel.setText(R.string.one_day);

        } else if (deletionPeriod.equals(Constants.PERIOD_TREE_DAY)) {
            deletionPeriodLabel.setText(R.string.tree_day);

        } else if (deletionPeriod.equals(Constants.PERIOD_WEEK)) {
            deletionPeriodLabel.setText(R.string.seven_day);

        } else if (deletionPeriod.equals(Constants.PERIOD_MONTH)) {
            deletionPeriodLabel.setText(R.string.month);

        }


        swipeAction = (TextView) findViewById(R.id.swipeAction);
        String remember = sharedPreferences.getString(Constants.SWIPE_REMEMBER,"");
        switch (remember){
            case Constants.ARCHIVE:
                swipeAction.setText(R.string.act_archive);
                break;
            case Constants.DELETED:
                swipeAction.setText(R.string.act_delete);
                break;
            case "":
                swipeAction.setText(R.string.act_ask);
                break;
        }

        setDeletionPeriod = (RelativeLayout) findViewById(R.id.setDeletionPeriod);

        Log.d("TAG", "FONT SIZE " + sharedPreferences.getInt(Constants.TASK_FONT_SIZE, 16));

        taskFontSeekBar.setProgress(sharedPreferences.getInt(Constants.TASK_FONT_SIZE, 16) - 12);
        cardFontSeekBar.setProgress(sharedPreferences.getInt(Constants.CARD_FONT_SIZE, 17) - 12);
    }

    private void setTime() {
        morningTime = (TextView) findViewById(R.id.morning_text);
        afternoonTime = (TextView) findViewById(R.id.afternoon_text);
        eveningTime = (TextView) findViewById(R.id.evening_text);

        String mHours = String.valueOf(sharedPreferences.getInt(Constants.MORNING_TIME_HOURS, 8));
        String aHours = String.valueOf(sharedPreferences.getInt(Constants.AFTERNOON_TIME_HOURS, 14));
        String eHours = String.valueOf(sharedPreferences.getInt(Constants.EVENING_TIME_HOURS, 20));

        String mMinutes = String.valueOf(sharedPreferences.getInt(Constants.MORNING_TIME_MINUTES, 2));
        if(mMinutes.length()==1)
            mMinutes = "0" + mMinutes;
        String aMinutes = String.valueOf(sharedPreferences.getInt(Constants.AFTERNOON_TIME_MINUTES, 4));
        if(aMinutes.length()==1)
            aMinutes = "0" + aMinutes;
        String eMinutes = String.valueOf(sharedPreferences.getInt(Constants.EVENING_TIME_MINUTES, 6));
        if(eMinutes.length()==1)
            eMinutes = "0" + eMinutes;

        morningTime.setText(mHours + ":" + mMinutes);
        afternoonTime.setText(aHours + ":" + aMinutes);
        eveningTime.setText(eHours + ":" + eMinutes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("                                        ertfgyhujikolp;jhgwaerdtfghjkl;");
        //getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setDeletionPeriod:
                editor = sharedPreferences.edit();
                PopupMenu popupMenu = new PopupMenu(this,setDeletionPeriod);
                popupMenu.inflate(R.menu.deletion_period);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.at_once:
                                deletionPeriodLabel.setText(R.string.at_once);
                                editor.putLong(Constants.DELETION_PERIOD, Constants.PERIOD_AT_ONCE);
                                break;
                            case R.id.one_day:
                                deletionPeriodLabel.setText(R.string.one_day);
                                editor.putLong(Constants.DELETION_PERIOD, Constants.PERIOD_ONE_DAY);
                                break;
                            case R.id.tree_days:
                                deletionPeriodLabel.setText(R.string.tree_day);
                                editor.putLong(Constants.DELETION_PERIOD, Constants.PERIOD_TREE_DAY);
                                break;
                            case R.id.seven_days:
                                deletionPeriodLabel.setText(R.string.seven_day);
                                editor.putLong(Constants.DELETION_PERIOD, Constants.PERIOD_WEEK);
                                break;
                            case R.id.month:
                                deletionPeriodLabel.setText(R.string.month);
                                editor.putLong(Constants.DELETION_PERIOD, Constants.PERIOD_MONTH);
                                break;
                        }
                        editor.commit();
                        return false;
                    }
                });
                popupMenu.show();
                break;
            case R.id.setSectionPosition:
                Intent intent = new Intent(this, SectionPositionActivity.class);
                this.startActivity(intent);
                return;
            case R.id.setSwipeAction:

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, new String[]{getResources().getString(R.string.act_delete),
                        getResources().getString(R.string.act_archive), getResources().getString(R.string.act_ask)});
                mBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        switch (i){
                            case 0:
                                editor.putString(Constants.SWIPE_REMEMBER, Constants.DELETED);
                                editor.commit();
                                swipeAction.setText(R.string.act_delete);
                                break;
                            case 1:
                                editor.putString(Constants.SWIPE_REMEMBER, Constants.ARCHIVE);
                                editor.commit();
                                swipeAction.setText(R.string.act_archive);
                                break;
                            case 2:
                                editor.putString(Constants.SWIPE_REMEMBER, "");
                                editor.commit();
                                swipeAction.setText(R.string.act_ask);
                                break;
                        }
                    }
                });
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                break;
            case R.id.setAfternoonTime:
                final int hour = sharedPreferences.getInt(Constants.AFTERNOON_TIME_HOURS,14);
                final int minute = sharedPreferences.getInt(Constants.AFTERNOON_TIME_MINUTES,0);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                        editor.putInt(Constants.AFTERNOON_TIME_MINUTES, m);
                        editor.putInt(Constants.AFTERNOON_TIME_HOURS, h);
                        editor.commit();
                        setTime();
                    }
                }, hour, minute, true);
                timePickerDialog.show();
                break;
            case R.id.setMorningTime:
                final int hour2 = sharedPreferences.getInt(Constants.MORNING_TIME_HOURS,8);
                final int minute2 = sharedPreferences.getInt(Constants.MORNING_TIME_MINUTES,0);

                TimePickerDialog timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                        editor.putInt(Constants.AFTERNOON_TIME_MINUTES, m);
                        editor.putInt(Constants.AFTERNOON_TIME_HOURS, h);
                        editor.commit();
                        setTime();
                    }
                }, hour2, minute2, true);
                timePickerDialog2.show();
                break;
            case R.id.setEveningTime:
                final int hour3 = sharedPreferences.getInt(Constants.EVENING_TIME_HOURS,20);
                final int minute3 = sharedPreferences.getInt(Constants.EVENING_TIME_MINUTES,0);

                TimePickerDialog timePickerDialog3 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                        editor.putInt(Constants.AFTERNOON_TIME_MINUTES, m);
                        editor.putInt(Constants.AFTERNOON_TIME_HOURS, h);
                        editor.commit();
                        setTime();
                    }
                }, hour3, minute3, true);
                timePickerDialog3.show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Log.d("TAG", "progress Changed " + i + "b " + b);
        editor = sharedPreferences.edit();
        switch (seekBar.getId()) {
            case R.id.seekBarTaskFont:
                taskFontLabel.setTextSize(i + 12);
                taskFontValue.setText(String.valueOf(i + 12));
                if (b) {
                    editor.putInt(Constants.TASK_FONT_SIZE, i + 12);
                }
                break;
            case R.id.seekBarCardFont:
                cardFontLabel.setTextSize(i + 12);
                if (b) {
                    editor.putInt(Constants.CARD_FONT_SIZE, i + 12);
                }
                cardFontValue.setText(String.valueOf(i + 12));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        editor.commit();
    }


}
