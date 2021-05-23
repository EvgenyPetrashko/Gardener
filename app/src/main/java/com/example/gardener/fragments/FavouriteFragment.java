package com.example.gardener.fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.Const;
import com.example.gardener.FavouritePlant;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.R;
import com.example.gardener.adapters.FavouriteAdapter;

import java.util.ArrayList;

public class FavouriteFragment extends Fragment {
    private View mainView;
    private SharedPreferences settings;
    private SQLiteDatabase db;

    private ImageButton back_arrow;
    private RecyclerView rv;
    private FavouriteAdapter adapter;
    public FavouriteFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.favourite_fragment , container, false);
        settings = getContext().getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        init();
        applyTheme(settings.getBoolean(Const.APP_THEME, true));
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }
        return mainView;
    }

    private void applyTheme(boolean is_dark) {
        mainView.findViewById(R.id.favourite_fragment_back).setBackgroundColor((is_dark? Color.BLACK:Color.WHITE));
    }

    private void init() {
        back_arrow = mainView.findViewById(R.id.favourite_fragment_back_arrow);
        back_arrow.setOnClickListener(OnBackClickListener());
        rv = mainView.findViewById(R.id.favourite_fragment_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        AdapterConfigure();
    }

    private void AdapterConfigure(){
        Cursor temp_cursor = db.query(Const.TABLE_FAVOURITE_PLANTS, null, null, null, null, null, null);
        temp_cursor.moveToFirst();
        ArrayList<FavouritePlant> arrayList = new ArrayList<>();
        while (!temp_cursor.isAfterLast()){
            int code = temp_cursor.getInt(0);
            Cursor another = db.query(Const.TABLE_PLANT_LIST, null, Const.LIST_ID + "= '" + code + "'", null, null, null, null);
            another.moveToFirst();
            String temp_name = getResources().getString(getResources().getIdentifier(another.getString(1), "string", getContext().getPackageName()));
            int temp_image_id = getResources().getIdentifier(another.getString(2), "drawable", getContext().getPackageName());
            FavouritePlant plant = new FavouritePlant(code, temp_name, temp_image_id);
            arrayList.add(plant);
            temp_cursor.moveToNext();
        }
        adapter = new FavouriteAdapter(arrayList, db, getActivity());
        rv.setAdapter(adapter);
    }

    private View.OnClickListener OnBackClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(db)).commit();
            }
        };
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
