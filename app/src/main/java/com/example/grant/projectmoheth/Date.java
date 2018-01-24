package com.example.grant.projectmoheth;

import java.util.Calendar;

public class Date {
    private boolean completed;
    private int month, dayOfMonth, year;

    public Date() {
        Calendar calendar = Calendar.getInstance();

        this.month = calendar.get(Calendar.MONTH);
        this.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        this.year = calendar.get(Calendar.YEAR);
    }

    public Date(boolean completed) {
        Calendar calendar = Calendar.getInstance();

        this.completed = completed;
        this.month = calendar.get(Calendar.MONTH);
        this.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        this.year = calendar.get(Calendar.YEAR);
    }

    public Date(int month, int dayOfMonth, int year) {
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.year = year;
    }

    public boolean isSameDate(int month, int dayOfMonth, int year) {
        return (this.month == month && this.dayOfMonth == dayOfMonth && this.year == year);
    }

    public boolean isSameDate(Date date) {
        return this.isSameDate(date.getMonth(), date.getDayOfMonth(), date.getYear());
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean getCompleted() {
        return this.completed;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDayOfMonth() {
        return this.dayOfMonth;
    }

    public int getYear() {
        return this.year;
    }
}