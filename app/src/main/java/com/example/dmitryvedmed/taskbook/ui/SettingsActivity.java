package com.example.dmitryvedmed.taskbook.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.json.SuperNoteDeserializer;
import com.example.dmitryvedmed.taskbook.json.SuperNoteSerializer;
import com.example.dmitryvedmed.taskbook.logic.DBHelper;
import com.example.dmitryvedmed.taskbook.logic.ListNote;
import com.example.dmitryvedmed.taskbook.logic.SimpleNote;
import com.example.dmitryvedmed.taskbook.logic.SuperNote;
import com.example.dmitryvedmed.taskbook.untils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;


public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final int EX_FILE_PICKER_RESULT_UPLOAD = 8;
    private static final int EX_FILE_PICKER_RESULT_DOWNLOAD = 16;

    SharedPreferences mSharedPreferences;
    TextView mNoteFontLabel;
    TextView mCardFontLabel;
    TextView mNoteFontValue;
    TextView mCardFontValue;
    TextView mDeletionPeriodLabel;
    TextView mSwipeAction;
    TextView mMorningTime;
    TextView mAfternoonTime;
    TextView mEveningTime;
    SeekBar mNoteFontSeekBar;
    SeekBar mCardFontSeekBar;
    SharedPreferences.Editor mEditor;
    RelativeLayout mDeletionPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSharedPreferences = getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);

        int color = ContextCompat.getColor(this, R.color.common_google_signin_btn_text_dark);
        toolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);


        mCardFontLabel = (TextView) findViewById(R.id.labelCardFont);
        mNoteFontLabel = (TextView) findViewById(R.id.labelNoteFont);

        mCardFontValue = (TextView) findViewById(R.id.textCardFontValue);
        mNoteFontValue = (TextView) findViewById(R.id.textNoteFontValue);

        setTime();

        mNoteFontSeekBar = (SeekBar) findViewById(R.id.seekBarTaskFont);
        mNoteFontSeekBar.setOnSeekBarChangeListener(this);
        mCardFontSeekBar = (SeekBar) findViewById(R.id.seekBarCardFont);
        mCardFontSeekBar.setOnSeekBarChangeListener(this);

        mDeletionPeriodLabel = (TextView) findViewById(R.id.deletionPeriodValue);
        Long deletionPeriod = mSharedPreferences.getLong(Constants.DELETION_PERIOD,Constants.PERIOD_WEEK);

        if (deletionPeriod.equals(Constants.PERIOD_AT_ONCE)) {
            mDeletionPeriodLabel.setText(R.string.at_once);

        } else if (deletionPeriod.equals(Constants.PERIOD_ONE_DAY)) {
            mDeletionPeriodLabel.setText(R.string.one_day);

        } else if (deletionPeriod.equals(Constants.PERIOD_TREE_DAY)) {
            mDeletionPeriodLabel.setText(R.string.tree_day);

        } else if (deletionPeriod.equals(Constants.PERIOD_WEEK)) {
            mDeletionPeriodLabel.setText(R.string.seven_day);

        } else if (deletionPeriod.equals(Constants.PERIOD_MONTH)) {
            mDeletionPeriodLabel.setText(R.string.month);

        }

        mSwipeAction = (TextView) findViewById(R.id.swipeAction);
        String remember = mSharedPreferences.getString(Constants.SWIPE_REMEMBER,"");
        switch (remember){
            case Constants.ARCHIVE:
                mSwipeAction.setText(R.string.act_archive);
                break;
            case Constants.DELETED:
                mSwipeAction.setText(R.string.act_delete);
                break;
            case "":
                mSwipeAction.setText(R.string.act_ask);
                break;
        }

        mDeletionPeriod = (RelativeLayout) findViewById(R.id.setDeletionPeriod);

        mNoteFontSeekBar.setProgress(mSharedPreferences.getInt(Constants.TASK_FONT_SIZE, 16) - 12);
        mCardFontSeekBar.setProgress(mSharedPreferences.getInt(Constants.CARD_FONT_SIZE, 17) - 12);
    }

    private void setTime() {
        mMorningTime = (TextView) findViewById(R.id.morning_text);
        mAfternoonTime = (TextView) findViewById(R.id.afternoon_text);
        mEveningTime = (TextView) findViewById(R.id.evening_text);

        String mHours = String.valueOf(mSharedPreferences.getInt(Constants.MORNING_TIME_HOURS, 7));
        String aHours = String.valueOf(mSharedPreferences.getInt(Constants.AFTERNOON_TIME_HOURS, 13));
        String eHours = String.valueOf(mSharedPreferences.getInt(Constants.EVENING_TIME_HOURS, 19));

        String mMinutes = String.valueOf(mSharedPreferences.getInt(Constants.MORNING_TIME_MINUTES, 0));
        if(mMinutes.length()==1)
            mMinutes = "0" + mMinutes;
        String aMinutes = String.valueOf(mSharedPreferences.getInt(Constants.AFTERNOON_TIME_MINUTES, 0));
        if(aMinutes.length()==1)
            aMinutes = "0" + aMinutes;
        String eMinutes = String.valueOf(mSharedPreferences.getInt(Constants.EVENING_TIME_MINUTES, 0));
        if(eMinutes.length()==1)
            eMinutes = "0" + eMinutes;

        Calendar time = Calendar.getInstance();
        String formattedTime;
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this);

        time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mHours));
        time.set(Calendar.MINUTE, Integer.parseInt(mMinutes));

        formattedTime = timeFormat.format(time.getTimeInMillis());
        mMorningTime.setText(formattedTime);

        time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aHours));
        time.set(Calendar.MINUTE, Integer.parseInt(aMinutes));

        formattedTime = timeFormat.format(time.getTimeInMillis());
        mAfternoonTime.setText(formattedTime);

        time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(eHours));
        time.set(Calendar.MINUTE, Integer.parseInt(eMinutes));

        formattedTime = timeFormat.format(time.getTimeInMillis());
        mEveningTime.setText(formattedTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setDeletionPeriod:
                mEditor = mSharedPreferences.edit();
                PopupMenu popupMenu = new PopupMenu(this, mDeletionPeriod);
                popupMenu.inflate(R.menu.deletion_period);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.at_once:
                                mDeletionPeriodLabel.setText(R.string.at_once);
                                mEditor.putLong(Constants.DELETION_PERIOD, Constants.PERIOD_AT_ONCE);
                                break;
                            case R.id.one_day:
                                mDeletionPeriodLabel.setText(R.string.one_day);
                                mEditor.putLong(Constants.DELETION_PERIOD, Constants.PERIOD_ONE_DAY);
                                break;
                            case R.id.tree_days:
                                mDeletionPeriodLabel.setText(R.string.tree_day);
                                mEditor.putLong(Constants.DELETION_PERIOD, Constants.PERIOD_TREE_DAY);
                                break;
                            case R.id.seven_days:
                                mDeletionPeriodLabel.setText(R.string.seven_day);
                                mEditor.putLong(Constants.DELETION_PERIOD, Constants.PERIOD_WEEK);
                                break;
                            case R.id.month:
                                mDeletionPeriodLabel.setText(R.string.month);
                                mEditor.putLong(Constants.DELETION_PERIOD, Constants.PERIOD_MONTH);
                                break;
                        }
                        mEditor.commit();
                        return false;
                    }
                });
                popupMenu.show();
                break;
            case R.id.setSectionPosition:
                Intent intent = new Intent(this, SectionLocationActivity.class);
                this.startActivity(intent);
                return;
            case R.id.setSwipeAction:

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, new String[]{getResources().getString(R.string.act_delete),
                        getResources().getString(R.string.act_archive), getResources().getString(R.string.act_ask)});
                mBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        switch (i){
                            case 0:
                                editor.putString(Constants.SWIPE_REMEMBER, Constants.DELETED);
                                editor.commit();
                                mSwipeAction.setText(R.string.act_delete);
                                break;
                            case 1:
                                editor.putString(Constants.SWIPE_REMEMBER, Constants.ARCHIVE);
                                editor.commit();
                                mSwipeAction.setText(R.string.act_archive);
                                break;
                            case 2:
                                editor.putString(Constants.SWIPE_REMEMBER, "");
                                editor.commit();
                                mSwipeAction.setText(R.string.act_ask);
                                break;
                        }
                    }
                });
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                break;
            case R.id.setAfternoonTime:
                final int hour = mSharedPreferences.getInt(Constants.AFTERNOON_TIME_HOURS,13);
                final int minute = mSharedPreferences.getInt(Constants.AFTERNOON_TIME_MINUTES,0);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                        mEditor.putInt(Constants.AFTERNOON_TIME_MINUTES, m);
                        mEditor.putInt(Constants.AFTERNOON_TIME_HOURS, h);
                        mEditor.commit();
                        setTime();
                    }
                }, hour, minute, true);
                timePickerDialog.show();
                break;
            case R.id.setMorningTime:
                final int hour2 = mSharedPreferences.getInt(Constants.MORNING_TIME_HOURS,7);
                final int minute2 = mSharedPreferences.getInt(Constants.MORNING_TIME_MINUTES,0);

                TimePickerDialog timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                        mEditor.putInt(Constants.MORNING_TIME_MINUTES, m);
                        mEditor.putInt(Constants.MORNING_TIME_HOURS, h);
                        mEditor.commit();
                        setTime();
                    }
                }, hour2, minute2, true);
                timePickerDialog2.show();
                break;
            case R.id.setEveningTime:
                final int hour3 = mSharedPreferences.getInt(Constants.EVENING_TIME_HOURS,19);
                final int minute3 = mSharedPreferences.getInt(Constants.EVENING_TIME_MINUTES,0);

                TimePickerDialog timePickerDialog3 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                        mEditor.putInt(Constants.EVENING_TIME_MINUTES, m);
                        mEditor.putInt(Constants.EVENING_TIME_HOURS, h);
                        mEditor.commit();
                        setTime();
                    }
                }, hour3, minute3, true);
                timePickerDialog3.show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mEditor = mSharedPreferences.edit();
        switch (seekBar.getId()) {
            case R.id.seekBarTaskFont:
                mNoteFontLabel.setTextSize(i + 12);
                mNoteFontValue.setText(String.valueOf(i + 12));
                if (b) {
                    mEditor.putInt(Constants.TASK_FONT_SIZE, i + 12);
                }
                break;
            case R.id.seekBarCardFont:
                mCardFontLabel.setTextSize(i + 12);
                if (b) {
                    mEditor.putInt(Constants.CARD_FONT_SIZE, i + 12);
                }
                mCardFontValue.setText(String.valueOf(i + 12));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mEditor.commit();
    }


    public void makeCopy(View view) {
        ExFilePicker exFilePicker = new ExFilePicker();
        exFilePicker.setCanChooseOnlyOneItem(true);
        //exFilePicker.setShowOnlyExtensions("apk");
        exFilePicker.setChoiceType(ExFilePicker.ChoiceType.DIRECTORIES);
        exFilePicker.start(this, EX_FILE_PICKER_RESULT_UPLOAD);

    }

    public void downloadCopy(View view) {

        ExFilePicker exFilePicker = new ExFilePicker();
        exFilePicker.setCanChooseOnlyOneItem(true);
        exFilePicker.setShowOnlyExtensions("json");
        exFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
        exFilePicker.start(this, EX_FILE_PICKER_RESULT_DOWNLOAD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EX_FILE_PICKER_RESULT_UPLOAD) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                // Here is object contains selected files names and path

                System.out.println(result.getPath()+result.getNames().get(0));

                DBHelper dbHelper = new DBHelper(this);

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder
                        .registerTypeAdapter(SimpleNote.class, new SuperNoteSerializer())
                        .registerTypeAdapter(ListNote.class, new SuperNoteSerializer())
                        .setPrettyPrinting()
                        .create();

                ArrayList<SuperNote> notes =  dbHelper.getAllNote();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("[");
                for (SuperNote note:notes
                     ) {
                    stringBuilder.append(gson.toJson(note));
                    stringBuilder.append(",\n");
                }
                stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
                stringBuilder.append("]");


                System.out.println(stringBuilder.toString());

                write(result.getPath()+result.getNames().get(0)  + "/notes.json", stringBuilder.toString());

            }
        }

        if (requestCode == EX_FILE_PICKER_RESULT_DOWNLOAD) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                // Here is object contains selected files names and path

                String filePath = result.getPath() + result.getNames().get(0);
                System.out.println(filePath);
                String notesContent = null;
                try {
                    notesContent = read(filePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                System.out.println(notesContent);

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder
                        .registerTypeAdapter(SuperNote.class, new SuperNoteDeserializer())
                        .setPrettyPrinting()
                        .create();

                JsonArray jsonArray = gson.fromJson(notesContent, JsonArray.class);


                for (JsonElement e:jsonArray
                        ) {

                    SuperNote superNote = gson.fromJson(e, SuperNote.class);
                    System.out.println("&&&SN " + (superNote instanceof SimpleNote));

                    if (superNote instanceof SimpleNote) {
                        System.out.println(((SimpleNote) superNote).getHeadLine());
                        System.out.println(((SimpleNote) superNote).getContent());
                        System.out.println(superNote.getSection());
                    } else {
                        System.out.println("&&&LN " + (superNote instanceof ListNote));
                        System.out.println(((ListNote) superNote).getUncheckedItems().get(0));
                        System.out.println(((ListNote) superNote).getUncheckedItems().get(1));
                        System.out.println(((ListNote) superNote).getCheckedItems().get(0));
                    }

                }


            }
        }
    }

    public static void write(String fileName, String text) {
        //Определяем файл
        System.out.println("WRITE");
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                file.createNewFile();
                System.out.println("CREATE");
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем текст у файл
                out.print(text);
                System.out.println("PRINT");

            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String read(String fileName) throws FileNotFoundException {
        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();

        try {
            //Объект для чтения файла в буфер
            BufferedReader in = new BufferedReader(new FileReader( fileName));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                //Также не забываем закрыть файл
                in.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        //Возвращаем полученный текст с файла
        return sb.toString();
    }
}
