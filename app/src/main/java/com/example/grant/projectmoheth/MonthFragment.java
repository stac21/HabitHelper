package com.example.grant.projectmoheth;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthFragment extends Fragment {
    private int year, month, dayOfWeek;
    private View view;
    private GridLayout gl;

    private static final int SUCCESSFUL_DAY_TEXT_COLOR = Color.CYAN;
    private static final int FAILED_DAY_TEXT_COLOR = Color.RED;
    private static final int OTHER_MONTH_TEXT_COLOR = Color.DKGRAY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        this.view = inflater.inflate(R.layout.month_fragment, container, false);

        return view;
    }

    // TODO make the colors of the calendar appealing and implement the color indicators
    public void makeCalendar(CardInfo cardInfo) {
        Calendar currentTime = Calendar.getInstance();

        TextView monthFragmentHeader = (TextView) this.view.findViewById(
                R.id.month_fragment_header);

        ArrayList<Date> savedDates = cardInfo.savedDates;
        ArrayList<Integer> selectedDays = cardInfo.selectedDays;

        this.month = currentTime.get(Calendar.MONTH);
        this.year = currentTime.get(Calendar.YEAR);

        String[] months = getResources().getStringArray(R.array.months_array);
        String monthString = months[month];

        currentTime.set(Calendar.DAY_OF_MONTH, 1);
        this.dayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK);

        monthFragmentHeader.setText(monthString + " " + year);

        this.gl = (GridLayout) view.findViewById(R.id.grid_layout);

        // populate the first row (which contains the names of the days of the week)
        for (int i = 0; i < 7; i++) {
            TextView tv = new TextView(view.getContext());

            tv.setText(" " + this.getDayOfWeekName(i) + " ");
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            this.gl.addView(tv);
        }

        /*
         populate the part of the calendar that is filled with last month's days if first day
         of the current month is not Sunday
        */
        if (dayOfWeek != Calendar.SUNDAY) {
            int day;
            int tempDayOfWeek = dayOfWeek;

            day = (month == Calendar.JANUARY) ? this.getDaysInMonth(Calendar.DECEMBER) -
                    (dayOfWeek - 2) : this.getDaysInMonth(month - 1) - (dayOfWeek - 2);
            int lastMonth = (month == Calendar.JANUARY) ? Calendar.DECEMBER : month - 1;
            int year = (month == Calendar.JANUARY) ? this.year - 1 : this.year;

            int end = 7 + (dayOfWeek - 1);

            for (int i = 7; i < end; i++) {
                TextView tv = new TextView(view.getContext());
                tv.setText(day + "");
                tv.setTextColor(OTHER_MONTH_TEXT_COLOR);

                // TODO make this comment not suck draw a background on the day
                for (int j = 0; j < savedDates.size(); j++) {
                    if (savedDates.get(j).isSameDate(lastMonth, day, year))
                        tv.setTextColor(SUCCESSFUL_DAY_TEXT_COLOR);
                    else {
                        for (int k = 0; k < selectedDays.size(); k++) {
                            if (tempDayOfWeek - 1 == selectedDays.get(k))
                                tv.setTextColor(FAILED_DAY_TEXT_COLOR);
                        }
                    }
                }

                day++;
                tempDayOfWeek = (tempDayOfWeek == Calendar.SUNDAY) ? Calendar.MONDAY :
                        ++tempDayOfWeek;

                gl.addView(tv, i);
            }
        }

        // populate the calendar with this month's days
        int day = 1;
        int end = this.getDaysInMonth(month) + 7 + (dayOfWeek - 1);
        int tempDayOfWeek = dayOfWeek;

        for (int i = 7 + (dayOfWeek - 1); i < end; i++) {
            TextView tv = new TextView(view.getContext());
            tv.setText(day + "");
            Calendar calendar = Calendar.getInstance();
            int dayOfYear = this.getDayOfYear(day, month);

            boolean afterOrOnDateCreatedOrEdited = cardInfo.dateCreatedOrEdited <= dayOfYear +
                    this.year;
            boolean beforeOrOnToday = calendar.get(Calendar.DAY_OF_YEAR) >= dayOfYear;

            // draw backgrounds on the successful and failed days
            if (cardInfo.containsDayOfWeek(tempDayOfWeek - 1) && afterOrOnDateCreatedOrEdited &&
                    beforeOrOnToday) {
                boolean success = false;

                for (int j = 0; j < savedDates.size() && !success; j++) {
                    if (savedDates.get(j).isSameDate(month, day, this.year)) {
                        tv.setTextColor(SUCCESSFUL_DAY_TEXT_COLOR);
                        success = true;
                    }
                }

                if (!success && !beforeOrOnToday) {
                    tv.setTextColor(FAILED_DAY_TEXT_COLOR);
                }
            }

            day++;
            tempDayOfWeek = (tempDayOfWeek == Calendar.SATURDAY) ? Calendar.SUNDAY :
                    ++tempDayOfWeek;

            gl.addView(tv, i);
        }

        /*
        populate the part of the calendar filled with next month's days if the last day of this
        month is not a saturday
         */
        currentTime.set(Calendar.DAY_OF_MONTH, this.getDaysInMonth(month));

        if (currentTime.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            day = 1;

            for (int j = end; j % 6 != 0; j++) {
                TextView tv = new TextView(view.getContext());
                tv.setText(day + "");
                tv.setTextColor(OTHER_MONTH_TEXT_COLOR);

                int nextMonth = (month == Calendar.DECEMBER) ? Calendar.JANUARY : month + 1;
                int year = (month == Calendar.DECEMBER) ? this.year + 1 : this.year;

                for (int n = 0; n < savedDates.size(); n++) {
                    if (savedDates.get(n).isSameDate(nextMonth, day, year)) {
                        tv.setTextColor(SUCCESSFUL_DAY_TEXT_COLOR);
                    } else {
                        for (int k = 0; k < selectedDays.size(); k++) {
                            if (tempDayOfWeek - 1 == selectedDays.get(k)) {
                                tv.setTextColor(FAILED_DAY_TEXT_COLOR);
                            }
                        }
                    }
                }

                day++;

                gl.addView(tv, j);
            }
        }
    }

    public void refreshCalendar(CardInfo cardInfo) {
        this.gl.removeAllViewsInLayout();

        this.makeCalendar(cardInfo);
    }

    private int getDayOfYear(int day, int month) {
        int total = 0;

        for (int i = 0; i < month; i++) {
            total += this.getDaysInMonth(i);
        }

        total += day;

        return total;
    }

    private byte getDaysInMonth(int month) {
        switch (month) {
            case Calendar.JANUARY:
                return 31;
            case Calendar.FEBRUARY:
                return (this.year % 4 == 0) ? (byte) 29 : 28;
            case Calendar.MARCH:
                return 31;
            case Calendar.APRIL:
                return 30;
            case Calendar.MAY:
                return 31;
            case Calendar.JUNE:
                return 30;
            case Calendar.JULY:
                return 31;
            case Calendar.AUGUST:
                return 31;
            case Calendar.SEPTEMBER:
                return 30;
            case Calendar.OCTOBER:
                return 31;
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.DECEMBER:
                return 31;
            default:
                return 0;
        }
    }

    private String getDayOfWeekName(int num) {
        switch (num) {
            case 0:
                return getString(R.string.sun_string);
            case 1:
                return getString(R.string.mon_string);
            case 2:
                return getString(R.string.tue_string);
            case 3:
                return getString(R.string.wed_string);
            case 4:
                return getString(R.string.thu_string);
            case 5:
                return getString(R.string.fri_string);
            case 6:
                return getString(R.string.sat_string);
            default:
                return "";
        }
    }
}
