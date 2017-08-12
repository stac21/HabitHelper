package com.example.grant.projectmoheth;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private CardAdapter cardAdapter;
    private static final String FILE_NAME = "MainActivityFile";
    private static int selectedHour;
    private static int selectedMinute;
    private static String selectedDay;
    private boolean launching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.launching = true;

        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Resources r = getResources();

        final String[] times = r.getStringArray(R.array.time_spinner_array);
        final String[] days = r.getStringArray(R.array.day_spinner_array);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        this.cardAdapter = new CardAdapter(this.loadList());

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(this.cardAdapter);

        cardAdapter.checkList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                View v = getLayoutInflater().inflate(R.layout.dialog, null);

                final EditText nameET = (EditText) v.findViewById(R.id.nameET);
                final EditText descriptionET = (EditText) v.findViewById(R.id.descriptionET);
                final Button colorButton = (Button) v.findViewById(R.id.colorButton);

                Spinner timeSpinner = (Spinner) v.findViewById(R.id.timeSpinner);
                timeSpinner.setAdapter(new ArrayAdapter<String>(view.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, times));

                timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 4) {
                            Calendar currentTime = Calendar.getInstance();
                            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = currentTime.get(Calendar.MINUTE);

                            final TimePickerDialog tmDialog = new TimePickerDialog(
                                    MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int userMinute) {
                                    selectedHour = hourOfDay;
                                    selectedMinute = userMinute;
                                }
                            }, hour, minute, true);

                            tmDialog.setTitle("Select Time");
                            tmDialog.show();
                        } else {
                            selectedHour = Integer.parseInt(times[position].substring(0, 2));
                            selectedMinute = 0;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                Spinner daySpinner = (Spinner) v.findViewById(R.id.daySpinner);
                daySpinner.setAdapter(new ArrayAdapter<String>(view.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, days));

                daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 8) {
                            AlertDialog.Builder dayDialog = new AlertDialog.Builder(MainActivity.this);
                            View vw = getLayoutInflater().inflate(R.layout.day_dialog, null);
                            final CheckBox sundayCB = (CheckBox) vw.findViewById(R.id.sundayCB);
                            final CheckBox mondayCB = (CheckBox) vw.findViewById(R.id.mondayCB);
                            final CheckBox tuesdayCB = (CheckBox) vw.findViewById(R.id.tuesdayCB);
                            final CheckBox wednesdayCB = (CheckBox) vw.findViewById(R.id.wednesdayCB);
                            final CheckBox thursdayCB = (CheckBox) vw.findViewById(R.id.thursdayCB);
                            final CheckBox fridayCB = (CheckBox) vw.findViewById(R.id.fridayCB);
                            final CheckBox saturdayCB = (CheckBox) vw.findViewById(R.id.saturdayCB);

                            dayDialog.setTitle(R.string.day_dialog_title);
                            dayDialog.setView(vw);
                            dayDialog.setCancelable(false);
                            dayDialog.setPositiveButton(R.string.day_dialog_yes_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectedDay = "";

                                    if (sundayCB.isChecked())
                                        selectedDay += getApplicationContext().
                                                getString(R.string.day_dialog_sunday_string) + " ";
                                    if (mondayCB.isChecked())
                                        selectedDay += getApplicationContext().
                                                getString(R.string.day_dialog_monday_string) + " ";
                                    if (tuesdayCB.isChecked())
                                        selectedDay += getApplicationContext().
                                                getString(R.string.day_dialog_tuesday_string) + " ";
                                    if (wednesdayCB.isChecked())
                                        selectedDay += getApplicationContext().
                                                getString(R.string.day_dialog_wednesday_string) + " ";
                                    if (thursdayCB.isChecked())
                                        selectedDay += getApplicationContext().
                                                getString(R.string.day_dialog_thursday_string) + " ";
                                    if (fridayCB.isChecked())
                                        selectedDay += getApplicationContext().
                                                getString(R.string.day_dialog_friday_string) + " ";
                                    if (saturdayCB.isChecked())
                                        selectedDay += getApplicationContext().
                                                getString(R.string.day_dialog_saturday_string) + " ";
                                }
                            });

                            dayDialog.create();

                            dayDialog.show();
                        } else {
                            selectedDay = days[position];
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                dialog.setTitle(R.string.dialog_title);
                dialog.setView(v);
                dialog.setCancelable(false);
                dialog.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cardAdapter.addCard(nameET.getText().toString(),
                                descriptionET.getText().toString(), selectedHour, selectedMinute,
                                selectedDay);
                        cardAdapter.notifyItemInserted(cardAdapter.getItemCount() + 1);

                        cardAdapter.checkList();
                    }
                });
                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialog.create();

                dialog.show();
            }
        });
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,
        View.OnClickListener {
        protected TextView nameTextView, timeTextView, dayTextView;

        public CardViewHolder(View v) {
            super(v);

            this.nameTextView = (TextView) v.findViewById(R.id.nameTextView);
            this.timeTextView = (TextView) v.findViewById(R.id.timeTextView);
            this.dayTextView = (TextView) v.findViewById(R.id.dayTextView);
            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, HabitActivity.class);

            i.putExtra("title", this.nameTextView.getText());

            MainActivity.this.startActivity(i);
        }
        @Override
        public boolean onLongClick(View v) {
            // TODO create the actions on the toolbar
            Snackbar.make(v, "Long Clicked", Snackbar.LENGTH_SHORT).show();

            return false;
        }
    }

    public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private ArrayList<CardInfo> cardInfoList;

        public CardAdapter(ArrayList<CardInfo> cardInfoList) {
            if (cardInfoList != null)
                this.cardInfoList = cardInfoList;
            else
                this.cardInfoList = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return this.cardInfoList.size();
        }

        public void onBindViewHolder(CardViewHolder v, int i) {
            CardInfo ci = this.cardInfoList.get(i);

            v.nameTextView.setText(ci.name);
            v.timeTextView.setText(ci.hour + ((ci.minute < 10) ? ":0" + ci.minute : ":" + ci.minute));
            v.dayTextView.setText(ci.selectedDay);
        }

        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.card_view, parent, false);

            return new CardViewHolder(itemView);
        }

        public void addCard(String name, String description, int hour, int minute, String selectedDay) {
            this.cardInfoList.add(new CardInfo(name, description, hour, minute, selectedDay));

            // save the list here
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sp.edit();

            // converts the CardInfo list into a JSON String
            String json = new Gson().toJson(this.cardInfoList);

            editor.putString(FILE_NAME, json);

            editor.apply();
        }

        public void checkList() {
            // bad name but exptyTextView is worse
            TextView ifEmptyTextView = (TextView) findViewById(R.id.ifEmptyTextView);

            if (this.getItemCount() == 0 && ifEmptyTextView.getVisibility() == View.GONE)
                ifEmptyTextView.setVisibility(View.VISIBLE);
            else
                ifEmptyTextView.setVisibility(View.GONE);
        }
    }

    public ArrayList<CardInfo> loadList() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String json = sp.getString(FILE_NAME, null);

        Type collectionType = new TypeToken<ArrayList<CardInfo>>(){}.getType();
        ArrayList<CardInfo> cardInfoList = new Gson().fromJson(json, collectionType);

        return cardInfoList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem mi = menu.findItem(R.id.extraButton);
        mi.setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, Settings.class);

            this.startActivity(i);
        } else if (id == R.id.temp_item) {
            new MyNotification(MainActivity.this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.launching)
            this.launching = false;
        else if (!this.launching)
            Utils.changeTheme(this, Utils.getCurrentTheme(MainActivity.this));
    }
}