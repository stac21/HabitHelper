package com.forloopers.grant.projectmoheth;

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

import com.google.gson.Gson;

import java.util.Calendar;

public class MyNotification {
    private NotificationCompat.Builder builder;
    private static final int SNOOZE_REQUEST_CODE = 0;
    private static final int CHECK_REQUEST_CODE = 1;
    private static final int HABIT_REQUEST_CODE = 3;

    private static final String CHANNEL_ID = "com.example.grant.projectmoheth.channel_id";

    public MyNotification(Context context, CardInfo cardInfo) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent habitIntent = new Intent(context, AlarmReceiver.class);
        String json = new Gson().toJson(cardInfo);
        habitIntent.putExtra("com.example.grant.projectmoheth.card", json);
        habitIntent.setAction("com.example.grant.projectmoheth.notification_clicked");
        PendingIntent habitPendingIntent = PendingIntent.getBroadcast(context,
                HABIT_REQUEST_CODE, habitIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
        Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
        snoozeIntent.putExtra("com.example.grant.projectmoheth.snoozeCardInfo",
                new Gson().toJson(cardInfo));
        snoozeIntent.setAction("com.example.grant.projectmoheth.snooze");
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context,
                SNOOZE_REQUEST_CODE, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
           */
        Intent checkIntent = new Intent(context, AlarmReceiver.class);
        checkIntent.putExtra("com.example.grant.projectmoheth.checkCardInfo",
                new Gson().toJson(cardInfo));
        checkIntent.setAction("com.example.grant.projectmoheth.check");
        PendingIntent checkPendingIntent = PendingIntent.getBroadcast(context,
                CHECK_REQUEST_CODE, checkIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        this.builder = new NotificationCompat.Builder(context);

        this.builder.setAutoCancel(true);
        // TODO: set the icon to my custom notification icon
        this.builder.setSmallIcon(R.drawable.ic_check_white_24dp);
        this.builder.setTicker(cardInfo.name + ": " + cardInfo.description);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, cardInfo.hour);
        calendar.set(Calendar.MINUTE, cardInfo.minute);
        this.builder.setWhen(calendar.getTimeInMillis());

        this.builder.setContentTitle(cardInfo.name);

        this.builder.setContentText(cardInfo.description);
        this.builder.setContentIntent(habitPendingIntent);
        this.builder.setPriority(Notification.PRIORITY_HIGH);

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = nm.getNotificationChannel(CHANNEL_ID);

            if (channel == null) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                String alarms = sp.getString("ringtone", "default ringtone");
                String name = context.getString(R.string.channel_name);
                String description = context.getString(R.string.channel_description);
                AudioAttributes.Builder builder = new AudioAttributes.Builder();
                builder.setUsage(AudioAttributes.USAGE_NOTIFICATION);

                channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.enableVibration(true);
                channel.enableLights(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                channel.setSound(Uri.parse(alarms), builder.build());

                nm.createNotificationChannel(channel);
            }
        }
        */

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String alarms = sp.getString("ringtone", "default ringtone");
        this.builder.setSound(Uri.parse(alarms));

        // sets the notification LED to the device's default
        this.builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        this.builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        /*
        String snoozeStr = context.getString(R.string.snooze);
        this.builder.addAction(R.drawable.ic_snooze_black_24dp, snoozeStr, snoozePendingIntent);
        */

        String checkStr = context.getString(R.string.check);
        this.builder.addAction(R.drawable.ic_check_black_24dp, checkStr, checkPendingIntent);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // may need to change this back to AlarmReceiver.class
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(habitIntent);

        // builds notification and issues it
        nm.notify(cardInfo.uniqueID, this.builder.build());
    }
}