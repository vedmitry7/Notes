package com.example.dmitryvedmed.taskbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.helper.SimpleItemTouchHelperCallback;

import java.util.List;

public class Main3Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnLongClickListener {

    List<SuperTask> values;
    public static DBHelper5 dbHelper;
    public static RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    public static Mode mode;
    boolean is_in_action_mode = false;
    TextView counterTextView;
    Toolbar toolbar;
    String currentKind = Constants.UNDEFINED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "      Main3Activity --- onCreate  ---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        dbHelper = new DBHelper5(this);
        update();
        initView();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        counterTextView = (TextView) findViewById(R.id.counter_text);
        counterTextView.setVisibility(View.GONE);


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        menu.add(Menu.NONE,Menu.FIRST,Menu.NONE,"My пункт");
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_common);

        adapter = new MainRecyclerAdapter(values, Main3Activity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("qq " );
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public boolean onLongClick(View view) {
        Log.d("TAG", "      Main3Activity --- onLongClick  ---");
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_action_mode);
        counterTextView.setVisibility(View.VISIBLE);
        is_in_action_mode = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return false;
    }

    public void prepareSelection(View view){
    }
    public void updateCounter(int counter){
        if(counter == 0){
            counterTextView.setText("0 item selected");
        } else {
            counterTextView.setText(counter + " item selected");
        }


    }

    public static enum Mode {
        NORMAL, REMOVE, SELECTED;

    }

    public void newSimpleTask(View v){
        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        intent.putExtra("position", adapter.getTasks().size());
        startActivity(intent);
    }

    public void newListTask(View v){
        Intent intent = new Intent(getApplicationContext(), ListTaskActivity.class);
        intent.putExtra("position", adapter.getTasks().size());
        startActivity(intent);
    }

    public void clearList(View v){
        Log.d("TAG", "      Main3Activity --- clearList  ---");
        dbHelper.clearDB();
        update();
    }

    @Override
    protected void onResume() {
        Log.d("TAG", "      Main3Activity --- onResume  ---");
        update();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("TAG", "      Main3Activity --- onPause  ---");
        values = adapter.getTasks();
        // save because positions could change
        for (SuperTask s:values
             ) {
            dbHelper.updateTask(s, currentKind);
        }
        super.onPause();
    }

    void update(){
        Log.d("TAG", "      Main3Activity --- update  ---");
        values = dbHelper.getTasks(currentKind);
        if(adapter!=null)
        adapter.dataChanged(values);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("MENUUUUUUUUUU");
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
      //  menuItemDelete = menu.findItem(R.id.delete);
      //  menuItemDelete.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
   /*     if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

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
            Log.d("TAG", "      Main3Activity --- my пункт  ---");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
