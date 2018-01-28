package com.forloopers.grant.projectmoheth;

import android.app.NotificationManager;
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
        // TODO register the PendingIntents on device wakeup (intent.getAction().equals(ACTION_BOOT_COMPLETED)
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sp.getString(MainActivity.CARD_FILE, null);

            System.out.println("boot completed");

            if (json != null) {
                Toast.makeText(context, "json was not null", Toast.LENGTH_LONG).show();

                Type collectionType = new TypeToken<ArrayList<CardInfo>>() {
                }.getType();
                ArrayList<CardInfo> cardInfoList = new Gson().fromJson(json,
                        collectionType);

                Calendar calendar = Calendar.getInstance();

                if (cardInfoList.size() != 0) {

                    for (byte i = 0; i < cardInfoList.size(); i++) {
                        CardInfo cardInfo = cardInfoList.get(i);

                        Utils.registerAlarm(context, cardInfo);

                        Toast.makeText(context, "alarm should have been registered", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else if (intent.getAction().equals("com.example.grant.projectmoheth.notification")) {
            System.out.println("Notification's broadcast was recieved");

            Type cardInfoCollectionType = new TypeToken<CardInfo>() {
            }.getType();
            CardInfo cardInfo = new Gson().fromJson(
                    intent.getStringExtra("com.example.grant.projectmoheth.alarmCardInfo"),
                    cardInfoCollectionType);

            ArrayList<CardInfo> cardInfoList = MainActivity.loadCardInfoList(context);

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

                                cardInfoList.get(j).setChecked(false);

                                MainActivity.saveCardInfoList(context, cardInfoList);

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
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
        } else if (intent.getAction().equals("com.example.grant.projectmoheth.check")) {
            ArrayList<Integer> idList = MainActivity.loadIDList(context);

            Type cardInfoCollectionType = new TypeToken<CardInfo>() {
            }.getType();
            CardInfo cardInfo = new Gson().fromJson(
                    intent.getStringExtra("com.example.grant.projectmoheth.checkCardInfo"),
                    cardInfoCollectionType);

            // if there is a NullPointerException when clicking the check button from the test
            // notification and cardInfoList.size() == 0, this is why. Done to check whether
            // the card was deleted.
            int i = this.cardIndexInList(context, cardInfo);

            if (i != -1) {
                NotificationManager nm = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);

                ArrayList<CardInfo> cardInfoList = MainActivity.loadCardInfoList(context);

                int uniqueID = cardInfo.uniqueID;
                // cancel the notification
                // TODO check to see whether the correct notification will be canceled
                nm.cancel(uniqueID);

                MainActivity.saveCardInfoList(context, cardInfoList);
            }
        } /*else if (intent.getAction().equals("com.example.grant.projectmoheth.snooze")){
            NotificationManager nm = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            ArrayList<Integer> idList = MainActivity.loadIDList(context);

            Type cardInfoCollectionType = new TypeToken<CardInfo>(){}.getType();
            CardInfo cardInfo = new Gson().fromJson(
                    intent.getStringExtra("com.example.grant.projectmoheth.snoozeCardInfo"),
                    cardInfoCollectionType);

            // check to make sure the card has not been deleted
            int i = Utils.binarySearch(idList, cardInfo.uniqueID);

            if (i != -1) {
                // cancel the notification if the card is still in the cardInfoList
                // TODO check to see whether the correct notification will be cancelled
                int uniqueID = cardInfo.uniqueID;

                nm.cancel(uniqueID);

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
        */
    }

    private int cardIndexInList(Context context, CardInfo cardInfo) {
        ArrayList<CardInfo> cardInfoList = MainActivity.loadCardInfoList(context);

        for (int i = 0; i < cardInfoList.size(); i++) {
            if (cardInfoList.get(i).equals(cardInfo)) {
                return i;
            }
        }

        return -1;
    }
}
