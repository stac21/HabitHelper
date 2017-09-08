package com.example.grant.projectmoheth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.grant.projectmoheth.notification")) {
            Type collectionType = new TypeToken<CardInfo>(){}.getType();
            CardInfo cardInfo = new Gson().fromJson(
                    intent.getStringExtra("com.example.grant.projectmoheth.alarmCardInfo"),
                    collectionType);

            Calendar calendar = Calendar.getInstance();

            if (cardInfo.dateCreated != calendar.get(Calendar.DAY_OF_YEAR) +
                    calendar.get(Calendar.YEAR) && calendar.get(Calendar.HOUR_OF_DAY) >
                    cardInfo.hour && calendar.get(Calendar.MINUTE) > cardInfo.minute) {
                new MyNotification(context, cardInfo);
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
