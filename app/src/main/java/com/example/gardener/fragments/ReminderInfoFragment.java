package com.example.gardener.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.MainActivity;
import com.example.gardener.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class ReminderInfoFragment extends Fragment {
    private View mainView;
    private SharedPreferences settings;
    private SQLiteDatabase db;
    private ImageButton back_arrow;
    private TextView name;
    private TextView desc;
    private TextView date;
    private TextView time;

    private AdView ad_block;
    public ReminderInfoFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.reminder_info_fragment_layout , container, false);
        settings = getContext().getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        init();
        Configure();
        applyTheme(settings.getBoolean(Const.APP_THEME, true));
        TurnOffBNV();
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }else {
            showAds();
        }
        return mainView;
    }

    private void init(){
        back_arrow = mainView.findViewById(R.id.reminder_info_back_arrow);
        back_arrow.setOnClickListener(OnBackClickListener());
        name = mainView.findViewById(R.id.reminder_info_name_text);
        desc = mainView.findViewById(R.id.reminder_info_desc_text);
        date = mainView.findViewById(R.id.reminder_info_date);
        time = mainView.findViewById(R.id.reminder_info_time);
    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.reminder_info_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private void Configure(){
        Bundle bundle = getArguments();
        name.setText(bundle.getString("name", "Name"));
        desc.setText(bundle.getString("desc", "Description"));
        date.setText(bundle.getString("date", "01.01.2020"));
        time.setText(bundle.getString("time", "17:00"));
    }

    private void applyTheme(boolean is_dark){
        int back_color = Color.BLACK;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
        }
        name.setTextColor(text_color);
        desc.setTextColor(text_color);
        mainView.findViewById(R.id.reminder_info_back).setBackgroundColor(back_color);
    }

    private View.OnClickListener OnBackClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(db)).commit();
                TurnOnBNV();
            }
        };
    }

    private void TurnOffBNV(){
        MainActivity.bottom_menu.setVisibility(View.GONE);
    }

    private void TurnOnBNV(){
        MainActivity.bottom_menu.setVisibility(View.VISIBLE);
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
