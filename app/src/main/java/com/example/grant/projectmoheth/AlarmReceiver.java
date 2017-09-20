package com.example.grant.projectmoheth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sp.getString(MainActivity.CARD_FILE, null);

            if (json != null) {
                Type collectionType = new TypeToken<ArrayList<CardInfo>>(){}.getType();
                ArrayList<CardInfo> list = new Gson().fromJson(MainActivity.CARD_FILE,
                        collectionType);

                Calendar calendar = Calendar.getInstance();

                if (list.size() != 0) {
                    MainActivity.alarmManager = (AlarmManager)
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


                        MainActivity.alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
                    }
                }
            }
        } else if (intent.getAction().equals("com.example.grant.projectmoheth.notification")) {
            Type cardInfoCollectionType = new TypeToken<CardInfo>(){}.getType();
            CardInfo cardInfo = new Gson().fromJson(
                    intent.getStringExtra("com.example.grant.projectmoheth.alarmCardInfo"),
                    cardInfoCollectionType);

            Type cardInfoListCollectionType = new TypeToken<ArrayList<CardInfo>>(){}.getType();
            ArrayList<CardInfo> cardInfoList = new Gson().fromJson(MainActivity.CARD_FILE,
                    cardInfoListCollectionType);

            Calendar calendar = Calendar.getInstance();

            if (cardInfo.dateCreated != calendar.get(Calendar.DAY_OF_YEAR) +
                    calendar.get(Calendar.YEAR) || calendar.get(Calendar.HOUR_OF_DAY) >=
                    cardInfo.hour && calendar.get(Calendar.MINUTE) >= cardInfo.minute) {
                for (int i = 0; i < cardInfoList.size(); i++) {
                    if (cardInfoList.get(i).equals(cardInfo)) {
                        System.out.println("This code was reached");
                        new MyNotification(context, cardInfo);
                        break;
                    }
                }
            }
        } else if (intent.getAction().equals("com.example.grant.projectmoheth.check")) {

            Type collectionType = new TypeToken<CardInfo>(){}.getType();
            CardInfo cardInfo = new Gson().fromJson(
                    intent.getStringExtra("com.example.grant.projectmoheth.checkCardInfo"),
                    collectionType);

            // TODO check off the habit
        } else if (intent.getAction().equals("com.example.grant.projectmoheth.snooze")){
            System.out.println("Snooze");
            // TODO implement snoozing
        }
    }
}
