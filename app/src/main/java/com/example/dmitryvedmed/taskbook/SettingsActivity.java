package com.example.dmitryvedmed.taskbook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreatePreferenceActivity();
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("                                        ertfgyhujikolp;jhgwaerdtfghjkl;");
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @SuppressWarnings("deprecation")
    private void onCreatePreferenceActivity() {
       // addPreferencesFromResource(R.xml.pref_general);
    }
}
