package com.example.grant.projectmoheth;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MonthFragment extends Fragment {
    int year;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.month_fragment, container, false);

        TextView monthFragmentHeader = (TextView) view.findViewById(R.id.month_fragment_header);

        Calendar currentTime = Calendar.getInstance();

        int month = currentTime.get(Calendar.MONTH);
        this.year = currentTime.get(Calendar.YEAR);

        String[] months = getResources().getStringArray(R.array.months_array);
        String monthString = months[month];

        currentTime.set(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK);

        System.out.println(dayOfWeek);

        monthFragmentHeader.setText(monthString + " " + year);

        GridLayout gl = (GridLayout) view.findViewById(R.id.grid_layout);

        /*
         populate the part of the calendar that is filled with last month's days if first day
         of the current month is not Sunday
        */
        if (dayOfWeek != Calendar.SUNDAY) {
            int day;

            if (month == Calendar.JANUARY)
                day = this.getDaysInMonth(Calendar.DECEMBER) - (dayOfWeek - 2);
            else
                day = this.getDaysInMonth(month - 1) - (dayOfWeek - 2);

            int end = 7 + (dayOfWeek - 1);

            for (int j = 7; j < end; j++) {
                TextView tv = new TextView(view.getContext());
                tv.setText(day + "");
                day++;

                gl.addView(tv, j);
            }
        }

        // populate the calendar with this month's days
        int day = 1;
        int end = this.getDaysInMonth(month) + 7 + (dayOfWeek - 1);

        for (int j = 7 + (dayOfWeek - 1); j < end; j++) {
            TextView tv = new TextView(view.getContext());
            tv.setText(day + "");
            day++;

            gl.addView(tv, j);
        }

        currentTime.set(Calendar.DAY_OF_MONTH, this.getDaysInMonth(month));

        if (currentTime.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            day = 1;

            for (int j = end; j % 6 == 0; j++) {
                TextView tv = new TextView(view.getContext());
                tv.setText(day + "");
                day++;

                gl.addView(tv, j);
            }
        }

        return view;
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
}
