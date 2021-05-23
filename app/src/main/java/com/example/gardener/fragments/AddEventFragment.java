package com.example.gardener.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.MainActivity;
import com.example.gardener.R;
import com.example.gardener.ReminderBroadcast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddEventFragment extends Fragment {
    private View mainView;
    private NestedScrollView background;
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

    private Button add_button;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private boolean theme_dark = true;

    private int selected_year = Calendar.getInstance().get(Calendar.YEAR);
    private int selected_month = Calendar.getInstance().get(Calendar.MONTH);
    private int selected_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private int selected_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    private int selected_minute = Calendar.getInstance().get(Calendar.MINUTE);

    private AdView ad_block;

    public AddEventFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.add_event_fragment_layout, container, false);
        init();
        settings = (getContext()).getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        applyTheme();
        createNotificationChannel();
        TurnOffBNV();
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }else{
            showAds();
        }
        Configure();
        return mainView;
    }

    private void init(){
        button_back = mainView.findViewById(R.id.add_fragment_back_arrow);
        button_back.setOnClickListener(OnBackArrowPressedListener());
        background = mainView.findViewById(R.id.add_fragment_back);

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

        add_button = mainView.findViewById(R.id.add_fragment_add_button);
        add_button.setOnClickListener(OnAddEventListener());
    }

    private void Configure(){
        Calendar temp_calendar = Calendar.getInstance();
        date_text.setText(new SimpleDateFormat("dd.MM.yyyy").format(temp_calendar.getTime()));
        time_text.setText(new SimpleDateFormat("HH:mm").format(temp_calendar.getTime()));
    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.add_event_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private View.OnClickListener OnBackArrowPressedListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TurnOnBNV();
                (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarFragment(db)).commit();
            }
        };
    }

    private void applyTheme(){
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

    private View.OnClickListener OnChangeEventDateListener(){
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

    private View.OnClickListener OnSwitchNotificationListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.findViewById(R.id.time_set_line).setVisibility(notify_switch.isChecked() ? View.VISIBLE : View.GONE);
            }
        };
    }

    private View.OnClickListener OnChangeEventTimeListener(){
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

    private View.OnClickListener OnAddEventListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                String name = name_text.getText().toString();
                if (!name.equals("")){
                    contentValues.put(Const.EVENT_NAME, name);
                    String desc = desc_text.getText().toString();
                    contentValues.put(Const.EVENT_DESC, desc);
                    String date = date_text.getText().toString();
                    contentValues.put(Const.EVENT_DATE, date);
                    if (notify_switch.isChecked()){
                        contentValues.put(Const.EVENT_IS_CHECKED, 1);
                        String time = time_text.getText().toString();
                        contentValues.put(Const.EVENT_TIME, time);
                    }else {
                        contentValues.put(Const.EVENT_IS_CHECKED, 0);
                        contentValues.put(Const.EVENT_TIME, "10:00");
                    }
                    db.insert(Const.TABLE_EVENT_LIST, null, contentValues);

                    if (notify_switch.isChecked() && DateIsBeforeThis(date, time_text.getText().toString())){
                        CreateNotification(getLatestIdFromDB());
                    }
                    (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarFragment(db)).commit();
                    TurnOnBNV();
                    Toast.makeText(getContext(), getString(R.string.result_added_string), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), getResources().getString(R.string.toast_on_dismiss_text), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private boolean DateIsBeforeThis(String date, String time){
        int year = Integer.parseInt(date.substring(6));
        int month = Integer.parseInt(date.substring(3,5));
        int day = Integer.parseInt(date.substring(0, 2));
        int hour = Integer.parseInt(time.substring(0,2));
        int minute = Integer.parseInt(time.substring(3));
        Calendar temp_cal = Calendar.getInstance();
        temp_cal.set(Calendar.YEAR, year);
        temp_cal.set(Calendar.MONTH, month - 1);
        temp_cal.set(Calendar.DAY_OF_MONTH, day);
        temp_cal.set(Calendar.HOUR_OF_DAY, hour);
        temp_cal.set(Calendar.MINUTE, minute);
        temp_cal.set(Calendar.SECOND, 0);
        if (Calendar.getInstance().before(temp_cal)){
            return true;
        }else {
            return false;
        }
    }

    private String FormatDateToString(String template, Date date){
        return new SimpleDateFormat(template).format(date);
    }

    private void createNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = (getActivity()).getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void CreateNotification(int db_id){
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

    private void TurnOffBNV(){
        MainActivity.bottom_menu.setVisibility(View.GONE);
    }

    private void TurnOnBNV(){
        MainActivity.bottom_menu.setVisibility(View.VISIBLE);
    }

    private int getLatestIdFromDB(){
        Cursor temp = db.query(Const.TABLE_EVENT_LIST, null, null, null, null, null, null);
        temp.moveToLast();
        //System.err.println(temp.getInt(0) + "\n\n\n");
        return temp.getInt(0);
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
