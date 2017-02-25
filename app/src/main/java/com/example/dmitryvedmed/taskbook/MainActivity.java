package com.example.dmitryvedmed.taskbook;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends ListActivity  {

    public static final String NAME_PREFERENCES = "prefs";
    MyArrayAdapter adapter;
    public static DBHelper dbHelper;
    List<Task> values;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        values = dbHelper.getAllTask();

        adapter = new MyArrayAdapter(this, values);

        setListAdapter(adapter);
    }

    public void newTask(View v){
        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        startActivity(intent);
    }

    public void clearList(View v){
       dbHelper.clearDB();
        updateList();
    }

    @Override
    protected void onResume() {
        Log.d("TAG", "onResume");
        updateList();
        super.onResume();
    }

    private void updateList(){
        values = dbHelper.getAllTask();
        adapter.refresh(values);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        intent.putExtra("id", values.get(position).getId());
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

        public void refresh(List values){
            this.clear();
            this.addAll(values);
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
