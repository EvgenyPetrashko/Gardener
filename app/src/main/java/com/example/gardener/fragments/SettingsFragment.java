package com.example.gardener.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.PaidContentActivity;
import com.example.gardener.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SettingsFragment extends Fragment {
    private NestedScrollView back;
    private boolean dark_theme = true;
    private AdView ad_block;
    //private RelativeLayout language_block;
    private RelativeLayout text_size_block;
    private RelativeLayout feedback_block;
    private RelativeLayout full_version_block;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    private View main_view;
    private Switch theme_switch;

    TextView text_size;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       main_view = inflater.inflate(R.layout.settings_fragment_layout, container, false);
       settings = (getContext()).getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
       editor = settings.edit();
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }else {
            showAds();
        }
        init();
        configure();
       return main_view;
    }

    private void configure() {
        //String app_language = settings.getString(Const.APP_LANGUAGE, "en");
        boolean is_dark = settings.getBoolean(Const.APP_THEME, true);
        applyTheme(is_dark);
        String app_text_size = settings.getString(Const.APP_TEXT_SIZE, "normal");
        applySize(app_text_size);
    }

    private void applyTheme(boolean is_dark) {
        dark_theme = is_dark;
        int back_color = Color.BLACK;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
        }
        back.setBackgroundColor(back_color);
        //((TextView)main_view.findViewById(R.id.language_text_title)).setTextColor(text_color);
        ((TextView)main_view.findViewById(R.id.theme_text_title)).setTextColor(text_color);
        ((TextView)main_view.findViewById(R.id.text_size_text_title)).setTextColor(text_color);
        ((TextView)main_view.findViewById(R.id.feedback_text_title)).setTextColor(text_color);
        ((TextView)main_view.findViewById(R.id.full_version_block_title)).setTextColor(text_color);
        if (is_dark && !theme_switch.isChecked()){
            theme_switch.setChecked(true);
        }
    }

    private void applySize(String size){
        switch (size){
            case "small":
                text_size.setText(R.string.text_size_option_small);
                break;
            case "normal":
                text_size.setText(R.string.text_size_option_normal);
                break;
            case "large":
                text_size.setText(R.string.text_size_option_large);
                break;
        }
    }

    private void init() {
        back = main_view.findViewById(R.id.settings_fragment_back);

       // language_block = main_view.findViewById(R.id.language_block);
        text_size_block = main_view.findViewById(R.id.text_size_block);
        text_size = main_view.findViewById(R.id.text_size_desc);
        feedback_block = main_view.findViewById(R.id.feedback_block);
        full_version_block = main_view.findViewById(R.id.full_version_block);
        theme_switch = main_view.findViewById(R.id.theme_switcher);

       // language_block.setOnClickListener(ShowLanguageDialog());
        theme_switch.setOnClickListener(SwitchTheme());
        text_size_block.setOnClickListener(SwitchTextSize());
        feedback_block.setOnClickListener(OnFeedBackClickListener());
        full_version_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PaidContentActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showAds(){
        ad_block = main_view.findViewById(R.id.settings_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private View.OnClickListener SwitchTheme(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(Const.APP_THEME, theme_switch.isChecked());
                editor.apply();
                applyTheme(theme_switch.isChecked());
            }
        };
    }

    private View.OnClickListener SwitchTextSize(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowTextSizeDialog();
            }
        };
    }

    private View.OnClickListener OnFeedBackClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LeaveFeedbackFragment()).commit();
            }
        };
    }

    private void ShowTextSizeDialog(){
        int local_theme = R.style.DialogThemeLight;

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), local_theme);
        builder.setTitle(R.string.text_size_title);
        int checked = 1;
        switch (settings.getString(Const.APP_TEXT_SIZE, "normal")){
            case ("small"):
                checked = 0;
                break;
            case ("normal"):
                checked = 1;
                break;
            case ("large"):
                checked = 2;
                break;
        }
        builder.setSingleChoiceItems(new String[]{getString(R.string.text_size_option_small), getString(R.string.text_size_option_normal), getString(R.string.text_size_option_large)}, checked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String size = "normal";
                switch (which){
                    case 0:
                        size = "small";
                        break;
                    case 1:
                        size = "normal";
                        break;
                    case 2:
                        size = "large";
                        break;
                }
                editor.putString(Const.APP_TEXT_SIZE, size);
                editor.apply();
                applySize(size);
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.cancel_text_size, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

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
