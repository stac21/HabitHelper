package com.example.grant.projectmoheth;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    private SharedPreferences sp;
    private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.sp = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        this.sp.unregisterOnSharedPreferenceChangeListener(this.listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.sp.registerOnSharedPreferenceChangeListener(this.listener);
    }

    public static class SettingsFragment extends PreferenceFragment {
        private SharedPreferences sp;
        private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // TODO: make the settings do what they are meant to do and save the summaries
                switch (key) {
                    case "night_mode":
                        CheckBoxPreference nightMode = (CheckBoxPreference) findPreference(key);

                        break;
                    case "snooze_interval":
                        snoozeInterval.setSummary(snoozeInterval.getValue());

                        break;
                    case "ringtone":
                        RingtonePreference ringtone = (RingtonePreference) findPreference(key);

                        ringtone.setSummary(ringtone.getRingtoneType());

                        break;
                }
            }
        };
        private ListPreference snoozeInterval;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.addPreferencesFromResource(R.xml.preferences);

            this.sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            this.snoozeInterval = (ListPreference) findPreference("snooze_interval");
            this.snoozeInterval.setSummary(this.snoozeInterval.getValue());
        }

        @Override
        public void onPause() {
            super.onPause();

            this.sp.unregisterOnSharedPreferenceChangeListener(this.listener);
        }

        @Override
        public void onResume() {
            super.onResume();

            this.sp.registerOnSharedPreferenceChangeListener(this.listener);
        }
    }
}
