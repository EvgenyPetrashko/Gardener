package com.example.gardener.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.MainActivity;
import com.example.gardener.R;
import com.example.gardener.intefaces.OnBackClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import at.blogc.android.views.ExpandableTextView;

public class InfoFavouriteFragment extends Fragment {
    private View mainView;
    private SharedPreferences settings;
    private SQLiteDatabase db;

    private ImageButton back_arrow;
    private RelativeLayout item_card_back;

    private TextView desc_title_text;
    private ExpandableTextView expand_desc_text;

    private TextView cond_title_text;
    private ExpandableTextView expand_cond_text;

    private TextView care_title_text;
    private ExpandableTextView expand_care_text;
    private AdView ad_block;
    public InfoFavouriteFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.info_favourite_fragment_layout , container, false);
        settings = getContext().getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        init();
        Configure();
        applyTheme(settings.getBoolean(Const.APP_THEME, true));
        applySize();
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
        back_arrow = mainView.findViewById(R.id.back_arrow_item);
        back_arrow.setOnClickListener(OnBackClickListener());

        item_card_back = mainView.findViewById(R.id.item_card_back);

        desc_title_text = mainView.findViewById(R.id.desc_title_text);
        expand_desc_text = mainView.findViewById(R.id.expand_desc_text);

        cond_title_text = mainView.findViewById(R.id.cond_title_text);
        expand_cond_text = mainView.findViewById(R.id.expand_cond_text);

        care_title_text = mainView.findViewById(R.id.care_title_text);
        expand_care_text = mainView.findViewById(R.id.expand_care_text);

        desc_title_text.setOnClickListener(OnTitleClick(expand_desc_text));
        cond_title_text.setOnClickListener(OnTitleClick(expand_cond_text));
        care_title_text.setOnClickListener(OnTitleClick(expand_care_text));
    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.info_favourite_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private void Configure(){
        Bundle bundle = getArguments();
        String desc = bundle.getString(Const.ITEMS_DESC, "ERROR");
        String cond = bundle.getString(Const.ITEMS_COND, "ERROR");
        String care = bundle.getString(Const.ITEMS_CARE, "ERROR");
        int image_id = bundle.getInt("image", R.drawable.strawberry_berries_card_back);
        item_card_back.setBackgroundResource(image_id);
        expand_desc_text.setText(desc);
        expand_cond_text.setText(cond);
        expand_care_text.setText(care);
    }

    private void applyTheme(boolean is_dark){
        int back_color = Color.BLACK;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
        }else{
            int color = getResources().getColor(R.color.experiment, null);
            mainView.findViewById(R.id.desc_title_back).setBackgroundColor(color);
            mainView.findViewById(R.id.cond_title_back).setBackgroundColor(color);
            mainView.findViewById(R.id.care_title_back).setBackgroundColor(color);
        }
        mainView.findViewById(R.id.plant_item_fragment_back).setBackgroundColor(back_color);

        desc_title_text.setTextColor(text_color);
        expand_desc_text.setTextColor(text_color);

        cond_title_text.setTextColor(text_color);
        expand_cond_text.setTextColor(text_color);

        care_title_text.setTextColor(text_color);
        expand_care_text.setTextColor(text_color);
    }

    private void applySize(){
        String size = settings.getString(Const.APP_TEXT_SIZE, "normal");
        int text_size = 14;
        switch (size){
            case "small":
                text_size = 14;
                break;
            case "normal":
                text_size = 16;
                break;
            case "large":
                text_size = 18;
                break;
        }
        expand_desc_text.setTextSize(text_size);
        expand_cond_text.setTextSize(text_size);
        expand_care_text.setTextSize(text_size);
    }

    private View.OnClickListener OnBackClickListener(){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                      getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FavouriteFragment(db)).commit();
                      TurnOnBNV();
            }
        };
    }

    private View.OnClickListener OnTitleClick(final ExpandableTextView expand_text){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expand_text.isExpanded()){
                    expand_text.collapse();
                }else {
                    expand_text.expand();
                }
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
