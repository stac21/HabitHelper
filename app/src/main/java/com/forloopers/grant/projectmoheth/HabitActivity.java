package com.forloopers.grant.projectmoheth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;

public class HabitActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_habit);

        Intent i = this.getIntent();
        String json = i.getStringExtra("com.example.grant.projectmoheth.card");
        Type collectionType = new TypeToken<CardInfo>(){}.getType();
        final CardInfo cardInfo = new Gson().fromJson(json, collectionType);

        this.setTitle(cardInfo.name);
        int titleTextColor = (Utils.getCurrentTheme(this) == Theme.LIGHT_THEME) ?
                Color.BLACK : Color.WHITE;

        TextView descriptionTV = (TextView) findViewById(R.id.descriptionTV);
        descriptionTV.setText(cardInfo.description);

        final MonthFragment monthFragment = (MonthFragment) getFragmentManager().
                findFragmentById(R.id.month_fragment);
        monthFragment.makeCalendar(cardInfo);

        final ConsistencyFragment consistencyFragment = (ConsistencyFragment) getFragmentManager().
                findFragmentById(R.id.consistency_fragment);
        consistencyFragment.makeFragment(cardInfo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(getTitle());
        toolbarLayout.setExpandedTitleColor(titleTextColor);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // refresh month and consistency fragments if the habit was not checked previously
                CardInfo newCardInfo = MainActivity.cardAdapter.getCard(MainActivity.position);
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) ?
                        Calendar.SATURDAY - 1 : calendar.get(Calendar.DAY_OF_WEEK) - 1;

                System.out.println("dayOfWeek = " + dayOfWeek);

                System.out.println("line 60: containsDayOfWeek = " + newCardInfo.containsDayOfWeek(dayOfWeek));

                if (!newCardInfo.getChecked() && newCardInfo.containsDayOfWeek(dayOfWeek)) {
                    //newCardInfo.setChecked(HabitActivity.this, true);
                    newCardInfo.setChecked(true);
                    Toast.makeText(HabitActivity.this,
                            HabitActivity.this.getString(R.string.checked),
                            Toast.LENGTH_SHORT).show();

                    monthFragment.refreshCalendar(newCardInfo);
                    consistencyFragment.refresh(newCardInfo);
                } else {
                    Toast.makeText(HabitActivity.this,
                            HabitActivity.this.getString(R.string.already_checked),
                            Toast.LENGTH_SHORT).show();
                }
                SharedPreferences sp =
                        PreferenceManager.getDefaultSharedPreferences(HabitActivity.this);
                SharedPreferences.Editor editor = sp.edit();

                String json = new Gson().toJson(MainActivity.cardAdapter.getCardInfoList());

                editor.putString(MainActivity.CARD_FILE, json);

                editor.apply();
            }
        });
    }
}
