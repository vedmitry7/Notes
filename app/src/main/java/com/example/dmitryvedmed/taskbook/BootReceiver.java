package com.example.dmitryvedmed.taskbook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.SuperTask;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "УРРРРРРАААААААА", Toast.LENGTH_LONG).show();
        DBHelper5 dbHelper5 = new DBHelper5(context);

        List<SuperTask> tasks = dbHelper5.getNotificationTasks();

        for (SuperTask task:tasks
             ) {

            if(task.getReminderTime()<System.currentTimeMillis()){
                return;
            }

            Intent i = new Intent(context, NotifyTaskReceiver.class);
            intent.setAction("TASK_NOTIFICATION");
            intent.putExtra("id", task.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId(), intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, task.getReminderTime(), pendingIntent);
        }
    }
}
