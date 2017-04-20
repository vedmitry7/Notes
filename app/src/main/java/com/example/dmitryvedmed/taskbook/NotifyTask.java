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

public class NotifyTask extends BroadcastReceiver {
    public NotifyTask() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Log.d("TAG", "NOTIFYYYYYYYYYYY");

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
        SimpleTask task = (SimpleTask) superTask;
        Intent notificationIntent;

        if(superTask instanceof SimpleTask)
            notificationIntent = new Intent(context, SimpleTaskActivity.class);
        else
            notificationIntent = new Intent(context, ListTaskActivity.class);


        notificationIntent.putExtra("Task", task);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                .setAutoCancel(true)
                .addAction(R.drawable.note_multiple_outline, "Открыть", contentIntent)
                .setContentTitle(task.getHeadLine())
                .setContentText(task.getContext()); // Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16

        Notification notification = builder.build();

        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
