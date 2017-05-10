package com.example.dmitryvedmed.taskbook.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.R;

public class SettingsActivity extends AppCompatActivity {

    TextView taskFontLabel, cardFontLabel, taskFontValue, cardFontValue;
    SeekBar taskFontSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskFontLabel = (TextView) findViewById(R.id.taskFont);
        cardFontLabel = (TextView) findViewById(R.id.cardFont);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("                                        ertfgyhujikolp;jhgwaerdtfghjkl;");
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }


    public void onClick(View v){
        switch (v.getId()){
            case R.id.taskFont:

        }
    }
}
