package com.example.gardener.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.MainActivity;
import com.example.gardener.R;
import com.example.gardener.ReminderBroadcast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ChangeEventFragment extends Fragment {
    private View mainView;
    private RelativeLayout background;
    private SQLiteDatabase db;

    private ImageButton button_back;

    private TextView date_title;
    private TextView date_text;
    private Button change_date;

    private  TextView name_title;
    private EditText name_text;

    private TextView desc_title;
    private EditText desc_text;

    private TextView notify_title;
    private Switch notify_switch;

    private TextView time_title;
    private TextView time_text;
    private Button time_change;

    private Button change_button;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private boolean theme_dark = true;

    private int selected_year = Calendar.getInstance().get(Calendar.YEAR);
    private int selected_month = Calendar.getInstance().get(Calendar.MONTH);
    private int selected_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private int selected_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    private int selected_minute = Calendar.getInstance().get(Calendar.MINUTE);


    private int input_id;
    private String input_title;
    private String input_desc;
    private String input_date;
    private String input_time;
    private boolean input_is_checked;
    private AdView ad_block;

    public ChangeEventFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.change_event_fragment_layout, container, false);
        init();
        Configure();
        settings = (getContext()).getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        applyTheme();
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }else {
            showAds();
        }
        return mainView;
    }

    private void init(){
        button_back = mainView.findViewById(R.id.change_fragment_back_arrow);
        button_back.setOnClickListener(OnBackArrowPressedListener());
        background = mainView.findViewById(R.id.change_fragment_back);

        date_title = mainView.findViewById(R.id.event_date_title_text);
        date_text = mainView.findViewById(R.id.event_date_text);
        change_date = mainView.findViewById(R.id.change_date_button);
        change_date.setOnClickListener(OnChangeEventDateListener());

        name_title = mainView.findViewById(R.id.event_name_text_title);
        name_text = mainView.findViewById(R.id.event_name_edit);

        desc_title = mainView.findViewById(R.id.event_desc_text_title);
        desc_text = mainView.findViewById(R.id.event_desc_edit);

        notify_title = mainView.findViewById(R.id.event_notification_text_title);
        notify_switch = mainView.findViewById(R.id.event_notify_switch);
        notify_switch.setOnClickListener(OnSwitchNotificationListener());

        time_title = mainView.findViewById(R.id.event_time_title_text);
        time_text = mainView.findViewById(R.id.event_time_text);
        time_change = mainView.findViewById(R.id.event_change_time_button);
        time_change.setOnClickListener(OnChangeEventTimeListener());

        change_button = mainView.findViewById(R.id.change_fragment_change_button);
        change_button.setOnClickListener(OnChangeEventListener());
    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.change_event_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private void Configure(){
        Bundle bundle = getArguments();
        input_id = bundle.getInt(Const.EVENT_ID);
        input_title = bundle.getString(Const.EVENT_NAME);
        name_text.setText(input_title);
        input_desc = bundle.getString(Const.EVENT_DESC);
        desc_text.setText(input_desc);
        input_date = bundle.getString(Const.EVENT_DATE);
        date_text.setText(input_date);
        input_time = bundle.getString(Const.EVENT_TIME);
        time_text.setText(input_time);
        input_is_checked = bundle.getBoolean(Const.EVENT_IS_CHECKED);
        notify_switch.setChecked(input_is_checked);
        if (input_is_checked){
            mainView.findViewById(R.id.time_set_line).setVisibility(View.VISIBLE);
        }else {
            mainView.findViewById(R.id.time_set_line).setVisibility(View.GONE);
        }
    }

    private void applyTheme() {
        boolean is_dark = settings.getBoolean(Const.APP_THEME, true);
        int back_color = Color.BLACK;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
            theme_dark = false;
        }
        background.setBackgroundColor(back_color);
        date_title.setTextColor(text_color);
        name_title.setTextColor(text_color);
        name_text.setTextColor(text_color);
        name_text.setHintTextColor(Color.DKGRAY);
        desc_title.setTextColor(text_color);
        desc_text.setTextColor(text_color);
        desc_text.setTextColor(text_color);
        desc_text.setHintTextColor(Color.DKGRAY);
        notify_title.setTextColor(text_color);
        time_title.setTextColor(text_color);
    }

    private View.OnClickListener OnChangeEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp_name = name_text.getText().toString();
                if (!temp_name.equals("")){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Const.EVENT_NAME, temp_name);
                    String temp_desc = desc_text.getText().toString();
                    contentValues.put(Const.EVENT_DESC, temp_desc);
                    String temp_date = date_text.getText().toString();
                    contentValues.put(Const.EVENT_DATE, temp_date);
                    if (notify_switch.isChecked()){
                        contentValues.put(Const.EVENT_IS_CHECKED, 1);
                        String temp_time = time_text.getText().toString();
                        contentValues.put(Const.EVENT_TIME, temp_time);
                        if (input_is_checked){
                            CancelNotification(input_id);
                        }
                         CreateNotification(input_id);
                    }else{
                        if (input_is_checked){
                            CancelNotification(input_id);
                        }
                        contentValues.put(Const.EVENT_IS_CHECKED, 0);
                        contentValues.put(Const.EVENT_TIME, "10:00");
                    }
                    String selection = Const.EVENT_ID + "= '" + input_id + "'";
                    db.update(Const.TABLE_EVENT_LIST, contentValues, selection, null);
                    InfoEventFragment fragment = new InfoEventFragment(db);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Const.EVENT_ID, input_id);
                    bundle.putString(Const.EVENT_NAME, name_text.getText().toString());
                    bundle.putString(Const.EVENT_DESC, desc_text.getText().toString());
                    bundle.putString(Const.EVENT_DATE, date_text.getText().toString());
                    bundle.putString(Const.EVENT_TIME, time_text.getText().toString());
                    bundle.putBoolean(Const.EVENT_IS_CHECKED, notify_switch.isChecked());
                    fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    Toast.makeText(getContext(), R.string.result_changed_string, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), R.string.toast_on_dismiss_text, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private View.OnClickListener OnChangeEventTimeListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar temp = Calendar.getInstance();
                int current_theme = R.style.DialogThemeLight;
                new TimePickerDialog(getContext(), current_theme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selected_hour = hourOfDay;
                        selected_minute = minute;
                        temp.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        temp.set(Calendar.MINUTE, minute);
                        time_text.setText(FormatDateToString("HH:mm", temp.getTime()));
                    }
                }, temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.MINUTE), true).show();
            }
        };
    }

    private View.OnClickListener OnSwitchNotificationListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.findViewById(R.id.time_set_line).setVisibility(notify_switch.isChecked()?View.VISIBLE:View.GONE);
            }
        };
    }

    private View.OnClickListener OnChangeEventDateListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar temp = Calendar.getInstance();
                int current_theme = R.style.DialogThemeLight;
                new DatePickerDialog(getContext(), current_theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selected_year = year;
                        selected_month = month;
                        selected_day = dayOfMonth;
                        temp.set(Calendar.YEAR, year);
                        temp.set(Calendar.MONTH, month);
                        temp.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        date_text.setText(FormatDateToString("dd.MM.yyyy",temp.getTime()));
                    }
                }, temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DAY_OF_MONTH)).show();
            }
        };
    }

    private View.OnClickListener OnBackArrowPressedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoEventFragment fragment = new InfoEventFragment(db);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.EVENT_ID, input_id);
                bundle.putString(Const.EVENT_NAME, input_title);
                bundle.putString(Const.EVENT_DESC, input_desc);
                bundle.putString(Const.EVENT_DATE, input_date);
                bundle.putString(Const.EVENT_TIME, input_time);
                bundle.putBoolean(Const.EVENT_IS_CHECKED, input_is_checked);
                fragment.setArguments(bundle);
                (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        };
    }

    private String FormatDateToString(String template, Date date){
        return new SimpleDateFormat(template).format(date);
    }

    private void CancelNotification(int db_id){
        //System.err.println(db_id + "\n\n\n");
        Cursor temp_cursor = db.query(Const.TABLE_EVENT_LIST, null, Const.EVENT_ID + "= '" + db_id + "'", null, null, null, null);
        temp_cursor.moveToFirst();
        String title = temp_cursor.getString(1);
        String desc = temp_cursor.getString(2);
        String date = temp_cursor.getString(4);
        String time = temp_cursor.getString(5);

        Intent intent = new Intent(getContext(), ReminderBroadcast.class);
        intent.putExtra("title", title);
        intent.putExtra("desc", desc);
        intent.putExtra("date", date);
        intent.putExtra("time", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), db_id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) (getActivity()).getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

    private void CreateNotification(int db_id){
        //System.err.println(db_id);
        Cursor temp_cursor = db.query(Const.TABLE_EVENT_LIST, null, Const.EVENT_ID + "= '" + db_id + "'", null, null, null, null);
        temp_cursor.moveToFirst();
        String title = temp_cursor.getString(1);
        String desc = temp_cursor.getString(2);
        String date = temp_cursor.getString(4);
        String time = temp_cursor.getString(5);

        Intent intent = new Intent(getContext(), ReminderBroadcast.class);
        intent.putExtra("title", title);
        intent.putExtra("desc", desc);
        intent.putExtra("date", date);
        intent.putExtra("time", time);

        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.YEAR, Integer.parseInt(date.substring(6)));
        temp.set(Calendar.MONTH, Integer.parseInt(date.substring(3,5))-1);
        temp.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0,2)));
        temp.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0,2)));
        temp.set(Calendar.MINUTE, Integer.parseInt(time.substring(3)));
        temp.set(Calendar.SECOND, 0);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), db_id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) (getActivity()).getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, temp.getTimeInMillis(), pendingIntent);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}
