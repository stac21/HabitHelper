package com.example.grant.projectmoheth;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
        this.builder.setSmallIcon(R.mipmap.ic_launcher);
        this.builder.setTicker("This is the ticker.");
        this.builder.setWhen(System.currentTimeMillis());
        this.builder.setContentTitle("Ayy");
        this.builder.setContentText("Lmao");
        this.builder.setContentIntent(pendingIntent);

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