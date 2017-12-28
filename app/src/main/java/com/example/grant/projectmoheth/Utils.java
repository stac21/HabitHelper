package com.example.grant.projectmoheth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Utils {
    private static Theme currentTheme;
    private static final String FILE_NAME = "UtilsFile";

    public static void changeTheme(Activity activity, Theme theme) {
        currentTheme = theme;
        saveCurrentTheme(activity.getApplicationContext());
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    // call in the onCreate method before setContentView
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

    /*
     using insertionSort because high school prevented me from getting much farther in my study of
     sorting algorithms, definitely NOT because I think it is good.
      */
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
        } else if (list.size() == 1) {
            return 0;
        }

        int i = (max + min) / 2;

        while (true) {
            if (list.get(i) == val) {
                return i;
            } else if (list.get(i).compareTo(val) < 0) {
                min = i;
            } else if (list.get(i).compareTo(val) > 0) {
                max = i;
            }
            i = (min + max) / 2;
        }
    }
}