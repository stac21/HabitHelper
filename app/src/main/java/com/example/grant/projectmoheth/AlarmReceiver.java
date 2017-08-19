package com.example.grant.projectmoheth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("notification")) {
            new MyNotification(context);
        } else if (intent.getAction().equals("check")) {
            System.out.println("Check");
        } else if (intent.getAction().equals("snooze")){
            System.out.println("Snooze");
        }
    }
}
