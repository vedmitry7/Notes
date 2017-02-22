package com.example.dmitryvedmed.taskbook;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends ListActivity  {

    public static final String NAME_PREFERENCES = "prefs";
    SharedPreferences sharedPreferences;
    ArrayList<String> tasks;
    MyArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };

        loadPreferences();
        adapter = new MyArrayAdapter(this, values);
        adapter.setNotifyOnChange(true);
        setListAdapter(adapter);
        //setContentView(R.layout.activity_main);
    }

    private void loadPreferences(){
        sharedPreferences = this.getSharedPreferences(NAME_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
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
        private final String[] values;

        MyArrayAdapter(Context context, String[] values) {
            super(context,  R.layout.rowlayout, R.id.label, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

           // linearLayout.setBackgroundResource(R.drawable.task_shape);
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            TextView textView1 = (TextView) rowView.findViewById(R.id.label1);


            textView1.setText(sharedPreferences.getString(String.valueOf(position)+"0","ХЗ заголовок"));
            textView.setText(sharedPreferences.getString(String.valueOf(position)+"1","ХЗ текст"));
            // Изменение иконки для Windows и iPhone
            String s = values[position];
            System.out.println("LOAD ITEM");
            System.out.println("pos = " + position);
            System.out.println(sharedPreferences.getString(String.valueOf(position)+"0"," ---0"));
            System.out.println(sharedPreferences.getString(String.valueOf(position)+"1"," ---1"));

            return rowView;
        }
    }
}
