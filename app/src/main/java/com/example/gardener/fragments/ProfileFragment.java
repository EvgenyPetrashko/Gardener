package com.example.gardener.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.OwnPlant;
import com.example.gardener.R;
import com.example.gardener.adapters.OwnPlantAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class ProfileFragment extends Fragment {
    private View mainView;
    private AdView ad_block;
    private CardView personal_card;
    private CardView favourite_card;
    private RecyclerView rv;
    private OwnPlantAdapter adapter;
    private ImageButton fab;
    private SQLiteDatabase db;
    private SharedPreferences settings;
    public ProfileFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.profile_fragment_layout, container, false);
        settings = getContext().getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }else {
            showAds();
        }
        init();
        Configure();
        UserImageConfigure();
        return mainView;
    }

    private void init() {
        personal_card = mainView.findViewById(R.id.person_card);
        personal_card.setOnClickListener(OnPersonCardClick());

        favourite_card = mainView.findViewById(R.id.favourites_card);
        favourite_card.setOnClickListener(OnFavouritesClickListener());

        rv = mainView.findViewById(R.id.my_garden_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        AdapterConfigure();
        rv.setAdapter(adapter);
        fab = mainView.findViewById(R.id.pr_fr__add_plant);
        fab.setOnClickListener(OnAddPlantListener());
    }

    private void Configure(){
        ((TextView)mainView.findViewById(R.id.person_name_text)).setText(settings.getString(Const.APP_USER_FULL_NAME, getContext().getResources().getString(R.string.user_full_name)));
    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.profile_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private void UserImageConfigure(){
        String path = "def_gard_icon_64_" + randInt(1,10);
        int id = getResources().getIdentifier(path, "drawable", getContext().getPackageName());
        if (id == 0){
            id = getResources().getIdentifier("def_gard_icon_64_1", "drawable", getContext().getPackageName());
        }
        ((ImageView) mainView.findViewById(R.id.person_icon)).setImageResource(id);
    }


    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    private View.OnClickListener OnPersonCardClick(){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.enter_full_name_dialog, (ViewGroup)(getView().getParent()), false);
                final EditText full_name = view.findViewById(R.id.full_name_text);
                full_name.setText(settings.getString(Const.APP_USER_FULL_NAME, getContext().getResources().getString(R.string.user_full_name)));
                new AlertDialog.Builder(getContext(), R.style.DialogThemeLight)
                        .setView(view)
                        .setTitle("Enter the full name")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((TextView)mainView.findViewById(R.id.person_name_text)).setText(full_name.getText().toString());
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(Const.APP_USER_FULL_NAME, full_name.getText().toString());
                                editor.apply();
                            }
                        }).show();
            }
        };
    }

    private View.OnClickListener OnFavouritesClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FavouriteFragment(db)).commit();
            }
        };
    }

    private void AdapterConfigure(){
        ArrayList<OwnPlant> temp_array = new ArrayList<>();
        Cursor temp_cursor = db.query(Const.TABLE_OWN_GARDEN, null, null , null, null, null, null);
        temp_cursor.moveToFirst();
        while (!temp_cursor.isAfterLast()){
            int temp_id = temp_cursor.getInt(0);
            String temp_name = temp_cursor.getString(1);
            String temp_desc = temp_cursor.getString(2);
            String temp_date = temp_cursor.getString(3);
            OwnPlant object = new OwnPlant(temp_id, temp_name, temp_desc, temp_date);
            temp_array.add(object);
            temp_cursor.moveToNext();
        }
        adapter = new OwnPlantAdapter(temp_array, true, getActivity(), db, getContext());
    }

    private View.OnClickListener OnAddPlantListener(){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddPlantFragment(db)).commit();
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
