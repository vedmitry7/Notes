package com.example.dmitryvedmed.taskbook;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dmitryvedmed.taskbook.logic.DBHelper;
import com.example.dmitryvedmed.taskbook.logic.ListNote;
import com.example.dmitryvedmed.taskbook.logic.SimpleNote;
import com.example.dmitryvedmed.taskbook.logic.SuperNote;
import com.example.dmitryvedmed.taskbook.ui.ListNoteDialogActivity;
import com.example.dmitryvedmed.taskbook.ui.SimpleNoteDialogActivity;
import com.example.dmitryvedmed.taskbook.untils.Constants;

public class NotifyTaskReceiver extends BroadcastReceiver {
    public NotifyTaskReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Log.d("TAG", "NOTIFYYYYYYYYYYY");

        if(intent.getBooleanExtra(Constants.REPEATING, false))
            Log.d("TAG", "REPEATING DAAAAAA");
        int period2 = intent.getIntExtra(Constants.PERIOD, 0);

        int id = intent.getIntExtra(Constants.ID, -2);
        Log.d("TAG", "ID = " + id);
        if(id==-2) {
            return;
        }
        DBHelper dbHelper = new DBHelper(context);
        SuperNote superNote = dbHelper.getTask(id);
        if(superNote == null)
            return;

        Log.d("TAG", "ST != null" );

        Intent notificationIntent;

        if(superNote instanceof SimpleNote){
            SimpleNote task = (SimpleNote) superNote;
            if(!task.isRepeating())
                task.setRemind(false);
            else {
                task.setReminderTime(task.getReminderTime() + task.getRepeatingPeriod());
            }

            notificationIntent = new Intent(context, SimpleNoteDialogActivity.class);
            notificationIntent.putExtra(Constants.ID, task.getId());
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            Resources res = context.getResources();

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_menu_black_24dp)
                    // большая картинка
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.super_ic))
                    .setTicker(String.valueOf(task.getId()))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(false)
                    .addAction(R.drawable.ic_menu_black_24dp, context.getString(R.string.act_open), contentIntent)
                    .setContentTitle(task.getHeadLine())
                    .setContentText(task.getContext()); // Текст уведомления

            // Notification notification = builder.getNotification(); // до API 16

            Notification notification = builder.build();


            Uri ringURI =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.sound = ringURI;

            notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
            dbHelper.updateTask(task, null);
        }
        else{
            notificationIntent = new Intent(context, ListNoteDialogActivity.class);
            ListNote task = (ListNote) superNote;
            if(!task.isRepeating())
                task.setRemind(false);
            else {
                task.setReminderTime(task.getReminderTime() + task.getRepeatingPeriod());
            }
            notificationIntent.putExtra(Constants.ID, task.getId());
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
                    .setSmallIcon(R.drawable.ic_format_list_checks_black_24dp)
                    // большая картинка
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.super_ic))
                    .setTicker(String.valueOf(task.getId()))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(false)
                    .addAction(R.drawable.ic_format_list_checks_black_24dp, "Открыть", contentIntent)
                    .setContentTitle(task.getHeadLine())
                    .setContentText("324");
            // Notification notification = builder.getNotification(); // до API 16

            Notification notification = builder.build();

            Uri ringURI =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.sound = ringURI;

            notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
            dbHelper.updateTask(task, null);
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void notifyq(){

    }
}
