package com.example.dmitryvedmed.taskbook;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

public class WidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private List<SuperTask> tasks;
    private int mAppWidgetId;
    private DBHelper5 dbHelper5;


    public WidgetViewFactory(Context context, Intent intent) {
        Log.d("TAG", "       WidgetViewFactory --- WidgetViewFactory");

        this.context = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        Log.d("TAG", "       WidgetViewFactory --- onCreate()");

        dbHelper5 = new DBHelper5(context);
        tasks = dbHelper5.getTasks(Constants.UNDEFINED);

    }
    @Override
    public RemoteViews getViewAt(int position) {
        Log.d("TAG", "       WidgetViewFactory --- getViewAt ");

        RemoteViews rv = null;

/*
        SimpleTask task = (SimpleTask) tasks.get(position);


        rv = new RemoteViews(context.getPackageName(), R.layout.row_layout_simple_task);
        rv.setTextViewText(R.id.rlstHeadLine, task.getHeadLine());
        rv.setTextViewText(R.id.rlstText, task.getContext());
*/

       if(tasks.get(position) instanceof SimpleTask){

            SimpleTask task = (SimpleTask) tasks.get(position);

            rv = new RemoteViews(context.getPackageName(), R.layout.row_layout_simple_task);
            rv.setTextViewText(R.id.rlstHeadLine, (task.getHeadLine()));
            rv.setTextViewText(R.id.rlstText, task.getContext());
            //rv.setBoolean(R.id.rlstText, "visibility", false);
        }  else

        if(tasks.get(position) instanceof ListTask){

            ListTask task = (ListTask) tasks.get(position);

            rv = new RemoteViews(context.getPackageName(), R.layout.row_layout_list_task);

            rv.setTextViewText(R.id.rlltHeadLine, task.getHeadLine());

            for (String s:task.getUncheckedTasks()
                 ) {
                RemoteViews innerRemoteView = new RemoteViews(context.getPackageName(),R.layout.row_layout_list_item);
                innerRemoteView.setTextViewText(R.id.textView33, s);
                rv.addView(R.id.container, innerRemoteView);
            }
            for (String s:task.getCheckedTasks()
                    ) {
                RemoteViews innerRemoteView = new RemoteViews(context.getPackageName(),R.layout.row_layout_list_item);
                innerRemoteView.setTextViewText(R.id.textView33, s);
                rv.addView(R.id.container, innerRemoteView);
            }
        }
        //if(tasks.get(position).getColor()!=0)

        return rv;
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return tasks.size();
    }



    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}