package com.example.grant.projectmoheth;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
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
    public static AlarmManager alarmManager;
    private RecyclerView recyclerView;
    private Resources r;
    public static final String CARD_FILE = "com.example.grant.projectmoheth.FileName";
    public static final int ALARM_REQUEST_CODE = 2;
    private static int selectedHour;
    private static int selectedMinute;
    private static ArrayList<Integer> selectedDay;
    private Theme theme;
    private boolean selected = false;
    public static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.theme = Utils.getCurrentTheme(this);

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

                // may need to be changed back to returning true
                return false;
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

                selectedDay = new ArrayList<>();

                Spinner daySpinner = (Spinner) v.findViewById(R.id.daySpinner);
                daySpinner.setAdapter(new ArrayAdapter<String>(view.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, days));

                daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 8) {
                            // wipes the current selectedDay to avoid duplicates
                            selectedDay = new ArrayList<>();
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
                                    if (sundayCB.isChecked())
                                        selectedDay.add(0);
                                    if (mondayCB.isChecked())
                                        selectedDay.add(1);
                                    if (tuesdayCB.isChecked())
                                        selectedDay.add(2);
                                    if (wednesdayCB.isChecked())
                                        selectedDay.add(3);
                                    if (thursdayCB.isChecked())
                                        selectedDay.add(4);
                                    if (fridayCB.isChecked())
                                        selectedDay.add(5);
                                    if (saturdayCB.isChecked())
                                        selectedDay.add(6);
                                }
                            });

                            String str = new String();
                            for (int i = 0; i < selectedDay.size(); i++) {
                                str += selectedDay.get(i) + "";
                            }

                            System.out.println(str);

                            dayDialog.create();

                            dayDialog.show();
                        } else {
                            // wipes the current selectedDay to avoid duplicates
                            selectedDay = new ArrayList<>();

                            selectedDay.add(position);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                dialog.setTitle(R.string.dialog_title);
                dialog.setView(v);
                // may have to set this to false
                dialog.setCancelable(true);
                dialog.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog alertDialog = dialog.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (nameET.getText().toString().length() != 0 &&
                                        descriptionET.getText().toString().length() != 0) {
                                    cardAdapter.addCard(nameET.getText().toString(),
                                            descriptionET.getText().toString(), selectedHour, selectedMinute,
                                            selectedDay);
                                    cardAdapter.notifyItemInserted(cardAdapter.getItemCount() + 1);

                                    cardAdapter.checkList();

                                    alertDialog.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            MainActivity.this.getString(R.string.empty_fields),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                alertDialog.show();
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

            v.nameTextView.setText(ci.getTruncatedName());
            v.timeTextView.setText(ci.hour + ((ci.minute < 10) ? ":0" + ci.minute : ":" + ci.minute));
            v.dayTextView.setText(ci.getSelectedDayName(MainActivity.this));
        }

        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.card_view, parent, false);

            return new CardViewHolder(itemView);
        }

        public void addCard(String name, String description, int hour, int minute,
                            ArrayList<Integer> selectedDay) {
            this.cardInfoList.add(new CardInfo(name, description, hour, minute, selectedDay));

            // save the list here
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sp.edit();

            // converts the CardInfo list into a JSON String
            String json = new Gson().toJson(this.cardInfoList);

            editor.putString(CARD_FILE, json);

            editor.apply();

            // TODO add the broadcast to each habit
            Intent i = new Intent(MainActivity.this, AlarmReceiver.class);
            i.putExtra("com.example.grant.projectmoheth.alarmCardInfo",
                    new Gson().toJson(this.cardInfoList.get(this.cardInfoList.size() - 1)));
            i.setAction("com.example.grant.projectmoheth.notification");
            PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, ALARM_REQUEST_CODE, i,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            if (alarmManager == null) {
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            }

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pi);
        }

        public void removeCard() {
            // TODO make sure that this actually removes the alarm from the habit
            /*
            Intent i = new Intent(MainActivity.this, AlarmReceiver.class);
            i.putExtra("com.example.grant.projectmoheth.alarmCardInfo",
                    new Gson().toJson(this.cardInfoList.get(position)));
            i.setAction("com.example.grant.projectmoheth.notification");

            if (alarmManager == null)
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            alarmManager.cancel(PendingIntent.getBroadcast(MainActivity.this, ALARM_REQUEST_CODE, i,
                    PendingIntent.FLAG_UPDATE_CURRENT));
                    */

            this.cardInfoList.remove(position);

            cardAdapter.checkList();

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sp.edit();

            String json = new Gson().toJson(this.cardInfoList);

            editor.putString(CARD_FILE, json);

            editor.apply();
        }

        public void editCard(String name, String description, int hour, int minute,
                             ArrayList<Integer> selectedDay) {
            this.cardInfoList.get(position).name = name;
            this.cardInfoList.get(position).description = description;
            this.cardInfoList.get(position).hour = hour;
            this.cardInfoList.get(position).minute = minute;
            this.cardInfoList.get(position).selectedDay = selectedDay;

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sp.edit();

            String json = new Gson().toJson(this.cardInfoList);

            editor.putString(CARD_FILE, json);

            editor.apply();

            // TODO find a way to refresh the activity without having the activity start animation
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

            if (this.getItemCount() == 0)
                ifEmptyTextView.setVisibility(View.VISIBLE);
            else
                ifEmptyTextView.setVisibility(View.GONE);
        }
    }

    public ArrayList<CardInfo> loadList() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String json = sp.getString(CARD_FILE, null);

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
            // hides the overflow menu
            MenuItem si = menu.findItem(R.id.action_settings);
            si.setVisible(false);
            MenuItem ni = menu.findItem(R.id.notification_item);
            ni.setVisible(false);

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
            // shows the overflow menu
            MenuItem si = menu.findItem(R.id.action_settings);
            si.setVisible(true);
            MenuItem ni = menu.findItem(R.id.notification_item);
            ni.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, Settings.class);

            this.startActivity(i);
        } else if (id == R.id.notification_item) {
            Calendar calendar = Calendar.getInstance();

            CardInfo test = new CardInfo(r.getString(R.string.test), "Description",
                    calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
                    new ArrayList<Integer>());

            new MyNotification(MainActivity.this, test);
        } else if (id == R.id.edit_item) {
            // TODO get the time and day equal to the oldCardInfo time and day
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

            selectedDay = new ArrayList<>();

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
                                if (sundayCB.isChecked())
                                    selectedDay.add(0);
                                if (mondayCB.isChecked())
                                    selectedDay.add(1);
                                if (tuesdayCB.isChecked())
                                    selectedDay.add(2);
                                if (wednesdayCB.isChecked())
                                    selectedDay.add(3);
                                if (thursdayCB.isChecked())
                                    selectedDay.add(4);
                                if (fridayCB.isChecked())
                                    selectedDay.add(5);
                                if (saturdayCB.isChecked())
                                    selectedDay.add(6);
                            }
                        });

                        dayDialog.create();

                        dayDialog.show();
                    } else {
                        selectedDay.add(position);
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

            final AlertDialog alertDialog = dialog.create();
            alertDialog.setOnShowListener(new AlertDialog.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (nameET.getText().toString().length() != 0 &&
                                    descriptionET.getText().toString().length() != 0) {
                                cardAdapter.editCard(nameET.getText().toString(),
                                        descriptionET.getText().toString(), selectedHour, selectedMinute,
                                        selectedDay);

                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        MainActivity.this.getString(R.string.empty_fields),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });

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

        if (this.theme != Utils.getCurrentTheme(this))
            Utils.changeTheme(this, Utils.getCurrentTheme(this));
    }
}