package com.example.gardener.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.R;

public class MenuFragment extends Fragment {
    private View mainView;
    private LinearLayout back;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.menu_fragment_layout, container, false);
        init();
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }
        return mainView;
    }

    private void init() {
        back = mainView.findViewById(R.id.menu_fragment_back);
        applyTheme();
    }

    private void applyTheme() {
        SharedPreferences sharedPreferences = (getContext()).getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        boolean is_dark = sharedPreferences.getBoolean(Const.APP_THEME, true);
        if (is_dark){
            back.setBackgroundColor(Color.BLACK);
        }else{
            back.setBackgroundColor(Color.WHITE);
        }
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
