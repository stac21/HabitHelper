package com.example.grant.projectmoheth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

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

        System.out.println("This method was called");

        if (json == null) {
            changeTheme(activity, Theme.LIGHT_THEME);

            System.err.println("json == null");

            return Theme.LIGHT_THEME;
        } else {
            Type collectionType = new TypeToken<Theme>(){}.getType();
            Theme theme = new Gson().fromJson(json, collectionType);

            System.err.println("json != null");

            return theme;
        }
    }
}
