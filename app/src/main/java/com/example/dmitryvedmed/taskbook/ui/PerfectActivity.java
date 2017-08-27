package com.example.dmitryvedmed.taskbook.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.NotifyTaskReceiver;
import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.helper.SimpleItemTouchHelperCallback;
import com.example.dmitryvedmed.taskbook.helper.SpacesItemDecoration;
import com.example.dmitryvedmed.taskbook.logic.DBHelper;
import com.example.dmitryvedmed.taskbook.logic.Section;
import com.example.dmitryvedmed.taskbook.logic.SuperNote;
import com.example.dmitryvedmed.taskbook.untils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.dmitryvedmed.taskbook.untils.Constants.NOTIF_ON;


public class PerfectActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;
    public static DBHelper sDbHelper;
    private MainRecyclerAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback mCallback;

    public static RecyclerView sRecyclerView;
    public static CoordinatorLayout sCoordinatorLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private FloatingActionButton mFabAddST;
    private FloatingActionButton mFabAddLT;
    private TextView mTextNoNotes;

    private Menu mMenu;
    private MenuItem mChangeView;
    private MenuItem mClearBasket, mMainSection;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Animation mFabAddAnimation, mFabCancelAnimation, mFabOpen, mFabClose;
    private Section mCurrentSection;
    private AlertDialog mDialog;
    private ActionMode mActionMode;

    private List<SuperNote> mValues = new ArrayList<>();
    private ArrayList<Section> mSections;

    private boolean is_action_mode_on = false;
    private int mColumnsNumber;
    private boolean mNotification_on;
    private boolean mFabPressed;
    public String currentKind = Constants.UNDEFINED;

    private InterstitialAd interstitialAd;

    public boolean is_in_action_mode() {
        return is_action_mode_on;
    }

    private ActionMode.Callback actionModeCallback;

    public boolean ismNotification_on() {
        return mNotification_on;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        initAds();

        mContext = this;
        sDbHelper = new DBHelper(this);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mColumnsNumber = 2;
        } else {
            mColumnsNumber = 3;
        }

        loadPreferences();
        checkDeprecated();
        checkRepeatingNotes();
        update();
        initView();
        initAnimation();
    }

    private void initAds() {

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        requestNewInterstitial();

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mAdapter.startNoteActivity(false);
                requestNewInterstitial();
            }
        });
    }

    public void showAd(){
        if(interstitialAd.isLoaded()){
            interstitialAd.show();
        } else {
            mAdapter.startNoteActivity(true);
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        interstitialAd.loadAd(adRequest);

    }

    private void loadPreferences(){
        mSharedPreferences = this.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mNotification_on = mSharedPreferences.getBoolean(Constants.NOTIF_ON, false);

        if(mSharedPreferences.getString("first_launch","321").equals("321")) {
            mEditor.putInt(Constants.MORNING_TIME_HOURS, 7);
            mEditor.putInt(Constants.MORNING_TIME_MINUTES, 0);
            mEditor.putInt(Constants.AFTERNOON_TIME_HOURS, 13);
            mEditor.putInt(Constants.AFTERNOON_TIME_MINUTES, 0);
            mEditor.putInt(Constants.EVENING_TIME_HOURS, 19);
            mEditor.putInt(Constants.EVENING_TIME_MINUTES, 0);
            mEditor.putString("first_launch", "123");
            mEditor.commit();
        }
    }

    private void initView() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFabAddST = (FloatingActionButton) findViewById(R.id.fabListNote);
        mFabAddLT = (FloatingActionButton) findViewById(R.id.fabSimpleNote);

        sCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mTextNoNotes = (TextView) findViewById(R.id.textNoNotes);
        mTextNoNotes.setText(R.string.empty);
        isSectionEmpty();
        if(mSharedPreferences.getBoolean(NOTIF_ON, false)){
        }

        setSupportActionBar(mToolbar);

        sRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_common);
        mAdapter = new MainRecyclerAdapter(mValues, PerfectActivity.this);
        RecyclerView.LayoutManager layoutManager;
        String s = mSharedPreferences.getString(Constants.MAIN_RECYCLER_LAYOUT, Constants.LAYOUT_LIST);

        if(s.equals(Constants.LAYOUT_LIST)) {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new StaggeredGridLayoutManager(mColumnsNumber, 1);
        }

        sRecyclerView.setLayoutManager(layoutManager);
        sRecyclerView.setHasFixedSize(true);
        sRecyclerView.setAdapter(mAdapter);

        sRecyclerView.addItemDecoration(new SpacesItemDecoration());
        mCallback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(sRecyclerView);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);

        mToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                getMenuInflater().inflate(R.menu.menu_selection_mode, menu);

                if(currentKind.equals(Constants.DELETED)){
                    MenuItem delete = menu.findItem(R.id.delete_selection_items);
                    delete.setIcon(getResources().getDrawable(R.drawable.ic_delete_forever_white_36dp));
                }
                if(currentKind.equals(Constants.ARCHIVE)){
                    MenuItem unarchive = menu.findItem(R.id.toArchive);
                    unarchive.setIcon(getResources().getDrawable(R.drawable.ic_package_up_white_36dp));
                }

                if(mNotification_on){
                    MenuItem unarchive = menu.findItem(R.id.toArchive);
                    MenuItem translate = menu.findItem(R.id.translateTo);
                    MenuItem delete = menu.findItem(R.id.delete_selection_items);
                    delete.setVisible(false);
                    unarchive.setVisible(false);
                    translate.setVisible(false);
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

                    case R.id.set_color_3:
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(R.layout.dialog_choose_color);
                        mDialog = builder.create();
                        mDialog.show();
                        break;

                    case R.id.delete_selection_items:
                        if(currentKind == Constants.DELETED) {

                            final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                            alert.setMessage(R.string.delete_forever_massage);
                            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mAdapter.deleteSelectedTasksForever();
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
                        showSnackBar(Constants.DELETED, mAdapter.getSelectedNotesCounter());
                        mAdapter.translateTo(Constants.DELETED);
                        mode.finish();
                        break;

                    case R.id.toArchive:
                        if(currentKind.equals(Constants.ARCHIVE)){
                            mAdapter.translateTo(Constants.UNDEFINED);
                        } else {
                            mAdapter.translateTo(Constants.ARCHIVE);
                        }
                        break;

                    case R.id.translateTo:

                        mSections = sDbHelper.getAllSections();
                        if(mSections.size() == 0 && !currentKind.equals(Constants.DELETED)){
                            Snackbar.make(sCoordinatorLayout, R.string.no_sections, Snackbar.LENGTH_SHORT)
                                    .show();
                            break;
                        }
                        MenuBuilder menuBuilder = new MenuBuilder(mContext);

                        MenuPopupHelper optionsMenu = new MenuPopupHelper(mContext, menuBuilder, mToolbar);
                        optionsMenu.setForceShowIcon(true);

                        PopupMenu popupMenu = new PopupMenu(mContext, mToolbar, Gravity.TOP);

                        if(currentKind != Constants.UNDEFINED) {
                            popupMenu.getMenu().add(354, Menu.NONE,Menu.NONE, R.string.main_section);
                        }

                        for (Section section: mSections
                                ) {
                            popupMenu.getMenu().add(section.getName());
                        }
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //    if(item.getTitle().equals(R.string.main_section)){
                                if(item.getGroupId() == 354){
                                    mAdapter.translateTo(Constants.UNDEFINED);
                                } else {
                                    mAdapter.translateTo((String) item.getTitle());
                                }
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
                mAdapter.cancelSelection();
            }
        };
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(is_action_mode_on) {
            outState.putInt(Constants.ACTION_MODE, 1);
            outState.putBooleanArray(Constants.SELLECTION_ARRAY, mAdapter.getSelects());
            mEditor.putInt(Constants.SELECTED_ITEM_COUNT, mAdapter.getSelectedNotesCounter());
            mEditor.commit();
            outState.putIntegerArrayList(Constants.SELECTED_ITEM_IDS, mAdapter.getSelectedListIds());
        }
        else {
            outState.putInt(Constants.ACTION_MODE, 0);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState!=null && savedInstanceState.getInt(Constants.ACTION_MODE)==1){
            is_action_mode_on = true;
            setSelectionMode();
            mAdapter.setSelectedNotesCounter(savedInstanceState.getInt(Constants.SELECTED_ITEM_COUNT));
            mAdapter.setSelects(savedInstanceState.getBooleanArray(Constants.SELLECTION_ARRAY));
            mAdapter.fillSelectedTasks(savedInstanceState.getIntegerArrayList(Constants.SELECTED_ITEM_IDS));
            mActionMode.setTitle(String.valueOf(mSharedPreferences.getInt(Constants.SELECTED_ITEM_COUNT,0)));
            mAdapter.setSelectedNotesCounter(mSharedPreferences.getInt(Constants.SELECTED_ITEM_COUNT,0));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        if(this.mMenu !=null)
            this.mMenu.clear();

        if(mNotification_on || currentKind.equals(Constants.UNDEFINED)|| currentKind.equals(Constants.ARCHIVE)){
            getMenuInflater().inflate(R.menu.main_menu, menu);
        } else
        if(currentKind.equals(Constants.DELETED)) {
            getMenuInflater().inflate(R.menu.bucket_menu, menu);
            mClearBasket = menu.findItem(R.id.clear_basket);
            if(mValues.size()==0){
                mClearBasket.setVisible(false);
            }
        } else
        {
            getMenuInflater().inflate(R.menu.section_menu, menu);
        }

        this.mMenu = menu;
        mChangeView = menu.findItem(R.id.change_view);
        setChangeViewMenuItemIcon();
        onCreateNavigationMenu();
        return  true;
    }

    private void setChangeViewMenuItemIcon() {
        if(mChangeView ==null) {
            return;
        }
        String s = mSharedPreferences.getString(Constants.MAIN_RECYCLER_LAYOUT, Constants.LAYOUT_LIST);
        if(s.equals(Constants.LAYOUT_LIST)) {
            mChangeView.setIcon(getResources().getDrawable(R.drawable.ic_view_dashboard_white_36dp));
        } else {
            mChangeView.setIcon(getResources().getDrawable(R.drawable.ic_view_stream_white_36dp));
        }
    }

    private void onCreateNavigationMenu() {
        mSections = sDbHelper.getAllSections();

        compareSections();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu navMenu = navigationView.getMenu();
        navMenu.clear();

        navMenu.add(Menu.NONE, Constants.KEY_GENERAL_SECTION , Menu.NONE, R.string.main_section).setIcon(getResources().getDrawable(R.drawable.ic_note_multiple_grey600_36dp));
        navMenu.add(Menu.NONE, Constants.KEY_NOTIFICATIONS_SECTION , Menu.NONE, R.string.notifications).setIcon(getResources().getDrawable(R.drawable.ic_bell_grey600_36dp));
        navMenu.add(Menu.NONE, Constants.KEY_ARCHIVE_SECTION , Menu.NONE, R.string.archive).setIcon(getResources().getDrawable(R.drawable.ic_package_down_grey600_36dp));

        mMainSection = navMenu.findItem(Constants.KEY_GENERAL_SECTION);

        for (Section s: mSections
                ) {
            navMenu.add(45, s.getId(), Menu.NONE, s.getName());
        }

        navMenu.add(45, R.id.add, Menu.NONE, R.string.newPoint).setIcon(getResources().getDrawable(R.drawable.ic_plus_grey600_36dp));
        navMenu.add(Menu.NONE, Constants.KEY_DELETED_SECTION , Menu.NONE, R.string.bucket).setIcon(getResources().getDrawable(R.drawable.ic_delete_grey600_36dp));
        navMenu.add(Menu.NONE, Constants.KEY_SETTINGS , Menu.NONE, R.string.settings).setIcon(getResources().getDrawable(R.drawable.ic_settings_grey600_36dp));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mAdapter.getMode()== MainRecyclerAdapter.Mode.SELECTION_MODE){
            mAdapter.cancelSelection();
            mFab.show();
        } else {
            super.onBackPressed();
        }
    }

    public void showSnackBar(String s, int i){

        String s1;

        switch (s){
            case Constants.UNDEFINED:
                s1 =  getString(R.string.notes_added_to_main_section) + " (" + i + ")";
                break;
            case Constants.DELETED:
                s1 = getString(R.string.notes_added_to_bucket) + " (" + i + ")";
                break;
            case Constants.ARCHIVE:
                s1 = getString(R.string.notes_archived) + " (" + i + ")";
                break;
            default:
                s1 = getString(R.string.notes_added_to) +" " + s + " (" + i + ")";
        }
        Snackbar.make(sCoordinatorLayout, s1, Snackbar.LENGTH_SHORT)
                .setAction(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showSnackBarCancel();
                        mAdapter.returnTranslatedTask(currentKind);

                    }
                })
                .show();
    }

    private void showSnackBarCancel(){
        Snackbar.make(sCoordinatorLayout,R.string.cancel, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        hideFabs();
        switch (item.getItemId()){
            case R.id.change_view:
                String s = mSharedPreferences.getString(Constants.MAIN_RECYCLER_LAYOUT, Constants.LAYOUT_LIST);
                SharedPreferences.Editor editor = mSharedPreferences.edit();

                RecyclerView.LayoutManager layoutManager;

                if(s.equals(Constants.LAYOUT_LIST)) {
                    layoutManager = new StaggeredGridLayoutManager(mColumnsNumber, 1);
                    editor.putString(Constants.MAIN_RECYCLER_LAYOUT, Constants.LAYOUT_GRID);

                } else {
                    layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    editor.putString(Constants.MAIN_RECYCLER_LAYOUT, Constants.LAYOUT_LIST);
                }
                editor.commit();
                setChangeViewMenuItemIcon();

                sRecyclerView.setLayoutManager(layoutManager);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.delete_forever:
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage(R.string.delete_forever_massage);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAdapter.deleteSelectedTasksForever();
                        mClearBasket.setVisible(true);
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
                final AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
                alert2.setTitle(R.string.question_clear_trash);
                alert2.setMessage(R.string.question_clear_trash_additional);
                alert2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (SuperNote t: sDbHelper.getNotes(Constants.DELETED)
                                ) {
                            sDbHelper.deleteNote(t);
                        }
                        mAdapter.getNotes().clear();
                        mAdapter.notifyDataSetChanged();
                        mClearBasket.setVisible(false);
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
                if(mValues.size()>0) {
                    AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
                    alert3.setTitle(R.string.question_delete_section);
                    alert3.setMessage(R.string.delete_section_massage);
                    alert3.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mAdapter.deleteSection();
                            //deleteSection.setVisible(false);
                            onNavigationItemSelected(mMainSection);
                            sDbHelper.deleteSection(mCurrentSection);
                            System.out.println(mCurrentSection);
                            mSections.remove(mCurrentSection);
                            mCurrentSection = null;
                            onCreateOptionsMenu(mMenu);
                        }
                    });
                    alert3.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });
                    alert3.show();
                } else {
                    onNavigationItemSelected(mMainSection);
                    sDbHelper.deleteSection(mCurrentSection);
                    mSections.remove(mCurrentSection);
                    mCurrentSection = null;
                    onCreateOptionsMenu(mMenu);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveCurrentKind(){
        mEditor.putString(Constants.CURRENT_KIND, currentKind);
        mEditor.commit();
    }

    private void sectionWasChanged(){

        mEditor.putBoolean(Constants.NOTIF_ON, false);
        mEditor.commit();

        mValues = sDbHelper.getNotes(currentKind);

        isSectionEmpty();

        if(currentKind.equals(Constants.DELETED)){
            checkOldTask();
            mFab.hide();
        } else {
            mFab.show();
        }

        onCreateOptionsMenu(mMenu);
        mAdapter.dataChanged(mValues);
        saveCurrentKind();
        setTitle();
    }

    private void isSectionEmpty(){

        if(mValues.size() == 0){
            mTextNoNotes.setVisibility(View.VISIBLE);
        }
        else {
            mTextNoNotes.setVisibility(View.GONE);
        }
    }

    private void setTitle() {
        if(mSharedPreferences.getBoolean(Constants.NOTIF_ON, false)){
            mToolbar.setTitle(R.string.notifications);
            return;
        }
        switch (currentKind){
            case Constants.DELETED:
                mToolbar.setTitle(R.string.bucket);
                setItemMovement(false);
                break;
            case Constants.ARCHIVE:
                setItemMovement(false);
                mToolbar.setTitle(R.string.archive);
                break;
            case Constants.UNDEFINED:
                setItemMovement(true);
                mToolbar.setTitle("");
                break;
            default:
                setItemMovement(true);
                mToolbar.setTitle(currentKind);
                break;
        }
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        System.out.println(item.getTitle());
        // Handle navigation mChangeView item clicks here.
        hideFabs();
        int id = item.getItemId();
        for (Section s: mSections
                ) {
            if(item.getItemId() == s.getId()){
                saveNotes();
                setItemMovement(false);
                currentKind = s.getName();
                mCurrentSection = s;
                sectionWasChanged();
                super.onOptionsItemSelected(item);
                break;
            }
        }
        mNotification_on = false;

        switch (item.getItemId()){

            case Constants.KEY_GENERAL_SECTION:
                saveNotes();
                setItemMovement(true);
                currentKind = Constants.UNDEFINED;
                sectionWasChanged();
                break;
            case Constants.KEY_DELETED_SECTION:
                saveNotes();
                currentKind = Constants.DELETED;
                sectionWasChanged();
                setItemMovement(false);
                break;
            case Constants.KEY_ARCHIVE_SECTION:
                saveNotes();
                setItemMovement(true);
                mToolbar.setTitle(R.string.archive);
                currentKind = Constants.ARCHIVE;
                sectionWasChanged();
                break;
            case Constants.KEY_NOTIFICATIONS_SECTION:
                saveNotes();
                mNotification_on = true;

                onCreateOptionsMenu(mMenu);

                mEditor.putBoolean(NOTIF_ON, true);
                mEditor.commit();

                mToolbar.setTitle(R.string.notifications);
                mValues = sDbHelper.getNotificationNotes();
                isSectionEmpty();
                mAdapter.dataChanged(mValues);
                mFab.hide();
                break;
            case Constants.KEY_SETTINGS:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
        }

        if (id == R.id.add){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.add_section);
            View mView = getLayoutInflater().inflate(R.layout.dialog_add_section, null);

            final EditText input = (EditText) mView.findViewById(R.id.text_view_dialog_add_section);
            input.setBackgroundColor(0);
            alert.setView(mView);

            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = String.valueOf(input.getText());
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                    for (Section s:mSections
                         ) {
                        if(value.equals(s.getName())){
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                            Snackbar.make(sCoordinatorLayout, R.string.section_already_exist, Snackbar.LENGTH_LONG)
                                    .show();
                            return;
                        }

                    }
                    Section section = new Section();
                    section.setName(value);
                    section.setPosition(mSections.size());
                    int sectionId = sDbHelper.addSection(section);
                    section.setId(sectionId);
                    sDbHelper.updateSection(section);
                    mMenu.clear();
                    onCreateOptionsMenu(mMenu);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                }
            });

            alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
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
        long deletionPeriod = mSharedPreferences.getLong(Constants.DELETION_PERIOD, Constants.PERIOD_WEEK);
        for (SuperNote task : mValues
                ) {
            if(task.getDeletionTime() + deletionPeriod < System.currentTimeMillis()) {
                sDbHelper.deleteNote(task);
            }
        }
        mValues = sDbHelper.getNotes(Constants.DELETED);
    }

    public void setSelectionMode(){
        mActionMode = startActionMode(actionModeCallback);
        is_action_mode_on = true;
        mAdapter.setSelectionMode(MainRecyclerAdapter.Mode.SELECTION_MODE);
        mEditor.putInt(Constants.ACTION_MODE, 1);
        mEditor.commit();
        mFab.hide();
    }

    public void selectedItemCount(int selectedTasksCounter) {
        if(selectedTasksCounter == 0) {
            if(mActionMode !=null){
                mActionMode.finish();
            }
            is_action_mode_on = false;
            setItemMovement(true);
            mFab.show();
            onCreateOptionsMenu(mMenu);
            mEditor.putInt(Constants.ACTION_MODE, 0);
            mEditor.commit();
        } else {
            mActionMode.setTitle(String.valueOf(selectedTasksCounter));
            setItemMovement(false);
            if(currentKind.equals(Constants.DELETED)) {
            }
        }
    }

    public void hideFabs(){
        if(!mFabPressed)
            return;
        mFab.startAnimation(mFabCancelAnimation);
        mFabAddST.startAnimation(mFabClose);
        mFabAddLT.startAnimation(mFabClose);
        mFabAddST.setClickable(false);
        mFabAddLT.setClickable(false);
        mFabAddST.setVisibility(View.INVISIBLE);
        mFabAddLT.setVisibility(View.INVISIBLE);
        mFabPressed = false;
    }

    public void add(View v){
        if(mFabPressed){
            hideFabs();
        } else {
            mFab.startAnimation(mFabAddAnimation);
            mFabAddST.startAnimation(mFabOpen);
            mFabAddLT.startAnimation(mFabOpen);
            mFabAddST.setClickable(true);
            mFabAddLT.setClickable(true);
            mFabAddST.setVisibility(View.VISIBLE);
            mFabAddLT.setVisibility(View.VISIBLE);
            mFabPressed = true;
        }
    }

 /*   public void copyDb(View v) throws IOException {

       // File dbFile = new File ("/data/data/com/example/dmitryvedmed/databases/myDB8.db");
      //  File dbFile = new File (mContext.getFilesDir().getPath() + "myDB8.db");
      //  File dbFile = new File (mContext.getApplicationInfo().dataDir + "/databases/" + "myDB8.db");
        // File dbFile = new File (mContext.getPackageName() + "/databases/" + "myDB8.db");
        File dbFile =  (mContext.getDatabasePath("myDB8.db"));
        FileInputStream fileInputStream = new FileInputStream(dbFile);

        OutputStream myOutput = new FileOutputStream("./sdcard/myDB8.db");

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        fileInputStream.close();
    }*/

    public void newListTask(View v){
        hideFabs();
        Intent intent = new Intent(getApplicationContext(), ListNoteActivity.class);
        intent.putExtra(Constants.POSITION, mAdapter.getNotes().size());
        intent.putExtra(Constants.KIND, currentKind);
        startActivity(intent);
    }


    public void newSimpleTask(View v){
        hideFabs();
        Intent intent = new Intent(getApplicationContext(), SimpleNoteActivity.class);
        intent.putExtra(Constants.POSITION, mAdapter.getNotes().size());
        intent.putExtra(Constants.KIND, currentKind);

        intent.putExtra(Constants.KIND, currentKind);
        startActivity(intent);
    }

    private void initAnimation() {
        mFabAddAnimation = AnimationUtils.loadAnimation(this,R.anim.fab_add_rotation);
        mFabCancelAnimation = AnimationUtils.loadAnimation(this,R.anim.fab_cancel_rotation);

        mFabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
    }


    public void setItemMovement(boolean b){
        ((SimpleItemTouchHelperCallback) mCallback).setCanMovement(b);
    }

    @Override
    protected void onPause() {
        if(!mNotification_on){
            saveNotes();
        }

        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private void saveNotes() {
        if(mSharedPreferences.getBoolean(Constants.NOTIF_ON, false))
            return;

        mValues = mAdapter.getNotes();
        for (SuperNote s: mValues
                ) {
            if(!sDbHelper.isRemind(s)){
                s.setRemind(false);
            }
            sDbHelper.updateNote(s, currentKind);
        }
    }

    void update(){
        currentKind = mSharedPreferences.getString(Constants.CURRENT_KIND, Constants.UNDEFINED);
        if(!mSharedPreferences.getBoolean(NOTIF_ON, false)){
            mValues = sDbHelper.getNotes(currentKind);
        } else {
            mValues = sDbHelper.getNotificationNotes();
        }
        if(mAdapter != null)
            mAdapter.dataChanged(mValues);
    }

    private void checkDeprecated(){
        for (SuperNote s: mValues
                ) {
            if(s.isRemind() == true && s.getReminderTime()<System.currentTimeMillis() && !s.isRepeating()){
                s.setRemind(false);
                sDbHelper.updateNote(s, currentKind);
            }
        }
    }

    private void checkRepeatingNotes(){
        ArrayList<SuperNote> list = sDbHelper.getNotificationNotes();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        for (SuperNote note:list
                ) {
            if(note.isRepeating()){

                Calendar notificationTime = Calendar.getInstance();
                notificationTime.setTimeInMillis(note.getReminderTime());

                final Intent intent = new Intent(getApplicationContext(), NotifyTaskReceiver.class);
                intent.setAction(Constants.ACTION_NOTIFICATION);
                intent.putExtra(Constants.ID, note.getId());

                if (note.getRepeatingPeriod() == Constants.PERIOD_ONE_DAY) {
                    note.setRepeatingPeriod(Constants.PERIOD_ONE_DAY);
                    while (notificationTime.getTimeInMillis() < System.currentTimeMillis()) {
                        notificationTime.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    PendingIntent pi1 = PendingIntent.getBroadcast(getApplicationContext(), note.getId(), intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), Constants.PERIOD_ONE_DAY, pi1);

                } else if (note.getRepeatingPeriod() == Constants.PERIOD_WEEK) {
                    note.setRepeatingPeriod(Constants.PERIOD_WEEK);
                    while (notificationTime.getTimeInMillis() < System.currentTimeMillis()) {
                        notificationTime.add(Calendar.WEEK_OF_MONTH, 1);
                    }
                    PendingIntent pi2 = PendingIntent.getBroadcast(getApplicationContext(), note.getId(), intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), Constants.PERIOD_WEEK, pi2);

                } else if (note.getRepeatingPeriod() == Constants.PERIOD_MONTH) {
                    note.setRepeatingPeriod(Constants.PERIOD_MONTH);
                    while (notificationTime.getTimeInMillis() < System.currentTimeMillis()) {
                        notificationTime.add(Calendar.MONTH, 1);
                    }
                    PendingIntent pi3 = PendingIntent.getBroadcast(getApplicationContext(), note.getId(), intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), Constants.PERIOD_MONTH, pi3);
                }

                note.setReminderTime(notificationTime.getTimeInMillis());

                sDbHelper.updateNote(note, null);
            } else {
                Intent intent = new Intent(mContext, NotifyTaskReceiver.class);
                intent.putExtra("id", note.getId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, note.getId(), intent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, note.getReminderTime(), pendingIntent);
            }
        }
    }

    @Override
    protected void onRestart() {
        update();
        mAdapter.updateSelectedTask();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        setTitle();
        isSectionEmpty();
        onCreateNavigationMenu();
        super.onResume();
    }

    public void setColor(View v){
        switch (v.getId()){
            case R.id.green:
                mAdapter.setColorSelectionTasks(Constants.GREEN);
                mFab.show();
                break;
            case R.id.red:
                mAdapter.setColorSelectionTasks(Constants.RED);
                mFab.show();
                break;
            case R.id.blue:
                mAdapter.setColorSelectionTasks(Constants.BLUE);
                mFab.show();
                break;
            case R.id.yellow:
                mAdapter.setColorSelectionTasks(Constants.YELLOW);
                mFab.show();
                break;
            case R.id.white:
                mAdapter.setColorSelectionTasks(0);
                break;
        }
        mDialog.dismiss();
    }

    private void compareSections(){
        Comparator<Section> comparator = new Comparator<Section>() {
            @Override
            public int compare(Section section, Section t1) {
                return section.getPosition() < t1.getPosition() ? -1 : 1;
            }
        };
        Collections.sort(mSections, comparator);
    }
}
