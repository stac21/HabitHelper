package com.example.grant.projectmoheth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
        Utils.onActivityCreateSetTheme(this);
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
        private ListPreference snoozeInterval;
        private RingtonePreference ringtone;
        private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // TODO: make the settings do what they are meant to do and save the summaries
                switch (key) {
                    case "night_mode":
                        CheckBoxPreference nightMode = (CheckBoxPreference) findPreference(key);

                        if (nightMode.isChecked())
                            Utils.changeTheme(getActivity(), Theme.NIGHT_THEME);
                        else
                            Utils.changeTheme(getActivity(), Theme.LIGHT_THEME);
                        break;
                    case "amoled_mode":
                        CheckBoxPreference amoledMode = (CheckBoxPreference) findPreference(key);

                        if (amoledMode.isChecked())
                            Utils.changeTheme(getActivity(), Theme.BLACK_THEME);
                        else
                            Utils.changeTheme(getActivity(), Theme.NIGHT_THEME);
                        break;
                    case "snooze_interval":
                        // TODO add a "custom" option to preferences.xml and create a dialog when it is clicked
                        break;
                }
            }
        };


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.addPreferencesFromResource(R.xml.preferences);

            this.sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

            this.snoozeInterval = (ListPreference) findPreference("snooze_interval");
            this.snoozeInterval.setSummary(this.snoozeInterval.getValue());
            this.ringtone = (RingtonePreference) findPreference("ringtone");
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
