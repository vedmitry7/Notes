package com.vedmitryapps.notes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vedmitryapps.notes.logic.DBHelper;
import com.vedmitryapps.notes.logic.SuperNote;
import com.vedmitryapps.notes.untils.Constants;

import java.util.ArrayList;
import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    Context context;
    Intent intent;

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent i) {

        this.context = context;
        this.intent = i;

        DBHelper sDbHelper = new DBHelper(context);
        ArrayList<SuperNote> list = sDbHelper.getNotificationNotes();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (SuperNote note:list
                ) {
            if(note.isRepeating()){

                Calendar notificationTime = Calendar.getInstance();
                notificationTime.setTimeInMillis(note.getReminderTime());

                final Intent intent = new Intent(context, NotifyTaskReceiver.class);
                intent.setAction(Constants.ACTION_NOTIFICATION);
                intent.putExtra(Constants.ID, note.getId());

                if (note.getRepeatingPeriod() == Constants.PERIOD_ONE_DAY) {
                    note.setRepeatingPeriod(Constants.PERIOD_ONE_DAY);
                    while (notificationTime.getTimeInMillis() < System.currentTimeMillis()) {
                        notificationTime.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    PendingIntent pi1 = PendingIntent.getBroadcast(context, note.getId(), intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), Constants.PERIOD_ONE_DAY, pi1);

                } else if (note.getRepeatingPeriod() == Constants.PERIOD_WEEK) {
                    note.setRepeatingPeriod(Constants.PERIOD_WEEK);
                    while (notificationTime.getTimeInMillis() < System.currentTimeMillis()) {
                        notificationTime.add(Calendar.WEEK_OF_MONTH, 1);
                    }
                    PendingIntent pi2 = PendingIntent.getBroadcast(context, note.getId(), intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), Constants.PERIOD_WEEK, pi2);

                } else if (note.getRepeatingPeriod() == Constants.PERIOD_MONTH) {
                    note.setRepeatingPeriod(Constants.PERIOD_MONTH);
                    while (notificationTime.getTimeInMillis() < System.currentTimeMillis()) {
                        notificationTime.add(Calendar.MONTH, 1);
                    }
                    PendingIntent pi3 = PendingIntent.getBroadcast(context, note.getId(), intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), Constants.PERIOD_MONTH, pi3);
                }

                note.setReminderTime(notificationTime.getTimeInMillis());

                sDbHelper.updateNote(note, null);
            } else {
                Intent intent = new Intent(context, NotifyTaskReceiver.class);
                intent.putExtra("id", note.getId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, note.getId(), intent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, note.getReminderTime(), pendingIntent);
            }
        }
    }
}
