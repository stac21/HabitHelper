package com.example.grant.projectmoheth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

        MonthFragment monthFragment = (MonthFragment) getFragmentManager().
                findFragmentById(R.id.month_fragment);
        monthFragment.drawBackgrounds(cardInfo.selectedDay);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Check off habit when user clicks this button
                MainActivity.cardAdapter.getCard(MainActivity.position).check(HabitActivity.this);
            }
        });
    }
}
