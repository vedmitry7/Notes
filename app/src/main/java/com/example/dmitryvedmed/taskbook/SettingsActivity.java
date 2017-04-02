package com.example.dmitryvedmed.taskbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("                                        ertfgyhujikolp;jhgwaerdtfghjkl;");
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }
}
