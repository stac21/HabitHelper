package com.example.grant.projectmoheth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

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

        setTitle(cardInfo.name);

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
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // refresh month and consistency fragments if the habit was not checked previously
                if (!MainActivity.cardAdapter.getCard(MainActivity.position).getChecked()) {
                    MainActivity.cardAdapter.getCard(MainActivity.position).setChecked(
                            HabitActivity.this, true);

                    monthFragment.refreshCalendar(cardInfo);
                    consistencyFragment.refresh(cardInfo);
                } else {
                    MainActivity.cardAdapter.getCard(MainActivity.position).setChecked(
                            HabitActivity.this, true);
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
