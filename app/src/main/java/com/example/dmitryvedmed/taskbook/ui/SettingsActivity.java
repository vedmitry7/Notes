package com.example.dmitryvedmed.taskbook.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.untils.Constants;


public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {


    SharedPreferences sharedPreferences;
    TextView taskFontLabel;
    TextView cardFontLabel;
    TextView taskFontValue;
    TextView cardFontValue;
    TextView deletionPeriodLabel;
    SeekBar taskFontSeekBar;
    SeekBar cardFontSeekBar;
    SharedPreferences.Editor editor;
    RelativeLayout setDeletionPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Settings");

        cardFontLabel = (TextView) findViewById(R.id.labelCardFont);
        taskFontLabel = (TextView) findViewById(R.id.labelTaskFont);

        cardFontValue = (TextView) findViewById(R.id.textCardFontValue);
        taskFontValue = (TextView) findViewById(R.id.textTaskFontValue);

        taskFontSeekBar = (SeekBar) findViewById(R.id.seekBarTaskFont);
        taskFontSeekBar.setOnSeekBarChangeListener(this);
        cardFontSeekBar = (SeekBar) findViewById(R.id.seekBarCardFont);
        cardFontSeekBar.setOnSeekBarChangeListener(this);

        deletionPeriodLabel = (TextView) findViewById(R.id.deletionPeriodValue);
        Long deletionPeriod = sharedPreferences.getLong("deletionPeriod",Constants.PERIOD_WEEK);

        if (deletionPeriod.equals(Constants.PERIOD_AT_ONCE)) {
            deletionPeriodLabel.setText("Сразу");

        } else if (deletionPeriod.equals(Constants.PERIOD_ONE_DAY)) {
            deletionPeriodLabel.setText("1 день");

        } else if (deletionPeriod.equals(Constants.PERIOD_TREE_DAY)) {
            deletionPeriodLabel.setText("3 дня");

        } else if (deletionPeriod.equals(Constants.PERIOD_WEEK)) {
            deletionPeriodLabel.setText("7 дней");

        } else if (deletionPeriod.equals(Constants.PERIOD_MONTH)) {
            deletionPeriodLabel.setText("Месяц");

        }

        setDeletionPeriod = (RelativeLayout) findViewById(R.id.setDeletionPeriod);

        Log.d("TAG", "FONT SIZE " + sharedPreferences.getInt("taskFontSize", 16));

        taskFontSeekBar.setProgress(sharedPreferences.getInt("taskFontSize", 16) - 12);
        cardFontSeekBar.setProgress(sharedPreferences.getInt("cardFontSize", 17) - 12);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("                                        ertfgyhujikolp;jhgwaerdtfghjkl;");
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
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
                                deletionPeriodLabel.setText("Сразу");
                                editor.putLong("deletionPeriod", Constants.PERIOD_AT_ONCE);
                                break;
                            case R.id.one_day:
                                deletionPeriodLabel.setText("1 день");
                                editor.putLong("deletionPeriod", Constants.PERIOD_ONE_DAY);
                                break;
                            case R.id.tree_days:
                                deletionPeriodLabel.setText("3 дня");
                                editor.putLong("deletionPeriod", Constants.PERIOD_TREE_DAY);
                                break;
                            case R.id.seven_days:
                                deletionPeriodLabel.setText("7 дней");
                                editor.putLong("deletionPeriod", Constants.PERIOD_WEEK);
                                break;
                            case R.id.month:
                                deletionPeriodLabel.setText("Месяц");
                                editor.putLong("deletionPeriod", Constants.PERIOD_MONTH);
                                break;
                        }
                        editor.commit();
                        return false;
                    }
                });
                popupMenu.show();
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
                    editor.putInt("taskFontSize", i + 12);
                }
                break;
            case R.id.seekBarCardFont:
                cardFontLabel.setTextSize(i + 12);
                if (b) {
                    editor.putInt("cardFontSize", i + 12);
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
