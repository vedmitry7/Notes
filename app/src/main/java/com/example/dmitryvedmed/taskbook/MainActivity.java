package com.example.dmitryvedmed.taskbook;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity  {

    public static final String NAME_PREFERENCES = "prefs";
    SharedPreferences sharedPreferences;
    MyArrayAdapter adapter;
    DBHelper dbHelper;
    List<Task> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         values = new ArrayList<>();

     /* String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };*/

        //loadPreferences();
        dbHelper = new DBHelper(this);
        loadDateFromDB();

        adapter = new MyArrayAdapter(this, values);
        setListAdapter(adapter);
    }


    public void newTask(View v){
        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        startActivity(intent);
    }

    public void clearList(View v){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("mytable", null, null);
        values.clear();
        loadDateFromDB();
        adapter.notifyDataSetChanged();
    }

    private void loadDateFromDB() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("mytable", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("headline");
            int emailColIndex = c.getColumnIndex("content");

            do {
                values.add(new Task(c.getInt(idColIndex),
                        c.getString(nameColIndex),
                        c.getString(emailColIndex)));
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d("TAG",
                        "ID = " + c.getInt(idColIndex) +
                                ", headline = " + c.getString(nameColIndex) +
                                ", content = " + c.getString(emailColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else
            Log.d("TAG", "0 rows");
        c.close();
    }

    private void loadPreferences(){
        sharedPreferences = this.getSharedPreferences(NAME_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        values.clear();
        loadDateFromDB();
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();


        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        intent.putExtra("pos", position);
        intent.putExtra("head", item);
        intent.putExtra("text", item + item + item);
        startActivity(intent);
    }



    public class MyArrayAdapter extends ArrayAdapter<String>{
        private final Context context;
        //private final String[] values;
        List<Task> values;
        MyArrayAdapter(Context context,  List values ) {
            super(context,  R.layout.rowlayout, R.id.label, values);
            this.context = context;
            this.values = values;
           Log.d("TAG", "list size = " + values.size());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           Log.d("TAG", "getView " + position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

           // linearLayout.setBackgroundResource(R.drawable.task_shape);
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            TextView textView1 = (TextView) rowView.findViewById(R.id.label1);

           textView.setText(values.get(position).getHeadLine());
            textView1.setText(values.get(position).getContext());

        /*    textView1.setText(sharedPreferences.getString(String.valueOf(position)+"0","ХЗ заголовок"));
            textView.setText(sharedPreferences.getString(String.valueOf(position)+"1","ХЗ текст"));*/



            return rowView;
        }
    }
}
