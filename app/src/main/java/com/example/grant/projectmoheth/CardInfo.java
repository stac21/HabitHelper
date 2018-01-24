package com.example.grant.projectmoheth;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

/*
    in a separate class than the CardAdapter and CardViewHolder because I need to access the hour and
    minute in the AlarmReceiver class
*/
public class CardInfo {
    protected String name;
    protected String description;
    protected int hour;
    protected int minute;
    // in the form of Calendar.get(Calendar.DAY_OF_YEAR) + Calendar.get(Calendar.YEAR);
    protected int dateCreatedOrEdited;
    private int streakCount;
    private boolean checked;
    private static final byte MAX_NAME_LENGTH = 13;
    private static final byte MAX_DAYS = 3;
    /*
    Meaning of numbers is as follows
    Sunday: 0, Monday: 1, Tuesday: 2, Wednesday: 3, Thursday: 4, Friday: 5, Saturday: 6, Everyday: 7
     */
    protected ArrayList<Integer> selectedDays;
    // keeps a list of the dates that have been checked for this habit
    protected ArrayList<Date> savedDates;
    protected int uniqueID;

    public CardInfo(String name, String description, int hour, int minute, ArrayList<Integer>
            selectedDays, int uniqueID) {
        Calendar calendar = Calendar.getInstance();
        int dateCreated = calendar.get(Calendar.DAY_OF_YEAR) + calendar.get(Calendar.YEAR);

        this.name = name;
        this.description = description;
        this.hour = hour;
        this.minute = minute;
        this.dateCreatedOrEdited = dateCreated;
        this.streakCount = 0;
        this.checked = false;
        this.selectedDays = selectedDays;
        this.savedDates = new ArrayList<Date>();
        this.uniqueID = uniqueID;
    }

    public String getTruncatedName() {
        if (this.name.length() >= MAX_NAME_LENGTH)
            return this.name.substring(0, MAX_NAME_LENGTH) + "...";
        else
            return this.name;
    }

    public String getSelectedDayName(Context context) {
        if (this.selectedDays.size() > 1)
            Utils.mergeSort(this.selectedDays, 0, this.selectedDays.size() - 1);

        String str = new String();

        if (this.selectedDays.size() > MAX_DAYS) {
            str = context.getString(R.string.custom);
        } else {
            for (int i = 0; i < this.selectedDays.size(); i++) {
                switch (this.selectedDays.get(i)) {
                    case 0:
                        str += context.getString(R.string.day_dialog_sunday_string) + ", ";
                        break;
                    case 1:
                        str += context.getString(R.string.day_dialog_monday_string) + ", ";
                        break;
                    case 2:
                        str += context.getString(R.string.day_dialog_tuesday_string) + ", ";
                        break;
                    case 3:
                        str += context.getString(R.string.day_dialog_wednesday_string) + ", ";
                        break;
                    case 4:
                        str += context.getString(R.string.day_dialog_thursday_string) + ", ";
                        break;
                    case 5:
                        str += context.getString(R.string.day_dialog_friday_string) + ", ";
                        break;
                    case 6:
                        str += context.getString(R.string.day_dialog_saturday_string) + ", ";
                        break;
                    case 7:
                        str += context.getString(R.string.every_day);
                        break;
                }
            }

            // removes the last comma and space
            if (this.selectedDays.get(0) != 7)
                str = str.substring(0, str.length() - 2);
        }

        return str;
    }

    public boolean equals(CardInfo cardInfo) {
        if (this.selectedDays.size() == cardInfo.selectedDays.size()) {
            for (int i = 0; i < this.selectedDays.size(); i++) {
                if (this.selectedDays.get(i) != cardInfo.selectedDays.get(i))
                    return false;
            }
        } else
            return false;

        return (this.name.equals(cardInfo.name) && this.description.equals(cardInfo.description) &&
            this.hour == cardInfo.hour && this.minute == cardInfo.minute &&
            this.dateCreatedOrEdited == cardInfo.dateCreatedOrEdited);
    }

    public void setChecked(Context context, boolean check) {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        System.out.println(this.containsDayOfWeek(dayOfWeek));

        if (check && this.checked)
            Toast.makeText(context, context.getString(R.string.already_checked), Toast.LENGTH_SHORT).
                    show();
        else if (check) {
            this.checked = true;
            this.streakCount++;

            Toast.makeText(context, context.getString(R.string.checked), Toast.LENGTH_SHORT).show();
            saveCurrentDate(true);
        } else {
            this.checked = false;
        }
    }

    private void saveCurrentDate(boolean completed) {
        this.savedDates.add(new Date(completed));
    }

    public boolean containsDayOfWeek(int dayOfWeek) {
        for (int i = 0; i < this.selectedDays.size(); i++) {
            if (this.selectedDays.get(i) == dayOfWeek) {
                return true;
            }
        }

        return false;
    }

    public int getStreakCount() {
        return this.streakCount;
    }

    public boolean getChecked() {
        return this.checked;
    }
}