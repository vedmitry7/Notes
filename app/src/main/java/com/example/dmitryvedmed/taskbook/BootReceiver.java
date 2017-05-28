package com.example.dmitryvedmed.taskbook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.SuperTask;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    Context context;
    Intent intent;

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        this.intent = intent;

        Toast.makeText(context, "УРРРРРРАААААААА", Toast.LENGTH_LONG).show();
        DBHelper5 dbHelper5 = new DBHelper5(context);
        Log.d("TAG", "BOOT RECEIVER. WORK");

        List<SuperTask> tasks = dbHelper5.getNotificationTasks();

        for (SuperTask task:tasks
                ) {
            Log.d("TAG", "BOOT RECEIVER. " + task.getId() + " repeat " + task.isRepeating());

            if(task.getReminderTime() < System.currentTimeMillis()) {
                Log.d("TAG", task.getId() + " is OLD");
                if(task.isRepeating()) {
                    for (int i = 0; i < 10; i++) {
                        if (System.currentTimeMillis() < task.getReminderTime()) {
                            task.setReminderTime(task.getReminderTime() + task.getRepeatingPeriod());
                        }
                    }
                    if (System.currentTimeMillis() < task.getReminderTime())
                        createRepeatingNotification(task);
                }
                else {
                    task.setRemind(false);
                    dbHelper5.updateTask(task, null);
                }
            } else {
                Log.d("TAG", task.getId() + " NOT OLD");
                if(task.isRepeating()) {
                    createRepeatingNotification(task);
                } else  {
                    createSingleNotification(task);
                }

            }
        }
    }

    private void createSingleNotification(SuperTask task){
        Log.d("TAG", task.getId() + " SIngle not");

        Intent i = new Intent(context, NotifyTaskReceiver.class);
        intent.setAction("TASK_NOTIFICATION");
        intent.putExtra("id", task.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, task.getReminderTime(), pendingIntent);
    }

    private void createRepeatingNotification(SuperTask task){
        Log.d("TAG", task.getId() + " rep not not");
        Intent i = new Intent(context, NotifyTaskReceiver.class);
        intent.setAction("TASK_NOTIFICATION");
        intent.putExtra("id", task.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, task.getReminderTime(), task.getRepeatingPeriod(), pendingIntent);
    }
}
