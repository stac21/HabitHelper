package com.example.grant.projectmoheth;

/*
    in a separate class than the CardAdapter and CardViewHolder because I need to access the hour and
    minute in the AlarmReceiver class
*/
public class CardInfo {
    protected String name;
    protected String description;
    protected int hour;
    protected int minute;
    protected int dateCreated;
    protected String selectedDay;

    public CardInfo(String name, String description, int hour, int minute, int dateCreated,
                    String selectedDay) {
        this.name = name;
        this.description = description;
        this.hour = hour;
        this.minute = minute;
        this.dateCreated = dateCreated;
        this.selectedDay = selectedDay;
    }
}