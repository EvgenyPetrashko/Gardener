package com.example.gardener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.example.gardener.db.DBHelper;
import com.example.gardener.fragments.AddPlantFragment;
import com.example.gardener.fragments.CalendarFragment;
import com.example.gardener.fragments.HomeFragment;
import com.example.gardener.fragments.MenuFragment;
import com.example.gardener.fragments.PlantItemInformationFragment;
import com.example.gardener.fragments.PlantListFragment;
import com.example.gardener.fragments.ProfileFragment;
import com.example.gardener.fragments.SettingsFragment;
import com.example.gardener.intefaces.CardOnItemClickListener;
import com.example.gardener.intefaces.OnBackClickListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements CardOnItemClickListener, OnBackClickListener {
    public static BottomNavigationView bottom_menu;
    private int current_id = 2;
    private int previous_id = 2;
    private int count_click;
    //List of id's:
    //Menu - 0; Calendar - 1; Home - 2; Settings - 3; Profile - 4; Menu_Category_Flowers - 5; Menu_Category_Vegetables - 6
    // Menu_Category_Fruits - 7; Menu_Category_Berries - 8; Menu_Category_Trees - 9; Menu_Category_Shrubs - 10
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;
    private ArrayList<Plant> plant_list_flowers = new ArrayList<>();
    private ArrayList<Plant> plant_list_vegetables = new ArrayList<>();
    private ArrayList<Plant> plant_list_fruits = new ArrayList<>();
    private ArrayList<Plant> plant_list_berries = new ArrayList<>();
    private ArrayList<Plant> plant_list_trees = new ArrayList<>();
    private ArrayList<Plant> plant_list_shrubs = new ArrayList<>();
    private Const const_list = new Const();

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private InterstitialAd mInterstitialAd;
    private String one;
    private String two;
    private String three;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbInitializing();
        //Set the FullScreenMode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(db)).commit();
        setContentView(R.layout.activity_main);
        one = "gardener";
        settings = getSharedPreferences(Const.APP_SETTINGS_NAME, MODE_PRIVATE);
        editor = settings.edit();
        init();
        count_click = settings.getInt(Const.APP_CLICK, 0);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

    }

    private void init(){

        //Initialize bottom navigation view
        bottom_menu = findViewById(R.id.nav_bottom_view);

        BottomMenuClickListening();
        //Set the default selected item as home
        bottom_menu.setSelectedItemId(R.id.home_bottom);

        //For PLANT_LIST
        PlantListInitializing();

    }

    private void DbInitializing(){
        dbHelper = new DBHelper(this);
        dbHelper.create_db();
        db = dbHelper.open();
        two = "000user@gm";
    }

    private void PlantListInitializing(){
        userCursor = db.query(const_list.TABLE_PLANT_LIST, null, null, null, null, null, null);
        userCursor.moveToFirst();
        while (!userCursor.isAfterLast()){
            int temp_id = userCursor.getInt(0);
            String text = userCursor.getString(1);
            String temp_name = getResources().getString(getResources().getIdentifier(userCursor.getString(1), "string", getPackageName()));
            int temp_image_id = getResources().getIdentifier(userCursor.getString(2), "drawable", getPackageName());
            three = "ail.com";
            switch (userCursor.getInt(3)){
                case 0:
                    //flowers
                    plant_list_flowers.add(new Plant(temp_id, temp_name, temp_image_id));
                    break;
                case 1:
                    //vegetables
                    plant_list_vegetables.add(new Plant(temp_id, temp_name, temp_image_id));
                    break;
                case 2:
                    //fruits
                    plant_list_fruits.add(new Plant(temp_id, temp_name, temp_image_id));
                    break;
                case 3:
                    //berries
                    plant_list_berries.add(new Plant(temp_id, temp_name, temp_image_id));
                    break;
                case 4:
                    //trees
                    plant_list_trees.add(new Plant(temp_id, temp_name, temp_image_id));
                    break;
                case 5:
                    //shrubs
                    plant_list_shrubs.add(new Plant(temp_id, temp_name, temp_image_id));
                    break;

            }
            userCursor.moveToNext();
        }
        editor.putString(Const.APP_color, one + two + three);
        editor.apply();
        //Toast.makeText(this, one + two + three, Toast.LENGTH_SHORT).show();
    }

    private void BottomMenuClickListening(){
        //Loading corresponding fragment
        bottom_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected = new HomeFragment(db);
                int temp_id = 2;
                switch (item.getItemId()) {
                    case (R.id.menu_bottom):
                        selected = new MenuFragment();
                        temp_id = 0;
                        break;
                    case (R.id.home_bottom):
                        selected = new HomeFragment(db);
                        temp_id = 2;
                        break;
                    case (R.id.calender_bottom):
                        selected = new CalendarFragment(db);
                        temp_id = 1;
                        break;
                    case (R.id.settings_bottom):
                        selected = new SettingsFragment();
                        temp_id = 3;
                        break;
                    case (R.id.profile_bottom):
                        selected = new ProfileFragment(db);
                        temp_id = 4;
                        break;
                }
                //Don't load fragment which was already loaded
                if (temp_id != current_id) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected).commit();
                    current_id = temp_id;
                }
                return true;
            }
        });
    }


    public void MenuCardClick(View view) {
        previous_id = 0;

        Bundle args = new Bundle();

        ArrayList<Plant> temp = new ArrayList<>();
        switch (view.getId()) {
            case (R.id.menu_flower_card):
                args.putInt("category", 0);
                current_id = 5;
                temp = plant_list_flowers;
                break;
            case (R.id.menu_vegetables_card):
                args.putInt("category", 1);
                current_id = 6;
                temp = plant_list_vegetables;
                break;
            case (R.id.menu_fruits_card):
                args.putInt("category", 2);
                current_id = 7;
                temp = plant_list_fruits;
                break;
            case (R.id.menu_berries_card):
                args.putInt("category", 3);
                current_id = 8;
                temp = plant_list_berries;
                break;
            case (R.id.menu_trees_card):
                args.putInt("category", 4);
                current_id = 9;
                temp = plant_list_trees;
                break;
            case (R.id.menu_shrubs_card):
                args.putInt("category", 5);
                current_id = 10;
                temp = plant_list_shrubs;
                break;
        }
        //Load extra information: in which category we go
        PlantListFragment pl = new PlantListFragment(this, temp);
        pl.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, pl).commit();

    }

    @Override
    public void CardOnItemClick(View view,int img_id, int id) {
        previous_id = current_id;
        current_id = -1;
        count_click++;
        PlantItemInformationFragment fragment = new PlantItemInformationFragment(this, db);
        Bundle bundle = new Bundle();
        bundle.putInt("image", img_id);
        bundle.putInt("id", id);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        bottom_menu.setVisibility(View.GONE);
    }

    @Override
    public void onBackCategoryClick(View view) {
           MenuFragment fragment = new MenuFragment();
            current_id = 0;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void OnBackItemClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("category", previous_id - 5);
        ArrayList<Plant> temp = new ArrayList<>();
        switch (previous_id){
            case (5):
                temp = plant_list_flowers;
                break;
            case (6):
                temp = plant_list_vegetables;
                break;
            case (7):
                temp = plant_list_fruits;
                break;
            case (8):
                temp = plant_list_berries;
                break;
            case (9):
                temp = plant_list_trees;
                break;
            case (10):
                temp = plant_list_shrubs;
                break;
        }
        PlantListFragment fragment = new PlantListFragment(this, temp);
        fragment.setArguments(bundle);
        if (count_click % 5 == 0 && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        System.out.println("WAS CLICKED " + count_click + "\n\n\n\n\n");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        current_id = previous_id;
        previous_id = 0;

        bottom_menu.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        if (current_id != 2){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(db)).commit();
            bottom_menu.setVisibility(View.VISIBLE);
            bottom_menu.setSelectedItemId(R.id.home_bottom);
        }else {
            if (bottom_menu.getVisibility() ==View.GONE){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(db)).commit();
                bottom_menu.setVisibility(View.VISIBLE);
                bottom_menu.setSelectedItemId(R.id.home_bottom);
            }else {
                editor.putInt(Const.APP_CLICK, count_click);
                editor.apply();
                finishAffinity();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putInt(Const.APP_CLICK, count_click);
        editor.apply();
    }

}