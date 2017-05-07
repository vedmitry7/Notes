package com.example.dmitryvedmed.taskbook;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.ListTask;
import com.example.dmitryvedmed.taskbook.logic.SimpleTask;
import com.example.dmitryvedmed.taskbook.logic.SuperTask;
import com.example.dmitryvedmed.taskbook.ui.ListTaskDialogActivity;
import com.example.dmitryvedmed.taskbook.ui.SimpleTaskDialogActivity;

public class NotifyTaskReceiver extends BroadcastReceiver {
    public NotifyTaskReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Log.d("TAG", "NOTIFYYYYYYYYYYY");

        if(intent.getBooleanExtra("repeating", false))
            Log.d("TAG", "REPEATING DAAAAAA");
        int period2 = intent.getIntExtra("period", 0);
        Log.d("TAG", "REPIOD " + period2);

        int id = intent.getIntExtra("id",-2);
        Log.d("TAG", "ID = " + id);
        if(id==-2) {
            return;
        }
        DBHelper5 dbHelper5 = new DBHelper5(context);
        SuperTask superTask = dbHelper5.getTask(id);
        if(superTask==null)
            return;
        Log.d("TAG", "ST != null" );
        Intent notificationIntent;

        if(superTask instanceof SimpleTask){
            SimpleTask task = (SimpleTask) superTask;
            if(!task.isRepeating())
            task.setRemind(false);
            else {
                task.setReminderTime(task.getReminderTime() + task.getRepeatingPeriod());
            }
            notificationIntent = new Intent(context, SimpleTaskDialogActivity.class);
            notificationIntent.putExtra("TaskId", task.getId());
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Resources res = context.getResources();

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.note_multiple_outline)
                    // большая картинка
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.note_multiple_outline))
                    .setTicker(String.valueOf(task.getId()))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(false)
                    .addAction(R.drawable.note_multiple_outline, "Открыть", contentIntent)
                    .setContentTitle(task.getHeadLine())
                    .setContentText(task.getContext()); // Текст уведомления

            // Notification notification = builder.getNotification(); // до API 16

            Notification notification = builder.build();

            notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
            dbHelper5.updateTask(task, null);
        }
        else{
            notificationIntent = new Intent(context, ListTaskDialogActivity.class);
            ListTask task = (ListTask) superTask;
            if(!task.isRepeating())
                task.setRemind(false);
            else {
                task.setReminderTime(task.getReminderTime() + task.getRepeatingPeriod());
            }
            notificationIntent.putExtra("ListTaskId", task.getId());
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

          //  notificationIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
          //  notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Resources res = context.getResources();

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.note_multiple_outline)
                    // большая картинка
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.note_multiple_outline))
                    .setTicker(String.valueOf(task.getId()))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(false)
                    .addAction(R.drawable.note_multiple_outline, "Открыть", contentIntent)
                    .setContentTitle(task.getHeadLine())
                    .setContentText(task.getUncheckedTask(0));
            // Notification notification = builder.getNotification(); // до API 16

            Notification notification = builder.build();

            notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
            dbHelper5.updateTask(task, null);
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void notifyq(){

    }
}
