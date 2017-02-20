package com.example.dmitryvedmed.taskbook;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };

        MyArrayAdapter adapter = new MyArrayAdapter(this, values);
        setListAdapter(adapter);
        //setContentView(R.layout.activity_main);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        intent.putExtra("head", item);
        intent.putExtra("text", item + item + item);
        startActivity(intent);
    }



    public class MyArrayAdapter extends ArrayAdapter<String>{
        private final Context context;
        private final String[] values;

        public MyArrayAdapter(Context context, String[] values) {
            super(context,  R.layout.rowlayout, R.id.label, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.label);
            TextView textView1 = (TextView) rowView.findViewById(R.id.label1);
            textView1.setText(values[position]);
            textView.setText(values[position]+ " " + values[position] + " " + values[position]);
            // Изменение иконки для Windows и iPhone
            String s = values[position];

            return rowView;
        }
    }
}
