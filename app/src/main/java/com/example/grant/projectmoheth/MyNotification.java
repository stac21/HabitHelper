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
    private int uniqueID = 123;

    /* TODO: use the titles and some sort of contentText of the card that it supposed
       to go off at the current system time
     */
    public MyNotification(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        this.builder = new NotificationCompat.Builder(context);

        this.builder.setAutoCancel(true);
        // TODO: set the icon to my custom notification icon
        this.builder.setSmallIcon(R.mipmap.ic_launcher);
        this.builder.setTicker("This is the ticker.");
        this.builder.setWhen(System.currentTimeMillis());
        this.builder.setContentTitle("Ayy");
        this.builder.setContentText("Lmao");
        this.builder.setContentIntent(pendingIntent);
        this.builder.setPriority(Notification.PRIORITY_HIGH);
        // TODO: show the snoozeInterval dialog when the user clicks this action
        this.builder.addAction(R.drawable.ic_snooze_black_24dp, "Snooze", pendingIntent);
        // TODO: change this pendingIntent to a PendingIntent that takes the user to the habit activity
        this.builder.addAction(R.drawable.ic_check_black_24dp, "Done", pendingIntent);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String alarms = sp.getString("ringtone", "default ringtone");
        this.builder.setSound(Uri.parse(alarms));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // may need to change this back to AlarmReceiver.class
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(i);

        // builds notification and issues it
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(this.uniqueID, this.builder.build());
    }
}