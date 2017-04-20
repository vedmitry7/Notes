package com.example.dmitryvedmed.taskbook.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("TAG", "       WidgetService --- onGetViewFactory");

        return(new WidgetViewFactory(this.getApplicationContext(),
                intent));
    }
}
