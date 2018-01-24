package com.example.grant.projectmoheth;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO register the PendingIntents on device wakeup (intent.getAction().equals(ACTION_BOOT_COMPLETED) or ACTION_LOCKED_BOOT_COMPLETED
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sp.getString(MainActivity.CARD_FILE, null);

            System.out.println("boot completed");

            if (json != null) {
                System.out.println("json was not null");

                Type collectionType = new TypeToken<ArrayList<CardInfo>>(){}.getType();
                ArrayList<CardInfo> list = new Gson().fromJson(json,
                        collectionType);

                Calendar calendar = Calendar.getInstance();

                if (list.size() != 0) {
                    AlarmManager alarmManager = (AlarmManager)
                            context.getSystemService(Context.ALARM_SERVICE);

                    for (byte i = 0; i < list.size(); i++) {
                        CardInfo cardInfo = list.get(i);

                        Intent intent1 = new Intent(context, AlarmReceiver.class);
                        intent1.putExtra("com.example.grant.projectmoheth.alarmCardInfo",
                                new Gson().toJson(cardInfo));
                        intent1.setAction("com.example.grant.projectmoheth.notification");

                        PendingIntent pi = PendingIntent.getBroadcast(context,
                                MainActivity.ALARM_REQUEST_CODE,intent1,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, cardInfo.hour);
                        calendar.set(Calendar.MINUTE, cardInfo.minute);

                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
                    }
                }
            }
        } else if (intent.getAction().equals("com.example.grant.projectmoheth.notification")) {
            System.out.println("Notification's broadcast was recieved");

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sp.getString(MainActivity.CARD_FILE, null);

            Type cardInfoCollectionType = new TypeToken<CardInfo>(){}.getType();
            CardInfo cardInfo = new Gson().fromJson(
                    intent.getStringExtra("com.example.grant.projectmoheth.alarmCardInfo"),
                    cardInfoCollectionType);

            Type cardInfoListCollectionType = new TypeToken<ArrayList<CardInfo>>(){}.getType();
            ArrayList<CardInfo> cardInfoList = new Gson().fromJson(json,
                    cardInfoListCollectionType);

            Calendar calendar = Calendar.getInstance();

            if ((cardInfo.dateCreatedOrEdited != calendar.get(Calendar.DAY_OF_YEAR) +
                    calendar.get(Calendar.YEAR) || calendar.get(Calendar.HOUR_OF_DAY) >=
                    cardInfo.hour && calendar.get(Calendar.MINUTE) >= cardInfo.minute)) {
                System.out.println("Got past the first condition");
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

                for (int i = 0; i < cardInfo.selectedDays.size(); i++) {
                    if (dayOfWeek == cardInfo.selectedDays.get(i) ||
                            cardInfo.selectedDays.get(i) == 7) {
                        for (int j = 0; j < cardInfoList.size(); j++) {
                            if (cardInfoList.get(j).equals(cardInfo)) {
                                System.out.println("This code was reached");
                                MainActivity.cardAdapter.getCard(j).setChecked(context, false);

                                SharedPreferences.Editor editor = sp.edit();

                                String newJson = new Gson().toJson(
                                        MainActivity.cardAdapter.getCardInfoList());
                                editor.putString(MainActivity.CARD_FILE, newJson);

                                editor.apply();

                                new MyNotification(context, cardInfo);

                                break;
                            }
                        }
                    }
                }
            }
        } else if (intent.getAction().equals("com.example.grant.projectmoheth.notification_clicked")) {
            Intent i = new Intent(context, HabitActivity.class);
            i.putExtra("com.example.grant.projectmoheth.card",
                    intent.getStringExtra("com.example.grant.projectmoheth.card"));

            context.startActivity(i);
        } else if (intent.getAction().equals("com.example.grant.projectmoheth.check")) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sp.getString(MainActivity.CARD_FILE, null);
            NotificationManager nm = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            Type cardInfoCollectionType = new TypeToken<CardInfo>(){}.getType();
            CardInfo cardInfo = new Gson().fromJson(
                    intent.getStringExtra("com.example.grant.projectmoheth.checkCardInfo"),
                    cardInfoCollectionType);

            Type cardInfoListCollectionType = new TypeToken<ArrayList<CardInfo>>(){}.getType();
            ArrayList<CardInfo> cardInfoList = new Gson().fromJson(json, cardInfoListCollectionType);

            // cancel the notification
            // TODO check to see whether the correct notification will be canceled
            int uniqueID = cardInfo.uniqueID;

            nm.cancel(uniqueID);

            // if there is a NullPointerException when clicking the check button from the test
            // notification and cardInfoList.size() == 0, this is why
            for (int i = 0; i < cardInfoList.size(); i++) {
                if (cardInfoList.get(i).equals(cardInfo)) {
                    MainActivity.cardAdapter.getCard(i).setChecked(context, true);
                    SharedPreferences.Editor editor = sp.edit();

                    String newJson = new Gson().toJson(MainActivity.cardAdapter.getCardInfoList());
                    editor.putString(MainActivity.CARD_FILE, newJson);

                    editor.apply();
                }
            }
        } else if (intent.getAction().equals("com.example.grant.projectmoheth.snooze")){
            NotificationManager nm = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // cancel the notification
            // TODO check to see whether the correct notification will be cancelled

            System.out.println("Snooze");
            // TODO figure out whether snoozing works
            intent.setAction("com.example.grant.projectmoheth.notification");
            PendingIntent pi = PendingIntent.getBroadcast(context, MainActivity.ALARM_REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                    Context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
                    new Settings.SettingsFragment().getSnoozeLengthMillis(), pi);
        }
    }
}
