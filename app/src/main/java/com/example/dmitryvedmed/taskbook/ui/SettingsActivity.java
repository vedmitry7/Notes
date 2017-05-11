package com.example.dmitryvedmed.taskbook.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    SeekBar taskFontSeekBar;
    SeekBar cardFontSeekBar;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        sharedPreferences = getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);

        Log.d("TAG", "FONT SIZE " + sharedPreferences.getInt("taskFontSize", 16));

        taskFontSeekBar.setProgress(sharedPreferences.getInt("taskFontSize", 16) - 12);
        cardFontSeekBar.setProgress(sharedPreferences.getInt("cardFontSize", 14) - 12);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("                                        ertfgyhujikolp;jhgwaerdtfghjkl;");
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }


    public void onClick(View v) {
        switch (v.getId()) {
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
