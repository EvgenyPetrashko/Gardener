package com.example.gardener.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.MainActivity;
import com.example.gardener.R;
import com.example.gardener.adapters.GridPhotoOwnAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.util.ArrayList;

public class InfoOwnPlantFragment extends Fragment {
    private SQLiteDatabase db;
    private View mainView;
    private int local_id;
    private String local_name;
    private String local_desc;
    private String local_date;
    private ImageButton back_arrow;
    private TextView date;
    private ImageButton change_button;
    private TextView name;
    private TextView desc;
    private GridView gridImages;
    private GridPhotoOwnAdapter adapter_gr;
    private SharedPreferences settings;

    private AdView ad_block;
    public InfoOwnPlantFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.info_own_plant_layout , container, false);
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

    private void init() {
        back_arrow = mainView.findViewById(R.id.info_own_back_arrow);
        back_arrow.setOnClickListener(OnBackClickListener());
        change_button = mainView.findViewById(R.id.info_own_change_button);
        change_button.setOnClickListener(OnChangeClickListener());
        date = mainView.findViewById(R.id.info_own_date);
        change_button = mainView.findViewById(R.id.info_own_change_button);
        name = mainView.findViewById(R.id.info_own_name);
        desc = mainView.findViewById(R.id.info_own_desc);
        gridImages = mainView.findViewById(R.id.gridViewOwnPhoto);
        //rv = mainView.findViewById(R.id.info_own_rv);
        //rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.info_own_plant_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private View.OnClickListener OnBackClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(db)).commit();
                TurnOnBNV();
            }
        };
    }

    private View.OnClickListener OnChangeClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeOwnPlantFragment fragment = new ChangeOwnPlantFragment(db);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.OWN_GARDEN_ID, local_id);
                bundle.putString(Const.OWN_GARDEN_NAME, local_name);
                bundle.putString(Const.OWN_GARDEN_DESC, local_desc);
                bundle.putString(Const.OWN_GARDEN_DATE, local_date);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        };
    }

    private void Configure(){
        Bundle bundle = getArguments();
        local_id = bundle.getInt(Const.OWN_GARDEN_ID);
        local_name = bundle.getString(Const.OWN_GARDEN_NAME);
        name.setText(local_name);
        local_desc = bundle.getString(Const.OWN_GARDEN_DESC);
        desc.setText(local_desc);
        local_date = bundle.getString(Const.OWN_GARDEN_DATE);
        date.setText(local_date);
        Cursor temp_cursor = db.query(Const.TABLE_OWN_IMAGES, null, Const.OWN_IMAGES_ID + "= '" + local_id + "'", null, null, null, null);
        temp_cursor.moveToFirst();
        ArrayList<Drawable> draw_array = new ArrayList<>();
        while (!temp_cursor.isAfterLast()){
            String path;
            try {
                path = temp_cursor.getString(1);
                draw_array.add(getDrawable(path));
            }catch (Exception e){
                break;
            }
            temp_cursor.moveToNext();
        }
        adapter_gr = new GridPhotoOwnAdapter(draw_array);
        gridImages.setAdapter(adapter_gr);
    }

    private void applyTheme(boolean is_dark){
        int back_color = Color.BLACK;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
        }
        mainView.setBackgroundColor(back_color);
        name.setTextColor(text_color);
        desc.setTextColor(text_color);
    }

    private Drawable getDrawable(String path){
        String location = Environment.getExternalStorageDirectory().getAbsolutePath()+"/saved_images/" + path;
        Bitmap bitmap = BitmapFactory.decodeFile(location);
        return new BitmapDrawable(getResources(), bitmap);
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
