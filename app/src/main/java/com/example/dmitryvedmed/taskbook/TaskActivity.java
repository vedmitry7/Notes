package com.example.dmitryvedmed.taskbook;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.dmitryvedmed.taskbook.Main3Activity.dbHelper;

public class TaskActivity extends AppCompatActivity {

    private int id;
    private EditText head,text;
    private SimpleTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_task);

        initView();
    }

    private void initView() {
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
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            head.requestFocus();
        }
        else
            Log.d("TAG", "TAAAAAAASK EST'!!!!!!!!");

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

    private void saveTask() {
        String headline = String.valueOf(head.getText());
        String content = String.valueOf(text.getText());


        Log.d("TAG", "SAVE  !!! " +  headline + " " + content);

        Log.d("TAG", "SAVE id = " + id + "???");

        task.setHeadLine(headline);
        task.setContext(content);

        if(task.getId()==-1) {
            id = dbHelper.addTask(task);
            task.setId(id);
            Log.d("TAG", "NEW TASK ID = " + id);
        }
        else{
            Log.d("TAG", "UPDATE id = " + id);
            dbHelper.updateTask(task, Constants.UNDEFINED);
        }
    }

    @Override
    protected void onPause() {
        saveTask();
        super.onPause();
    }
}