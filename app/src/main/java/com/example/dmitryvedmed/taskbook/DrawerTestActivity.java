package com.example.dmitryvedmed.taskbook;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.helper.SimpleItemTouchHelperCallback;

import java.util.List;

public class DrawerTestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    List<SuperTask> values;
    public static DBHelper5 dbHelper;
    public static RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    public static Main3Activity.Mode mode;
    boolean is_in_action_mode = false;
    TextView counterTextView;
    Toolbar toolbar;
    String currentKind = Constants.UNDEFINED;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_test);

        context = this;

        dbHelper = new DBHelper5(this);
        update();
        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_common);
        adapter = new MainRecyclerAdapter(values, DrawerTestActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBar toolbar = getActionBar();
    }


    void update(){
        Log.d("TAG", "      Activity --- update  ---");
        values = dbHelper.getTasks(currentKind);
        if(adapter!=null)
            adapter.dataChanged(values);
    }

    @Override
    protected void onResume() {
        Log.d("TAG", "      Activity --- onResume  ---");
        update();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        //  menuItemDelete = menu.findItem(R.id.delete);
        //  menuItemDelete.setVisible(false);
        return true;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {

        } else if (id == R.id.undefined) {
            currentKind = Constants.UNDEFINED;
            values = dbHelper.getTasks(currentKind);
            adapter.dataChanged(values);

        } else if (id == R.id.deleted) {
            currentKind = Constants.DELETED;
            values = dbHelper.getTasks(Constants.DELETED);
            adapter.dataChanged(values);
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.exit) {
            this.finish();
        } else if (id == Menu.FIRST){
        } else if (id == R.id.add){

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Добавить раздел");
            //alert.setMessage("Message");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            input.setBackgroundColor(0);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = String.valueOf(input.getText());
                    // Do something with value!


                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

                    Menu menu = navigationView.getMenu();
                    //MenuItem sections =  menu.getItem(R.id.sections);
                    menu.add(R.id.sections,Menu.FIRST,Menu.NONE, value);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });

            alert.show();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
