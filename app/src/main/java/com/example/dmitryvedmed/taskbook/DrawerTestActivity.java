package com.example.dmitryvedmed.taskbook;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.helper.SimpleItemTouchHelperCallback;
import com.example.dmitryvedmed.taskbook.helper.SpacesItemDecoration;

import java.util.List;

public class DrawerTestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private List<SuperTask> values;
    public static DBHelper5 dbHelper;
    public static RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback callback;
    boolean is_in_action_mode = false;
    private  TextView counterTextView;
    private Toolbar toolbar;
    public String currentKind = Constants.UNDEFINED;
    private Context context;
    private Menu menu;
    private FloatingActionButton fab;
    private FloatingActionButton fabAddST;
    private FloatingActionButton fabAddLT;
    CoordinatorLayout coordinatorLayout;
    MenuItem setColor;
    Animation fabAddAnimetion, fabCancelAnimation, fabOpen, fabClose;
    private boolean fabPressed;
    public String getCurrentKind() {
        return currentKind;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawer_test);
        context = this;
        dbHelper = new DBHelper5(this);
        update();
        initView();
        initAnimation();
    }

    private void initAnimation() {
        fabAddAnimetion = AnimationUtils.loadAnimation(this,R.anim.fab_add_rotation);
        fabCancelAnimation = AnimationUtils.loadAnimation(this,R.anim.fab_cancel_rotation);

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
    }

    private void initView() {

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabAddST = (FloatingActionButton) findViewById(R.id.fabAddST);
        fabAddLT = (FloatingActionButton) findViewById(R.id.fabAddLT);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        counterTextView = (TextView) findViewById(R.id.counter_text);
        counterTextView.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_common);
        adapter = new MainRecyclerAdapter(values, DrawerTestActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new SpacesItemDecoration(15));
        callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

      //  drawerLayout.setScrimColor(Color.TRANSPARENT);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //getSupportActionBar().setTitle("1 item selected");

       // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.colorYellow)));

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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


    public void showSnackBar(int i){
        Snackbar.make(coordinatorLayout, i + " заметкок добавлено в корзину!", Snackbar.LENGTH_SHORT)
                .setAction("Отмена", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(coordinatorLayout,"Отменено! или нет...", Snackbar.LENGTH_LONG)
                                .show();
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;


        switch (item.getItemId()){
            case R.id.delete_selection_items:
                Log.d("TAG", "       Adapter --- delete_selection_items");
                showSnackBar(adapter.getSelectedTasksCounter());
                adapter.deleteSelectedTasks();
                break;
            case R.id.select_item:
                Log.d("TAG", "       Adapter --- set selection mode");
                adapter.setSelectionMode(MainRecyclerAdapter.Mode.SELECTION_MODE);
                break;
            case R.id.green:
                adapter.setColorSelectionTasks(Constants.GREEN);
                break;
            case R.id.red:
                adapter.setColorSelectionTasks(Constants.RED);

                break;
            case R.id.blue:
                adapter.setColorSelectionTasks(Constants.BLUE);

                break;
            case R.id.yellow:
                adapter.setColorSelectionTasks(Constants.YELLOW);
                break;
            case R.id.white:
                adapter.setColorSelectionTasks(0);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);


        setColor = menu.findItem(R.id.set_color);
        setColor.setVisible(false);
        //  menuItemDelete = menu.findItem(R.id.delete);
        //  menuItemDelete.setVisible(false);

        /*ArrayList<Section> sections = dbHelper.getAllSections();
        System.out.println("ssssssssssssssssss");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu navmenu = navigationView.getMenu();
        Menu submenu = navmenu.getItem(0).getSubMenu();
        submenu.clear();

        for (Section s:sections
             ) {
            System.out.println(s.getName());
            //MenuItem sections =  menu.getItem(R.id.sections);
            submenu.add(R.id.sections,Menu.FIRST,Menu.NONE, s.getName());
        }*/

        return true;
    }

    public void newSimpleTask(View v){
        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        intent.putExtra("position", adapter.getTasks().size());
        startActivity(intent);
    }

    public void add(View v){
        if(fabPressed){
            fab.startAnimation(fabCancelAnimation);
            fabAddST.startAnimation(fabClose);
            fabAddLT.startAnimation(fabClose);
            fabAddST.setClickable(false);
            fabAddLT.setClickable(false);
            fabPressed = false;
        } else {
            fab.startAnimation(fabAddAnimetion);
            fabAddST.startAnimation(fabOpen);
            fabAddLT.startAnimation(fabOpen);
            fabAddST.setClickable(true);
            fabAddLT.setClickable(true);
            fabPressed = true;
        }
    }

    public void newListTask(View v){
        Intent intent = new Intent(getApplicationContext(), ListTaskActivity.class);
        intent.putExtra("position", adapter.getTasks().size());
        startActivity(intent);
    }

    public void clearList(View v){
        /*if(fab.isShown())
        fab.hide();
        else fab.show();*/

           Log.d("TAG", "      Main3Activity --- clearList  ---");
        dbHelper.clearDB();
        update();
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        System.out.println(item.getTitle());
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        System.out.println("ID = " + id);
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

                    Section section = new Section();
                    section.setName(value);
                    dbHelper.addSection(section);
                    menu.clear();
                    onCreateOptionsMenu(menu);
          /*          Menu menu = navigationView.getMenu();
                    Menu submenu = menu.getItem(0).getSubMenu();
                    //MenuItem sections =  menu.getItem(R.id.sections);
                    submenu.add(R.id.sections,Menu.FIRST,Menu.NONE, value);*/

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void selectedItemCount(int selectedTasksCounter) {
        if(selectedTasksCounter == 0) {
            counterTextView.setVisibility(View.GONE);
            setColor.setVisible(false);
            ((SimpleItemTouchHelperCallback)callback).setCanMovement(true);
        } else {
            ((SimpleItemTouchHelperCallback)callback).setCanMovement(false);
            counterTextView.setVisibility(View.VISIBLE);
            setColor.setVisible(true);
            counterTextView.setText(selectedTasksCounter + " item selected");
        }
    }

    @Override
    protected void onPause() {
        Log.d("TAG", "      Activity --- onPause  ---");
        values = adapter.getTasks();
        // save because positions could change
        for (SuperTask s:values
                ) {
            dbHelper.updateTask(s, currentKind);
        }
        super.onPause();
    }
}
