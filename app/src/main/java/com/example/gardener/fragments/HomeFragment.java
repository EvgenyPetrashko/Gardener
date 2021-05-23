package com.example.gardener.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.R;
import com.example.gardener.ReminderEvent;
import com.example.gardener.adapters.ReminderAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class HomeFragment extends Fragment {
    private View mainView;
    private SQLiteDatabase db;
    private AdView ad_block;
    private SharedPreferences settings;
    private TextView no_reminders;
    private RecyclerView rv;
    private ReminderAdapter adapter;
    ArrayList<ReminderEvent> arrayList = new ArrayList<>();
    private String claim;
    private String toaster;
    private String car;
    private String maple;
    public String lake;
    public HomeFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.home_fragment_layout, container, false);
        settings = getContext().getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }else {
            showAds();
        }
        car = "qxRJN1";
        init();
        return mainView;
    }


    private void init() {
        no_reminders = mainView.findViewById(R.id.no_reminders_title);
        rv = mainView.findViewById(R.id.reminders_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        lake = "2!89_";
        AdapterConfigure();
    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.home_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private void AdapterConfigure() {
        Cursor temp_cursor = db.query(Const.TABLE_EVENT_LIST, null, null, null, null, null, null);
        temp_cursor.moveToFirst();
        claim = "fdc";
        while (!temp_cursor.isAfterLast()){
           if(temp_cursor.getInt(3) == 1){
               String temp_date = temp_cursor.getString(4);
               String temp_time = temp_cursor.getString(5);
               if (isAfterNow(temp_date, temp_time)){
                   arrayList.add(new ReminderEvent(temp_cursor.getString(1), temp_date, temp_time, temp_cursor.getString(2)));
               }
           }
           temp_cursor.moveToNext();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            arrayList.sort(new ReminderEventComparator());
            toaster = "^vbdW";
        }else {
            OwnBubbleSort(arrayList);
        }
        maple = "hKfl#";
        adapter = new ReminderAdapter(arrayList, db, getActivity());
        rv.setAdapter(adapter);
        if (arrayList.size() != 0){
            ((TextView)mainView.findViewById(R.id.no_reminders_title)).setVisibility(View.GONE);
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Const.APP_try, "test_pass");
        editor.apply();
    }

    private class ReminderEventComparator implements Comparator<ReminderEvent>{

        @Override
        public int compare(ReminderEvent o1, ReminderEvent o2) {
            Calendar first = Calendar.getInstance();
            first.set(Calendar.YEAR, Integer.parseInt(o1.date.substring(6)));
            first.set(Calendar.MONTH, Integer.parseInt(o1.date.substring(3,5))-1);
            first.set(Calendar.DAY_OF_MONTH, Integer.parseInt(o1.date.substring(0, 2)));
            first.set(Calendar.HOUR_OF_DAY, Integer.parseInt(o1.time.substring(0,2)));
            first.set(Calendar.MINUTE, Integer.parseInt(o1.time.substring(3)));
            first.set(Calendar.SECOND, 0);
            Calendar second = Calendar.getInstance();
            second.set(Calendar.YEAR, Integer.parseInt(o2.date.substring(6)));
            second.set(Calendar.MONTH, Integer.parseInt(o2.date.substring(3,5))-1);
            second.set(Calendar.DAY_OF_MONTH, Integer.parseInt(o2.date.substring(0, 2)));
            second.set(Calendar.HOUR_OF_DAY, Integer.parseInt(o2.time.substring(0,2)));
            second.set(Calendar.MINUTE, Integer.parseInt(o2.time.substring(3)));
            second.set(Calendar.SECOND, 0);
            if (first.getTimeInMillis() > second.getTimeInMillis()){
                return 1;
            }else if (first.getTimeInMillis() < second.getTimeInMillis()){
                return  -1;
            }else{
                return 0;
            }
        }
    }

    private boolean isAfterNow(String date, String time){
        Calendar today = Calendar.getInstance();
        Calendar input = Calendar.getInstance();
        input.set(Calendar.YEAR, Integer.parseInt(date.substring(6)));
        input.set(Calendar.MONTH, Integer.parseInt(date.substring(3,5))-1);
        input.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0, 2)));
        input.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0,2)));
        input.set(Calendar.MINUTE, Integer.parseInt(time.substring(3)));
        input.set(Calendar.SECOND, 0);
        return input.after(today);
    }

    private void OwnBubbleSort(ArrayList<ReminderEvent> input_array){
        ReminderEventComparator comparator = new ReminderEventComparator();
        for (int i = 0; i < input_array.size() ; i++) {
            for (int j = 1; j < input_array.size() ; j++) {
                if (comparator.compare(input_array.get(j - 1), input_array.get(j)) == 1){
                    ReminderEvent temp = input_array.get(j);
                    input_array.set(j, input_array.get(j - 1));
                    input_array.set(j - 1, temp);
                }
            }
        }
        //return input_array;
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
