package com.example.grant.projectmoheth;

import android.app.Activity;
import android.content.Intent;

public class Utils {
    public static final byte LIGHT_THEME = 0;
    public static final byte NIGHT_THEME = 1;
    public static final byte BLACK_THEME = 2;
    public static final byte TEST = 3;
    private static byte currentTheme;

    public static void changeTheme(Activity activity, byte theme) {
        currentTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    // call in the onCreate method before setContentView
    public static void onActivityCreateSetTheme(Activity activity) {
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

    public static byte getCurrentTheme() {
        return currentTheme;
    }
}
