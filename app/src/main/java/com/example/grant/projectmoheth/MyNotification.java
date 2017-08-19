package com.example.grant.projectmoheth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

public class MyNotification {
    private NotificationCompat.Builder builder;
    private final int UNIQUE_ID = 123;
    private final int SNOOZE_REQUEST_CODE = 0;
    private final int CHECK_REQUEST_CODE = 1;

    /* TODO: use the titles and some sort of contentText of the card that it supposed
       to go off at the current system time
     */
    public MyNotification(Context context) {
        Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
        snoozeIntent.setAction("snooze");
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context,
                this.SNOOZE_REQUEST_CODE, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent checkIntent = new Intent(context, AlarmReceiver.class);
        checkIntent.setAction("check");
        PendingIntent checkPendingIntent = PendingIntent.getBroadcast(context,
                this.CHECK_REQUEST_CODE, checkIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        this.builder = new NotificationCompat.Builder(context);

        this.builder.setAutoCancel(true);
        // TODO: set the icon to my custom notification icon
        this.builder.setSmallIcon(R.mipmap.ic_launcher);
        this.builder.setTicker("This is the ticker.");
        this.builder.setWhen(System.currentTimeMillis());
        this.builder.setContentTitle("Ayy");
        this.builder.setContentText("Lmao");
        this.builder.setContentIntent(snoozePendingIntent);
        this.builder.setPriority(Notification.PRIORITY_HIGH);
        // TODO: show the snoozeInterval dialog when the user clicks this action
        String snoozeStr = context.getString(R.string.snooze);
        this.builder.addAction(R.drawable.ic_snooze_black_24dp, snoozeStr, snoozePendingIntent);

        String checkStr = context.getString(R.string.check);
        this.builder.addAction(R.drawable.ic_check_black_24dp, checkStr, checkPendingIntent);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String alarms = sp.getString("ringtone", "default ringtone");
        this.builder.setSound(Uri.parse(alarms));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // may need to change this back to AlarmReceiver.class
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(checkIntent);

        // builds notification and issues it
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(this.UNIQUE_ID, this.builder.build());
    }
}