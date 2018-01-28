package com.forloopers.grant.projectmoheth;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class Utils {
    private static Theme currentTheme;
    private static final String FILE_NAME = "UtilsFile";

    public static void changeTheme(Activity activity, Theme theme) {
        currentTheme = theme;
        saveCurrentTheme(activity.getApplicationContext());
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    // call in the onCreate method before setContentView is called
    public static void onActivityCreateSetTheme(Activity activity) {
        currentTheme = getCurrentTheme(activity);

        switch (currentTheme) {
            case LIGHT_THEME:
                activity.setTheme(R.style.LightTheme);
                break;
            case NIGHT_THEME:
                activity.setTheme(R.style.NightTheme);
                break;
            case BLACK_THEME:
                activity.setTheme(R.style.BlackTheme);
                break;
            case TEST:
                activity.setTheme(R.style.AppTheme);
        }
    }

    // call in the onCreate method before setSupportActionBar is called
    public static void setToolbarTheme(Activity activity, Toolbar toolbar) {
        switch (Utils.getCurrentTheme(activity)) {
            case LIGHT_THEME:
                toolbar.setPopupTheme(R.style.LightTheme);
                toolbar.setTitleTextColor(Color.BLACK);

                Drawable backArrow =
                        activity.getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                backArrow.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                break;
            case NIGHT_THEME:
                toolbar.setPopupTheme(R.style.NightTheme);
                // titleTextColor does not need to be set to white
                break;
            case BLACK_THEME:
                toolbar.setPopupTheme(R.style.NightTheme);
                // titleTextColor does not need to be set to white
                break;
        }
    }

    private static void saveCurrentTheme(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        //converts the Theme into a JSON String
        String json = new Gson().toJson(currentTheme);

        editor.putString(FILE_NAME, json);

        editor.apply();
    }

    public static Theme getCurrentTheme(Activity activity) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String json = sp.getString(FILE_NAME, null);

        if (json == null) {
            changeTheme(activity, Theme.LIGHT_THEME);

            return Theme.LIGHT_THEME;
        } else {
            Type collectionType = new TypeToken<Theme>(){}.getType();
            Theme theme = new Gson().fromJson(json, collectionType);

            return theme;
        }
    }

    public static void registerAlarm(Context context, CardInfo cardInfo) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Intent i = new Intent(context, AlarmReceiver.class);
        i.putExtra("com.example.grant.projectmoheth.alarmCardInfo",
                new Gson().toJson(cardInfo));
        i.setAction("com.example.grant.projectmoheth.notification");

        PendingIntent pi = PendingIntent.getBroadcast(context, cardInfo.uniqueID, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, cardInfo.hour);
        calendar.set(Calendar.MINUTE, cardInfo.minute);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }

    public static <T extends Comparable<T>> void insertionSort(ArrayList<T> list) {
        if (list.size() > 1) {
            for (int i = 0; i < list.size(); i++) {
                T val = list.get(i);

                int j = i - 1;

                while (j > -1 && val.compareTo(list.get(j)) < 0) {
                    list.set(j + 1, list.get(j));
                    j--;
                }

                list.set(j + 1, val);
            }
        }
    }

    public static <T extends Comparable<T>> int binarySearch(ArrayList<T> list, T val) {
        int max = list.size() - 1;
        int min = 0;

        // if the size is 0
        if (max < min) {
            return -1;
        } else if (list.get(min).compareTo(val) > 0 || list.get(max).compareTo(val) < 0) {
            return -1;
        } else if (list.size() == 1) {
            return 0;
        }

        int i = (max + min) / 2;

        while (min <= max) {
            if (list.get(i).compareTo(val) == 0) {
                return i;
            } else if (list.get(i).compareTo(val) < 0) {
                min = i + 1;
            } else if (list.get(i).compareTo(val) > 0) {
                max = i - 1;
            }

            i = (min + max) / 2;
        }

        return -1;
    }

    public static <T extends Comparable<T>> void mergeSort(ArrayList<T> list, int p, int r) {
        // if subarray's size > 1
        if (p < r) {
            int q = (p + r) / 2;

            mergeSort(list, p, q);
            mergeSort(list, q + 1, r);
            merge(list, p, q, r);
        }
    }

    private static <T extends Comparable<T>> void merge(ArrayList<T> list, int p, int q, int r) {
        int lowSize = q - p + 1;
        int highSize = r - q;
        ArrayList<T> lowHalf = new ArrayList<T>();
        ArrayList<T> highHalf = new ArrayList<T>();
        int i = 0, j = 0, k = p;

        // copy list[p...q] and list[q + 1...r]
        for (int n = 0; k <= q; n++, k++) {
            lowHalf.add(n, list.get(k));
        }
        for (int n = 0; k <= r; n++, k++) {
            highHalf.add(n, list.get(k));
        }

        k = p;

        while (i < lowSize && j < highSize) {
            T lowVal = lowHalf.get(i);
            T highVal = highHalf.get(j);

            if (lowVal.compareTo(highVal) < 0) {
                list.set(k++, lowVal);
                i++;
            } else {
                list.set(k++, highVal);
                j++;
            }
        }

		/*
		 * copy the remaining elements of whichever array has
		 * elements left to be copied
		 */
        while (i < lowSize) {
            list.set(k++, lowHalf.get(i++));
        }
        while (j < highSize) {
            list.set(k++, highHalf.get(j++));
        }
    }
}