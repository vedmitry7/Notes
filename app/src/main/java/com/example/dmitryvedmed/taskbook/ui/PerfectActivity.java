package com.example.dmitryvedmed.taskbook.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.helper.SimpleItemTouchHelperCallback;
import com.example.dmitryvedmed.taskbook.helper.SpacesItemDecoration;
import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.Section;
import com.example.dmitryvedmed.taskbook.logic.SuperTask;
import com.example.dmitryvedmed.taskbook.untils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.dmitryvedmed.taskbook.R.drawable.delete;


public class PerfectActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private List<SuperTask> values = new ArrayList<>();
    public static DBHelper5 dbHelper;
    public static RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback callback;
    boolean is_in_action_mode = false;
    private  TextView counterTextView, mainToolbarText;
    private Toolbar toolbar;
    public String currentKind = Constants.UNDEFINED;
    private Context context;
    private Menu menu;
    private FloatingActionButton fab;
    private FloatingActionButton fabAddST;
    private FloatingActionButton fabAddLT;
    public static CoordinatorLayout coordinatorLayout;
    private MenuItem  choose, clearBascet, delateForever, deleteSection, translateTo, undifinedPoint;
    private Animation fabAddAnimetion, fabCancelAnimation, fabOpen, fabClose;
    private boolean fabPressed;
    private SharedPreferences sharedPreferences;
    private ArrayList<Section> sections;
    private Section currentSection;
    private int columnsNumber;
    private SharedPreferences.Editor editor;
    private AlertDialog dialog;
    ActionMode actionMode;
    Bundle instanceState;

    public boolean is_in_action_mode() {
        return is_in_action_mode;
    }

    private ActionMode.Callback actionModeCallback;
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
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            columnsNumber = 2;
        } else columnsNumber = 3;

        loadPreferences();
        update();
        initView();
        initAnimation();
        loadPreferences();
    }

    private void loadPreferences(){
        sharedPreferences = this.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabAddST = (FloatingActionButton) findViewById(R.id.fabAddST);
        //  fabAddST.setVisibility(View.INVISIBLE);
        fabAddLT = (FloatingActionButton) findViewById(R.id.fabAddLT);
        //  fabAddLT.setVisibility(View.INVISIBLE);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("TAG", "                  touch toolbar)");
                hideFabs();
                return false;
            }
        });


        //  counterTextView = (TextView) findViewById(R.id.counter_text2);
        //counterTextView.setVisibility(View.GONE);

        mainToolbarText = (TextView) findViewById(R.id.mainToolbarText);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_common);
        adapter = new MainRecyclerAdapter(values, PerfectActivity.this);
        RecyclerView.LayoutManager layoutManager;
        String s = sharedPreferences.getString(Constants.MAIN_RECYCLER_LAYOUT, Constants.LAYOUT_LIST);

        if(s.equals(Constants.LAYOUT_LIST)) {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new StaggeredGridLayoutManager(columnsNumber, 1);
        }
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new SpacesItemDecoration());
        callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //  drawerLayout.setScrimColor(Color.TRANSPARENT);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "      TOOOOOGLE CLICK ---");
                hideFabs();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_selection_mode, menu);

                if(currentKind.equals(Constants.DELETED)){
                    MenuItem delete = menu.findItem(R.id.delete_selection_items);
                    delete.setIcon(getResources().getDrawable(R.drawable.delete_forever_2));
                }
                if(currentKind.equals(Constants.ARCHIVE)){
                    MenuItem unarchive = menu.findItem(R.id.toArchive);
                    unarchive.setIcon(getResources().getDrawable(R.drawable.ic_unarch));
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.green:
                        adapter.setColorSelectionTasks(Constants.GREEN);
                        fab.show();
                        break;
                    case R.id.red:
                        adapter.setColorSelectionTasks(Constants.RED);
                        fab.show();
                        break;
                    case R.id.blue:
                        adapter.setColorSelectionTasks(Constants.BLUE);
                        fab.show();
                        break;
                    case R.id.yellow:
                        adapter.setColorSelectionTasks(Constants.YELLOW);
                        fab.show();
                        break;
                    case R.id.white:
                        adapter.setColorSelectionTasks(0);
                        break;
                    case R.id.set_color_3:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        //builder.setTitle("Выберете цвет");
                        builder.setView(R.layout.dialog_choose_color);
                        dialog = builder.create();
                        dialog.show();
                        break;

                    case R.id.delete_selection_items:
                        Log.d("TAG", "       Adapter --- delete_selection_items");

                        if(currentKind == Constants.DELETED) {

                            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            // alert.setTitle("Очистить корзину?");
                            alert.setMessage(R.string.delete_forever_massage);
                            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    adapter.deleteSelectedTasksForever();
                                    mode.finish();
                                }
                            });
                            alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            alert.show();
                            break;
                        }
                        showSnackBar(Constants.DELETED, adapter.getSelectedTasksCounter());
                        adapter.deleteSelectedTasks();
                        mode.finish();
                        break;
                    case R.id.set_color:
           /*         final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                    View mViewe = getLayoutInflater().inflate(R.layout.color_layout, null);
                    mBuilder.setCancelable(true);
                    mBuilder.setView(mViewe);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            Log.d("TAG", "       Adapter --- CANCEL LISTENER ");
                        }
                    });
                    dialog.show();*/
                        break;
                    case R.id.toArchive:
                        if(currentKind.equals(Constants.ARCHIVE)){
                            adapter.translateTo(Constants.UNDEFINED);
                        } else {
                            adapter.translateTo(Constants.ARCHIVE);
                        }
                        break;

                    case R.id.translateTo:

                        MenuBuilder menuBuilder = new MenuBuilder(context);

                        MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, toolbar);
                        optionsMenu.setForceShowIcon(true);


                        PopupMenu popupMenu = new PopupMenu(context, toolbar, Gravity.TOP);

                        if(currentKind != Constants.UNDEFINED)
                            popupMenu.getMenu().add(R.string.main_section);
                        sections = dbHelper.getAllSections();
                        for (Section section:sections
                                ) {
                            popupMenu.getMenu().add(section.getName());
                        }
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Log.d("TAG", "           click pop up NAME " + item.getItemId() + item.getTitle());
                                if(item.getTitle().equals(R.string.main_section)){
                                    adapter.translateTo(Constants.UNDEFINED);
                                } else
                                    adapter.translateTo((String) item.getTitle());
                                onNavigationItemSelected(undifinedPoint);
                                mode.finish();
                                return true;
                            }
                        });
                        popupMenu.show();
                        break;
                }


                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.cancelSelection();
            }
        };
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("TAG", "                               onSaveInstanceState!!!!!!!!!!!!!");
        if(is_in_action_mode) {
            outState.putInt(Constants.ACTION_MODE, 1);
            outState.putBooleanArray(Constants.SELLECTION_ARRAY, adapter.getSelects());
            editor.putInt(Constants.SELECTED_ITEM_COUNT, adapter.getSelectedTasksCounter());
            editor.commit();

            outState.putIntegerArrayList(Constants.SELECTED_ITEM_IDS, adapter.getSelectedListIds());
        }
        else {
            outState.putInt(Constants.ACTION_MODE, 0);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("TAG", "                              onRestoreInstanceState!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if(savedInstanceState!=null && savedInstanceState.getInt(Constants.ACTION_MODE)==1){
            is_in_action_mode = true;
            Log.d("TAG", "                              onRestoreInstanceState!!!!!!!!!!!!!!!!!!!!!!!!!!!  A M = true");
            setSelectionMode();
            adapter.setSelectedTasksCounter(savedInstanceState.getInt(Constants.SELECTED_ITEM_COUNT));
            adapter.setSelects(savedInstanceState.getBooleanArray(Constants.SELLECTION_ARRAY));
            adapter.fillSelectedTasks(savedInstanceState.getIntegerArrayList(Constants.SELECTED_ITEM_IDS));
            //  actionMode.setTitle(String.valueOf(savedInstanceState.getInt(Constants.SELECTED_ITEM_COUNT)));
            actionMode.setTitle(String.valueOf(sharedPreferences.getInt(Constants.SELECTED_ITEM_COUNT,0)));
            adapter.setSelectedTasksCounter(sharedPreferences.getInt(Constants.SELECTED_ITEM_COUNT,0));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        menu.clear();
        if(this.menu!=null)
            this.menu.clear();
        if(currentKind.equals(Constants.DELETED)) {
            getMenuInflater().inflate(R.menu.bucket_menu, menu);
            clearBascet = menu.findItem(R.id.clear_basket);
            if(values.size()==0){
                clearBascet.setVisible(false);
            }
        } else {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }
        this.menu = menu;
        onCreateNavigationMenu();
        return  true;

/*


            choose = menu.findItem(R.id.select_item);
            clearBascet = menu.findItem(R.id.clear_basket);
            if(currentKind==Constants.DELETED && adapter.getTasks().size() !=0)
                clearBascet.setVisible(true);
            else
                clearBascet.setVisible(false);
            delateForever = menu.findItem(R.id.delete_forever);
            delateForever.setVisible(false);

            deleteSection = menu.findItem(R.id.deleteSection);
            deleteSection.setVisible(false);
            translateTo = menu.findItem(R.id.translateTo);
            translateTo.setVisible(false);
*/




        //  menuItemDelete = menu.findItem(R.id.delete);
        //  menuItemDelete.setVisible(false);

    }

    private void onCreateNavigationMenu() {
        sections = dbHelper.getAllSections();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu navMenu = navigationView.getMenu();
        undifinedPoint = navMenu.findItem(R.id.undefined);
        navMenu.clear();

        navMenu.add(Menu.NONE, R.id.undefined , Menu.NONE, R.string.all).setIcon(getResources().getDrawable(R.drawable.note_multiple));
        navMenu.add(Menu.NONE, R.id.archive , Menu.NONE, R.string.archive).setIcon(getResources().getDrawable(R.drawable.archive_2));
        for (Section s:sections
                ) {
            Log.d("TAG", "Section " + s.getName() + " id " + s.getId());
            navMenu.add(45, s.getId(), Menu.NONE, s.getName());
        }
        navMenu.add(45, R.id.add, Menu.NONE, R.string.newPoint).setIcon(getResources().getDrawable(R.drawable.ic_add));
        navMenu.add(Menu.NONE, R.id.deleted , Menu.NONE, R.string.bucket).setIcon(getResources().getDrawable(delete));
        navMenu.add(Menu.NONE, R.id.settings , Menu.NONE, R.string.settings).setIcon(getResources().getDrawable(R.drawable.settings));
        navMenu.add(Menu.NONE, R.id.exit , Menu.NONE, R.string.exit).setIcon(getResources().getDrawable(R.drawable.exit_to_app));
        //  navMenu.add(Menu.NONE, 245 , Menu.NONE,"clear sections");
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (adapter.getMode()== MainRecyclerAdapter.Mode.SELECTION_MODE){
            adapter.cancelSelection();
            fab.show();
        } else {
            super.onBackPressed();
        }
    }

    public void showSnackBar(String s, int i){
        switch (s){
            case Constants.DELETED:
                Snackbar.make(coordinatorLayout, i + " заметкок добавлено в корзину!", Snackbar.LENGTH_SHORT)
                        .setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar.make(coordinatorLayout,"Отмена!", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        })
                        .show();
                break;
            case Constants.ARCHIVE:
                Snackbar.make(coordinatorLayout, i + " заметкок архивированно!", Snackbar.LENGTH_SHORT)
                        .setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar.make(coordinatorLayout,"Отмена!", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        })
                        .show();
                break;
            default:
                Snackbar.make(coordinatorLayout, i + " заметкок добавлено в раздел " + s, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar.make(coordinatorLayout,"Отмена!", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        })
                        .show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("TAG", "        onOptionsItemSelected  onOptionsItemSelected  onOptionsItemSelected");

        if(toggle.onOptionsItemSelected(item))
            return true;

        hideFabs();
        switch (item.getItemId()){
            case R.id.delete_selection_items:
                Log.d("TAG", "       Adapter --- delete_selection_items");
                showSnackBar(Constants.DELETED, adapter.getSelectedTasksCounter());
                adapter.deleteSelectedTasks();
                break;
            case R.id.select_item:
                Log.d("TAG", "       Adapter --- set selection mode");
                setSelectionMode();
                break;
            case R.id.change_view:

                String s = sharedPreferences.getString(Constants.MAIN_RECYCLER_LAYOUT, Constants.LAYOUT_LIST);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                RecyclerView.LayoutManager layoutManager;

                if(s.equals(Constants.LAYOUT_LIST)) {
                    layoutManager = new StaggeredGridLayoutManager(columnsNumber, 1);
                    editor.putString(Constants.MAIN_RECYCLER_LAYOUT, Constants.LAYOUT_GRID);

                } else {
                    layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    editor.putString(Constants.MAIN_RECYCLER_LAYOUT, Constants.LAYOUT_LIST);
                }
                editor.commit();
                recyclerView.setLayoutManager(layoutManager);
                adapter.notifyDataSetChanged();
                break;
            case R.id.delete_forever:
                Log.d("TAG", "      Main3Activity           RRR delete_forever");

                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                // alert.setTitle("Очистить корзину?");
                alert.setMessage(R.string.delete_forever_massage);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.deleteSelectedTasksForever();
                        delateForever.setVisible(false);
                        Log.d("TAG", "                      CCCCCLLLLLLLLIIIIIIRRRRRRRRR ++++++");
                        clearBascet.setVisible(true);
                    }
                });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.show();
                break;
            case R.id.clear_basket:
                Log.d("TAG", "      Main3Activity           RRR CLEAR BASKET");
                final AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
                alert2.setTitle(R.string.question_clear_basket);
                alert2.setMessage(R.string.question_clear_busket_additional);
                alert2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (SuperTask t:dbHelper.getTasks(Constants.DELETED)
                                ) {
                            dbHelper.deleteTask(t);
                        }
                        adapter.getTasks().clear();
                        adapter.notifyDataSetChanged();
                        Log.d("TAG", "                      CCCCCLLLLLLLLIIIIIIRRRRRRRRR ------clear_basket");

                        clearBascet.setVisible(false);
                    }
                });
                alert2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert2.show();
                break;
            case R.id.deleteSection:
                AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
                alert3.setTitle(R.string.question_delete_section);
                alert3.setMessage(R.string.delete_section_massage);

                alert3.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.deleteSection();
                        deleteSection.setVisible(false);
                        onNavigationItemSelected(undifinedPoint);
                    }
                });
                alert3.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert3.show();
                dbHelper.deleteSection(currentSection);
                sections.remove(currentSection);
                currentSection = null;
                onCreateOptionsMenu(menu);
                break;
            case R.id.translateTo:

                MenuBuilder menuBuilder = new MenuBuilder(this);

                MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, toolbar);
                optionsMenu.setForceShowIcon(true);


                PopupMenu popupMenu = new PopupMenu(this, toolbar, Gravity.TOP);

                sections = dbHelper.getAllSections();
                for (Section section:sections
                        ) {
                    popupMenu.getMenu().add(section.getName()).setIcon(getResources().getDrawable(R.drawable.delete));
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d("TAG", "           click pop up NAME " + item.getItemId() + item.getTitle());
                        adapter.translateTo((String) item.getTitle());
                        onNavigationItemSelected(undifinedPoint);
                        return true;
                    }
                });
                popupMenu.show();
                /*PopupMenu popup = new PopupMenu(this, toolbar);
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon",boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popup.getMenuInflater().inflate(R.menu.popupmenu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                });
                popup.show();*/
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveCurrentKind(){
        editor.putString(Constants.CURRENT_KIND, currentKind);
        editor.commit();
    }

    private void sectionWasChanged(){
        values = dbHelper.getTasks(currentKind);
        if(currentKind.equals(Constants.DELETED))
            checkOldTask();
        onCreateOptionsMenu(menu);
        adapter.dataChanged(values);
        saveCurrentKind();
        setTitle();
    }

    private void setTitle() {
        switch (currentKind){
            case Constants.DELETED:
                mainToolbarText.setText(R.string.bucket);
                setItemMovement(false);
                break;
            case Constants.ARCHIVE:
                setItemMovement(false);
                mainToolbarText.setText(R.string.archive);
                break;
            case Constants.UNDEFINED:
                setItemMovement(true);
                mainToolbarText.setText("");
                break;
            default:
                setItemMovement(true);
                mainToolbarText.setText(currentKind);
                break;
        }
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        System.out.println(item.getTitle());
        // Handle navigation view item clicks here.
        hideFabs();
        int id = item.getItemId();
        System.out.println("ID = " + id);
        Log.d("TAG", "           CLICK ITEM " + item.getItemId());
        for (Section s:sections
                ) {
            Log.d("TAG", "           SECTIONS NAME " + s.getName() + "id" +  item.getItemId());
            if(item.getItemId() == s.getId()){
                setItemMovement(false);
                currentKind = s.getName();
                currentSection = s;
                Log.d("TAG", "         YESSS " + s.getName() + " id " +  item.getItemId());
                sectionWasChanged();
                super.onOptionsItemSelected(item);
                break;
            }
        }

        switch (item.getItemId()){

            case R.id.undefined:
                setItemMovement(true);
                currentKind = Constants.UNDEFINED;
                fab.show();
                sectionWasChanged();
                break;
            case R.id.deleted:
                currentKind = Constants.DELETED;
                sectionWasChanged();
                setItemMovement(false);
                fab.hide();
                break;
            case R.id.archive:
                setItemMovement(true);
                mainToolbarText.setText(R.string.archive);
                currentKind = Constants.ARCHIVE;
                sectionWasChanged();
                break;
            case R.id.notifications:
                mainToolbarText.setText(R.string.notifications);
                //currentKind = Constants.NOTIFICATIONS;
                deleteSection.setVisible(false);
                values = dbHelper.getNotificationTasks();
                adapter.dataChanged(values);
                break;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.exit:
                this.finish();
                break;
            case 245:
                dbHelper.clearSectionTable();
                sections = dbHelper.getAllSections();
                onCreateNavigationMenu();
                break;
        }

        if (id == R.id.add){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.add_section);
            //alert.setMessage("Message");
            // Set an EditText view to get user input
            View mView = getLayoutInflater().inflate(R.layout.dialog_add_section, null);

            final EditText input = (EditText) mView.findViewById(R.id.text_view_dialog_add_section);
            input.setBackgroundColor(0);
            alert.setView(mView);

            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = String.valueOf(input.getText());
                    // Do something with value!
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("TAG", "                   NavigationView CLICK");
                        }
                    });
                    Section section = new Section();
                    section.setName(value);
                    section.setPosition(sections.size());
                    int sectionId = dbHelper.addSection(section);
                    section.setId(sectionId);
                    dbHelper.updateSection(section);
                    menu.clear(); //!!!!!!!!!!
                    onCreateOptionsMenu(menu);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                }
            });

            alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                }
            });

            alert.show();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    private void checkOldTask() {
        long deletionPeriod = sharedPreferences.getLong(Constants.DELETION_PERIOD, Constants.PERIOD_WEEK);
        for (SuperTask task : values
                ) {
            if(task.getDeletionTime() + deletionPeriod < System.currentTimeMillis()) {
                dbHelper.deleteTask(task);
            }
        }
        values = dbHelper.getTasks(Constants.DELETED);
    }

    public void setSelectionMode(){
        actionMode = startActionMode(actionModeCallback);
        is_in_action_mode = true;
        adapter.setSelectionMode(MainRecyclerAdapter.Mode.SELECTION_MODE);
        //onCreateOptionsMenu(menu);
        editor.putInt(Constants.ACTION_MODE, 1);
        editor.commit();

        fab.hide();
    }

    public void selectedItemCount(int selectedTasksCounter) {
        if(selectedTasksCounter == 0) {
            actionMode.finish();
            is_in_action_mode = false;
            setItemMovement(true);
            fab.show();
            onCreateOptionsMenu(menu);
            editor.putInt(Constants.ACTION_MODE, 0);
            editor.commit();
        } else {
            actionMode.setTitle(String.valueOf(selectedTasksCounter));
            setItemMovement(false);
            if(currentKind.equals(Constants.DELETED)) {
                Log.d("TAG", "                      CCCCCLLLLLLLLIIIIIIRRRRRRRRR --------selectedItemCount");
            }
        }
    }

    public void hideFabs(){
        Log.d("TAG", "      Main3Activity --- HIDE FABS  ---");
        if(!fabPressed)
            return;
        fab.startAnimation(fabCancelAnimation);
        fabAddST.startAnimation(fabClose);
        fabAddLT.startAnimation(fabClose);
        fabAddST.setClickable(false);
        fabAddLT.setClickable(false);
        fabAddST.setVisibility(View.INVISIBLE);
        fabAddLT.setVisibility(View.INVISIBLE);
        fabPressed = false;
    }

    public void add(View v){
        if(fabPressed){
            hideFabs();
        } else {
            fab.startAnimation(fabAddAnimetion);
            fabAddST.startAnimation(fabOpen);
            fabAddLT.startAnimation(fabOpen);
            fabAddST.setClickable(true);
            fabAddLT.setClickable(true);
            fabAddST.setVisibility(View.VISIBLE);
            fabAddLT.setVisibility(View.VISIBLE);
            fabPressed = true;
        }
    }

    public void newListTask(View v){
        hideFabs();
        Intent intent = new Intent(getApplicationContext(), ListTaskActivity.class);
        intent.putExtra(Constants.POSITION, adapter.getTasks().size());
        startActivity(intent);
    }


    public void newSimpleTask(View v){
        hideFabs();
        Intent intent = new Intent(getApplicationContext(), SimpleTaskActivity.class);
        intent.putExtra(Constants.POSITION, adapter.getTasks().size());
        startActivity(intent);
    }

    public void clearList(View v){
        Log.d("TAG", "      Main3Activity --- clearList  ---");
        dbHelper.clearDB();
        update();
    }

    private void initAnimation() {
        fabAddAnimetion = AnimationUtils.loadAnimation(this,R.anim.fab_add_rotation);
        fabCancelAnimation = AnimationUtils.loadAnimation(this,R.anim.fab_cancel_rotation);

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
    }


    public void setItemMovement(boolean b){
        ((SimpleItemTouchHelperCallback)callback).setCanMovement(b);
    }

    @Override
    protected void onPause() {
        Log.d("TAG", "      Activity --- onPause  ---");
        values = adapter.getTasks();
        // save because positions could change
        for (SuperTask s:values
                ) {
            if(!dbHelper.isRemind(s))
                s.setRemind(false);
            dbHelper.updateTask(s, currentKind);
        }

        super.onPause();
    }

    void update(){
        Log.d("TAG", "      Activity --- update  ---");
        currentKind = sharedPreferences.getString(Constants.CURRENT_KIND, Constants.UNDEFINED);
        values = dbHelper.getTasks(currentKind);
        if(adapter!=null)
            adapter.dataChanged(values);

        for (SuperTask s:values
                ) {
            if(s.isRemind()==true&& s.getReminderTime()<System.currentTimeMillis()){
                Log.d("TAG", "      Activity                    DEPRICATED TASK!!!" + s.getId());
            }
        }
    }


    @Override
    protected void onRestart() {
        Log.d("TAG", "    Activity --- onRESTART  ---");
        Log.d("TAG", "!!!!!!!!! sel size = " + adapter.getSelectedTasks().size());

        update();
        adapter.updateSelectedTask();

        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d("TAG", "      Activity --- onResume  ---      "  + currentKind);
        Log.d("TAG", "!!!!!!!!! sel size = " + adapter.getSelectedTasks().size());

        setTitle();
        // update();
        // onCreateNavigationMenu();
        super.onResume();
    }

    public void setColor(View v){
        switch (v.getId()){
            case R.id.green:
                adapter.setColorSelectionTasks(Constants.GREEN);
                fab.show();
                break;
            case R.id.red:
                adapter.setColorSelectionTasks(Constants.RED);
                fab.show();
                break;
            case R.id.blue:
                adapter.setColorSelectionTasks(Constants.BLUE);
                fab.show();
                break;
            case R.id.yellow:
                adapter.setColorSelectionTasks(Constants.YELLOW);
                fab.show();
                break;
            case R.id.white:
                adapter.setColorSelectionTasks(0);
                break;
            case R.id.pink:
                adapter.setColorSelectionTasks(Constants.RED);
                break;
        }
        dialog.dismiss();
    }

    public String getCurrentKind() {
        return currentKind;
    }

    private void compareSections(){
        Log.d("TAG", "       Adapter --- compareSections");
        Comparator<Section> comparator = new Comparator<Section>() {
            @Override
            public int compare(Section section, Section t1) {
                return section.getPosition() < t1.getPosition() ? 1 : -1;
            }
        };
        Collections.sort(sections, comparator);
    }

    private void setRightPosition(){
        Log.d("TAG", "       Adapter --- setRightPosition");
        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).setPosition(sections.size()-i-1);
        }
    }
}
