package com.example.gardener.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.Plant;
import com.example.gardener.R;
import com.example.gardener.adapters.PlantAdapter;
import com.example.gardener.intefaces.OnBackClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlantListFragment extends Fragment {
    private RecyclerView rv_plant;
    private ImageButton arrow_back;
    private ArrayList<Plant> list;
    private OnBackClickListener listener_back;
    private Const const_list;
    private LinearLayout back;
    private View mainView;
    private Toolbar toolbar;
    public PlantListFragment(Context context, ArrayList<Plant> temp){
        listener_back = (OnBackClickListener) context;
        list = temp;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.list_of_plants_layout, container, false);
        init();
        Sort();
        setHasOptionsMenu(true);
        search();
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }
        return mainView;

    }

    private void init(){
        back = mainView.findViewById(R.id.plant_list_fragment_back);
        rv_plant = mainView.findViewById(R.id.rv_plants);
        arrow_back = mainView.findViewById(R.id.back_arrow_list_of_plants);
        toolbar = mainView.findViewById(R.id.toolbar_list_of_plants);
        rv_plant.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        rv_plant.setAdapter(new PlantAdapter(list, getContext()));

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener_back.onBackCategoryClick(v);
            }
        });
        applyTheme();
    }

    private void applyTheme() {
        SharedPreferences sharedPreferences = (getContext()).getSharedPreferences(const_list.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        boolean is_dark = sharedPreferences.getBoolean(const_list.APP_THEME, true);
        if (is_dark){
            back.setBackgroundColor(Color.BLACK);
        }else{
            back.setBackgroundColor(Color.WHITE);
        }
    }

    private void search(){

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) toolbar.getMenu().findItem(R.id.plant_search).getActionView();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ArrayList<Plant> temp_list = new ArrayList<>();
                for (int i = 0; i <list.size() ; i++) {
                    if (list.get(i).name.toLowerCase().contains(query.toLowerCase())) temp_list.add(list.get(i));
                }
                rv_plant.setAdapter(new PlantAdapter(temp_list, getContext()));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                rv_plant.setAdapter(new PlantAdapter(list, getContext()));
            }
        });
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

    private void Sort(){
        Collections.sort(list, new PlantTitleComparator());
    }

    private static class PlantTitleComparator implements Comparator<Plant> {
        @Override
        public int compare(Plant o1, Plant o2) {
            int comparison = o1.name.compareTo(o2.name);
            if (comparison > 0){
                return 1;
            }else if (comparison < 0){
                return -1;
            }
            return 0;
        }
    }

}
