package com.example.grant.projectmoheth;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
    public static CardAdapter cardAdapter;
    private AlarmManager alarmManager;
    private RecyclerView recyclerView;
    private Resources r;
    private static final String FILE_NAME = "MainActivityFile";
    private static final int ALARM_REQUEST_CODE = 2;
    private static int selectedHour;
    private static int selectedMinute;
    private static String selectedDay;
    private boolean launching = false;
    private boolean selected = false;
    public static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.launching = true;

        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        r = getResources();

        final String[] times = r.getStringArray(R.array.time_spinner_array);
        final String[] days = r.getStringArray(R.array.day_spinner_array);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        this.cardAdapter = new CardAdapter(this.loadList());

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(this.cardAdapter);

        ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                cardAdapter.moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        ith.attachToRecyclerView(recyclerView);

        cardAdapter.checkList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                View v = getLayoutInflater().inflate(R.layout.dialog, null);

                final EditText nameET = (EditText) v.findViewById(R.id.nameET);
                final EditText descriptionET = (EditText) v.findViewById(R.id.descriptionET);

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
            CardInfo cardInfo = cardAdapter.getCard(this.getLayoutPosition());
            position = this.getAdapterPosition();

            i.putExtra("com.example.grant.projectmoheth.card", new Gson().toJson(cardInfo));

            MainActivity.this.startActivity(i);
        }

        @Override
        public boolean onLongClick(View v) {
            selected = true;
            position = this.getAdapterPosition();

            MainActivity.this.invalidateOptionsMenu();

            return true;
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

        public CardInfo getCard(int position) {
            return this.cardInfoList.get(position);
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
            Calendar calendar = Calendar.getInstance();
            int dateCreated = calendar.get(Calendar.DAY_OF_YEAR) + calendar.get(Calendar.YEAR);

            this.cardInfoList.add(new CardInfo(name, description, hour, minute, dateCreated,
                    selectedDay));

            // save the list here
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sp.edit();

            // converts the CardInfo list into a JSON String
            String json = new Gson().toJson(this.cardInfoList);

            editor.putString(FILE_NAME, json);

            editor.apply();

            // TODO add the broadcast to each habit
            Intent i = new Intent(MainActivity.this, AlarmReceiver.class);
            i.putExtra("com.example.grant.projectmoheth.alarmCardInfo",
                    new Gson().toJson(this.cardInfoList.get(this.cardInfoList.size() - 1)));
            i.setAction("com.example.grant.projectmoheth.notification");
            PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, ALARM_REQUEST_CODE, i,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pi);
        }

        public void removeCard() {
            // TODO make sure that this actually removes the alarm from the habit
            Intent i = new Intent(MainActivity.this, AlarmReceiver.class);
            i.putExtra("com.example.grant.projectmoheth.alarmCardInfo",
                    new Gson().toJson(this.cardInfoList.get(position)));
            i.setAction("com.example.grant.projectmoheth.notification");
            alarmManager.cancel(PendingIntent.getBroadcast(MainActivity.this, ALARM_REQUEST_CODE, i,
                    PendingIntent.FLAG_UPDATE_CURRENT));

            this.cardInfoList.remove(position);

            cardAdapter.checkList();

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sp.edit();

            String json = new Gson().toJson(this.cardInfoList);

            editor.putString(FILE_NAME, json);

            editor.apply();

            // if the app crashes when a habit is removed, this is the culprit
            //Snackbar.make(null, MainActivity.this.getString(R.string.habit_deleted),
                  //  Snackbar.LENGTH_SHORT);
        }

        public void editCard(String name, String description, int hour, int minute, String selectedDay) {
            this.cardInfoList.get(position).name = name;
            this.cardInfoList.get(position).description = description;
            this.cardInfoList.get(position).hour = hour;
            this.cardInfoList.get(position).minute = minute;
            this.cardInfoList.get(position).selectedDay = selectedDay;

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sp.edit();

            String json = new Gson().toJson(this.cardInfoList);

            editor.putString(FILE_NAME, json);

            editor.apply();

            MainActivity.this.finish();
            startActivity(new Intent(MainActivity.this, MainActivity.class));

            // TODO edit the card's PendingIntent to set off an alarm at the changed time
        }

        public void moveItem(int oldPos, int newPos) {
            position = newPos;
            CardInfo temp = this.cardInfoList.get(oldPos);

            this.cardInfoList.remove(oldPos);
            this.cardInfoList.add(newPos, temp);
            this.notifyItemMoved(oldPos, newPos);
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
        if (this.selected) {
            MenuItem ti = menu.findItem(R.id.trash_item);
            ti.setVisible(true);
            MenuItem ei = menu.findItem(R.id.edit_item);
            ei.setVisible(true);
            MenuItem ci = menu.findItem(R.id.clear_item);
            ci.setVisible(true);
            // TODO hide the overflow menu and show it again after the menu is invalidated

            if (Utils.getCurrentTheme(MainActivity.this).equals(Theme.LIGHT_THEME)) {
                ti.setIcon(R.drawable.ic_delete_black_24dp);
                ei.setIcon(R.drawable.ic_edit_black_24dp);
                ci.setIcon(R.drawable.ic_clear_black_24dp);
            } else {
                ti.setIcon(R.drawable.ic_delete_white_24dp);
                ei.setIcon(R.drawable.ic_edit_white_24dp);
                ci.setIcon(R.drawable.ic_clear_white_24dp);
            }

            this.selected = false;
        } else {
            MenuItem ti = menu.findItem(R.id.trash_item);
            ti.setVisible(false);
            MenuItem ei = menu.findItem(R.id.edit_item);
            ei.setVisible(false);
            MenuItem ci = menu.findItem(R.id.clear_item);
            ci.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO implement the edit_item functionality
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, Settings.class);

            this.startActivity(i);
        } else if (id == R.id.temp_item) {
            CardInfo test = new CardInfo("Test", "Description", 12, 0, Calendar.DAY_OF_YEAR, "Penis");

            new MyNotification(MainActivity.this, test);
        } else if (id == R.id.edit_item) {
            final CardInfo oldCardInfo = cardAdapter.getCard(position);

            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

            View v = getLayoutInflater().inflate(R.layout.dialog, null);

            final String[] times = r.getStringArray(R.array.time_spinner_array);
            final String[] days = r.getStringArray(R.array.day_spinner_array);

            final EditText nameET = (EditText) v.findViewById(R.id.nameET);
            nameET.setText(oldCardInfo.name);
            final EditText descriptionET = (EditText) v.findViewById(R.id.descriptionET);
            descriptionET.setText(oldCardInfo.description);

            Spinner timeSpinner = (Spinner) v.findViewById(R.id.timeSpinner);
            timeSpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,
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

                        tmDialog.setTitle(r.getString(R.string.time_dialog_title));
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

            String selection = (String) timeSpinner.getSelectedItem();
            Toast.makeText(MainActivity.this, selection, Toast.LENGTH_LONG).show();

            Spinner daySpinner = (Spinner) v.findViewById(R.id.daySpinner);
            daySpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,
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
            dialog.setPositiveButton(R.string.day_dialog_yes_button,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cardAdapter.editCard(nameET.getText().toString(),
                            descriptionET.getText().toString(), selectedHour, selectedMinute,
                            selectedDay);
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

            this.selected = false;
            MainActivity.this.invalidateOptionsMenu();
        } else if (id == R.id.trash_item) {
            this.cardAdapter.removeCard();

            this.cardAdapter.notifyItemRemoved(this.position);
            this.cardAdapter.notifyItemRangeChanged(this.position, this.cardAdapter.getItemCount());

            this.selected = false;
            MainActivity.this.invalidateOptionsMenu();
        } else if (id == R.id.clear_item) {
            this.selected = false;
            MainActivity.this.invalidateOptionsMenu();
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