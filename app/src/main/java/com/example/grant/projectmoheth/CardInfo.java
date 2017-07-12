package com.example.grant.projectmoheth;

import android.content.res.Resources;

/*
    in a separate class than the CardAdapter and CardViewHolder because I need to access the hour and
    minute in the AlarmReceiver class
*/
public class CardInfo {
    protected String name;
    protected String description;
    protected int hour;
    protected int minute;
    protected String selectedDay;

    public CardInfo(String name, String description, int hour, int minute, String selectedDay) {
        this.name = name;
        this.description = description;
        this.hour = hour;
        this.minute = minute;
        this.selectedDay = selectedDay;
    }
}