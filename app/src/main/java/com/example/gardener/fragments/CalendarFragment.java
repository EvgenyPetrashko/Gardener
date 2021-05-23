package com.example.gardener.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.Const;
import com.example.gardener.EventItem;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.PaidContentActivity;
import com.example.gardener.R;
import com.example.gardener.adapters.EventAdapter;
import com.example.gardener.intefaces.OnEventClickListener;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarFragment extends Fragment implements OnEventClickListener {
    private CompactCalendarView calendar;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM  yyyy", Locale.getDefault());
    private TextView month_title;
    private ImageButton left_month_button;
    private ImageButton right_month_button;

    private CardView no_events_card;

    private FloatingActionButton fab_event;
    private FloatingActionButton fab_moon;

    private RecyclerView rv;
    private SQLiteDatabase db;
    private Date current_date = Calendar.getInstance().getTime();
    private SharedPreferences settings;

    private RelativeLayout back;

    private View mainView;


    private int theme = 1;
    public CalendarFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.calendar_fragment_layout, container, false);
        settings = getContext().getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        init();
        InitializeArrayEvent();
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }
        return mainView;
    }

    private void init() {
        calendar = mainView.findViewById(R.id.calendar);

        back = mainView.findViewById(R.id.calendar_fragment_back);
        applyTheme();

        month_title = mainView.findViewById(R.id.month_title_text);
        left_month_button = mainView.findViewById(R.id.month_left_button);
        right_month_button = mainView.findViewById(R.id.month_right_button);
        fab_event = mainView.findViewById(R.id.fab_add_event);
        fab_moon = mainView.findViewById(R.id.moon_calendar_button);
        rv = mainView.findViewById(R.id.event_recycler);
        no_events_card = mainView.findViewById(R.id.card_no_events);

        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        month_title.setText(dateFormatForMonth.format(Calendar.getInstance().getTime()).toUpperCase());


        CalendarConfigure();

        left_month_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.scrollLeft();
            }
        });

        right_month_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.scrollRight();
            }
        });

        fab_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEventFragment fragment = new AddEventFragment(db);
                (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                calendar.performClick();
            }
        });

        fab_moon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMoonDialog();
            }
        });


    }

    private void applyTheme() {
        boolean is_dark = settings.getBoolean(Const.APP_THEME, true);
        if (is_dark){
            back.setBackgroundColor(Color.BLACK);
            theme = 1;
        }else{
            back.setBackgroundColor(Color.WHITE);
            theme = 0;
        }

        applyThemeCalendar(is_dark);
        applyThemeNoEventsCard(is_dark);

    }

    private void applyThemeNoEventsCard(boolean is_dark) {
        int back_color = Color.DKGRAY;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
        }
        ((TextView)mainView.findViewById(R.id.card_no_events_text_title)).setTextColor(text_color);
        ((LinearLayout)mainView.findViewById(R.id.card_no_events_background)).setBackgroundColor(back_color);
    }

    private void applyThemeCalendar(boolean is_dark) {
        int back_color = Color.BLACK;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
        }
        //calendar.setCalendarBackgroundColor(back_color);
    }

    private void CalendarConfigure(){
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        calendar.setUseThreeLetterAbbreviation(true);

        calendar.shouldDrawIndicatorsBelowSelectedDays(true);

        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                SetEventsToAdapter(dateClicked);
                current_date = dateClicked;

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

                month_title.setText(dateFormatForMonth.format(firstDayOfNewMonth).toUpperCase());
                SetEventsToAdapter(firstDayOfNewMonth);
                current_date = firstDayOfNewMonth;

            }
        });

        calendar.setSelected(false);

        SetEventsToAdapter(current_date);
    }

    private void SetEventsToAdapter(Date date){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String str_date = simpleDateFormat.format(date);
        ArrayList<EventItem> arrayList = new ArrayList<>();
        Cursor cursor = db.query(Const.TABLE_EVENT_LIST, null, Const.EVENT_DATE + "= '" + str_date + "'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            boolean notif = (cursor.getInt(3) == 1) ? true : false;
            String time = cursor.getString(5);
            arrayList.add(new EventItem(id,title, desc, notif, time));
            cursor.moveToNext();
        }
        if (arrayList.size() != 0){
            no_events_card.setVisibility(View.GONE);
        }else {
            no_events_card.setVisibility(View.VISIBLE);
        }
        rv.setAdapter(new EventAdapter(arrayList, getContext(), this, theme, date));
    }

    private void ShowMoonDialog(){
        boolean paid = settings.getBoolean(Const.APP_paid, false);
        if (!paid){
            Intent intent = new Intent(getContext(), PaidContentActivity.class);
            startActivity(intent);
        }else {
            Date new_moon_date;
            try {
                new_moon_date = new SimpleDateFormat("dd.MM.yyyy").parse("20.07.2020");
                long days_between = (Math.abs(current_date.getTime() - new_moon_date.getTime()) / (3600 * 1000 * 24)) % 29;
                // 0-2 - new moon
                // 6-10 - first quarter
                // 14-16 - full moon
                // 21-24 - third quarter

                String phase = "New moon";
                if (days_between >= 0 && days_between <= 2) {
                    phase = getStringFromResource(R.string.new_moon_phase);
                } else if (days_between > 2 && days_between < 6) {
                    phase = getStringFromResource(R.string.new_and_first);
                } else if (days_between >= 6 && days_between <= 10) {
                    phase = getStringFromResource(R.string.first_quarter_phase);
                } else if (days_between > 10 && days_between < 14) {
                    phase = getStringFromResource(R.string.first_and_full);
                } else if (days_between >= 14 && days_between <= 16) {
                    phase = getStringFromResource(R.string.full_moon_phase);
                } else if (days_between > 16 && days_between < 21) {
                    phase = getStringFromResource(R.string.full_and_third);
                } else if (days_between >= 21 && days_between <= 24) {
                    phase = getStringFromResource(R.string.third_quarter_phase);
                } else if (days_between > 24) {
                    phase = getStringFromResource(R.string.third_and_new);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = getLayoutInflater().inflate(R.layout.moon_dialog_layout, null);

                TextView orientation = view.findViewById(R.id.moon_orientation_text);
                orientation.setText(phase);

                TextView date_dialog = view.findViewById(R.id.orientation_text_title);
                date_dialog.setText(getStringFromResource(R.string.orientation_title) + " " + new SimpleDateFormat("dd.MM.yyyy").format(current_date));

                builder.setView(view);
                builder.show();

                //Toast.makeText(getContext(), Long.toString(days_between), Toast.LENGTH_LONG).show();
            } catch (ParseException e) {
                Toast.makeText(getContext(), getResources().getString(R.string.unavailable_moon_calendar), Toast.LENGTH_LONG).show();
                return;
            }
        }
    }


    private String getStringFromResource(int id){
        return getResources().getString(id);
    }

    private void InitializeArrayEvent() {
       Cursor cursor = db.query(Const.TABLE_EVENT_LIST, null, null, null, null, null, null);
       cursor.moveToFirst();
       while (!cursor.isAfterLast()){
          String date_str = cursor.getString(4);
           long mills = 0;
           try {
               mills = new SimpleDateFormat("dd.MM.yyyy").parse(date_str).getTime();
           } catch (ParseException e) {
               e.printStackTrace();
           }
           calendar.addEvent(new Event(Color.RED, mills));
           cursor.moveToNext();
       }
   }

    @Override
    public void OnEventClick(View view,int id, String title, String desc, String time, boolean is_checked) {
        InfoEventFragment fragment = new InfoEventFragment(db);
        Bundle bundle = new Bundle();
        bundle.putInt(Const.EVENT_ID, id);
        bundle.putString(Const.EVENT_NAME, title);
        bundle.putString(Const.EVENT_DESC, desc);
        bundle.putBoolean(Const.EVENT_IS_CHECKED, is_checked);
        bundle.putString(Const.EVENT_TIME, time);
        bundle.putString(Const.EVENT_DATE, new SimpleDateFormat("dd.MM.yyyy").format(current_date));
        fragment.setArguments(bundle);
        (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
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
