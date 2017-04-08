package com.example.dmitryvedmed.taskbook;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class TaskActivity extends AppCompatActivity {

    private int id;
    private EditText head,text;
    private SimpleTask task;
    private String currentKind;
    Color color;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_task);

        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.st_toolbar);
        toolbar.setTitle("");
       setSupportActionBar(toolbar);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.taskColorGreen)));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        head = ( EditText) findViewById(R.id.headEditText);
        text = ( EditText) findViewById(R.id.taskEditText);

        Typeface typeFace = Typeface.createFromAsset(getAssets(), "font/Roboto-Regular.ttf");
        Typeface boldTypeFace = Typeface.createFromAsset(getAssets(), "font/Roboto-Bold.ttf");

        text.setTypeface(typeFace);
        head.setTypeface(boldTypeFace);
       // Intent intent = getIntent();
       // id = intent.getIntExtra("id", -1);
       // Log.d("TAG", String.valueOf(id));

        task = (SimpleTask) getIntent().getSerializableExtra("Task");
        if(task == null){
            task = new SimpleTask();
            task.setId(-1);
            task.setPosition(getIntent().getIntExtra("position", 0));
            Log.d("TAG", "TAAAAAAASKA  NEEEET");
            Log.d("TAG", "ID = " + task.getId());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            head.requestFocus();
        }
        else {
            Log.d("TAG", "TAAAAAAASK EST'!!!!!!!!");
            Log.d("TAG", "ID = " + task.getId());
        }

        currentKind = getIntent().getStringExtra("kind");
        if(currentKind==null)
            currentKind = Constants.UNDEFINED;

        // Log.d("TAG", "Eah, I've get task - " + String.valueOf(id));

      /*  if(id==-1){
            task = new Task();
            task.setId(id);
            return;
        }*/
       // task = dbHelper.getTask(id);
        head.setText(task.getHeadLine());
        text.setText(task.getContext());

        head.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if( keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                    Log.d("TAG", "                                  ENTER!");
                    text.requestFocus();
                    return true;
                }
                return false;
            }
        });

        head.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
                        text.requestFocus();
                }
                return false;
            }
        });

       /* text.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if( keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL
                        && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                    Log.d("TAG", "                                  DEL!" + text.getText());

                    if(text.getText().toString().equals("")){
                        head.requestFocus();
                    }
                    return true;
                }
                return true;
            }
        });*/
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DEL && event.getAction()==KeyEvent.ACTION_DOWN){
                    if(text.getText().toString().equals(""))
                        head.requestFocus();
                }
                return false;
            }
        });

    /*    text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "EDITTWXT                  CLICK");
                text.setSelection(text.getText().length());
            }
        });*/
        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Always use a TextKeyListener when clearing a TextView to prevent android
                    // warnings in the log
                    Log.d("TAG", "EDITTWXT                  HAS FOKUS");
                    text.setSelection(text.getText().length());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveTask() {
        String headline = String.valueOf(head.getText());
        String content = String.valueOf(text.getText());


        Log.d("TAG", "SAVE  !!! " +  headline + " " + content);

        Log.d("TAG", "SAVE id = " + id + "???");

        task.setHeadLine(headline);
        task.setContext(content);
        task.setColor(Color.BLUE);

        DBHelper5 dbHelper = new DBHelper5(this);
        if(task.getId()==-1) {
            if(head.getText().length()==0&&text.getText().length()==0)
                return;
            id = dbHelper.addTask(task);
            task.setId(id);
            Log.d("TAG", "NEW TASK ID = " + id);
        }
        else{
            Log.d("TAG", "UPDATE id = " + id);
            dbHelper.updateTask(task, currentKind);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_colors, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        saveTask();
        super.onPause();
    }
}