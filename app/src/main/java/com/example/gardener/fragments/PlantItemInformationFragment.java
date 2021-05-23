package com.example.gardener.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.R;
import com.example.gardener.intefaces.OnBackClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import at.blogc.android.views.ExpandableTextView;

public class PlantItemInformationFragment extends Fragment {
    private RelativeLayout item_card_back;
    private int image;
    private int global_id;
    private AdView ad_block;

    private TextView desc_title_text;
    private ExpandableTextView expand_desc_text;

    private TextView cond_title_text;
    private ExpandableTextView expand_cond_text;

    private TextView care_title_text;
    private ExpandableTextView expand_care_text;

    private ImageButton back_item_button;
    private OnBackClickListener listener_back;

    private Button leave_comment;

    private View mainView;
    private NestedScrollView back;

    private ImageButton favourite_button;

    SharedPreferences settings;
    private SQLiteDatabase db;

    public PlantItemInformationFragment(Context context, SQLiteDatabase db){
        listener_back = (OnBackClickListener) context;
        this.db = db;
    }
    /*private View.OnClickListener listener_title;*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.plant_item_information_layout,container, false);
        settings = (getContext()).getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }else {
            showAds();
        }
        init();
        image = getArguments().getInt("image", R.drawable.strawberry_berries_card_back);
        global_id = getArguments().getInt("id", 0);
        //item_card_back.setBackground(getResources().getDrawable(image, null));
        item_card_back.setBackgroundResource(image);
        Configure();
        return mainView;
    }

    private void init(){
        back = mainView.findViewById(R.id.plant_item_fragment_back);

        item_card_back = mainView.findViewById(R.id.item_card_back);

        desc_title_text = mainView.findViewById(R.id.desc_title_text);
        expand_desc_text = mainView.findViewById(R.id.expand_desc_text);

        cond_title_text = mainView.findViewById(R.id.cond_title_text);
        expand_cond_text = mainView.findViewById(R.id.expand_cond_text);

        care_title_text = mainView.findViewById(R.id.care_title_text);
        expand_care_text = mainView.findViewById(R.id.expand_care_text);

        leave_comment = mainView.findViewById(R.id.leave_comment_button);

        back_item_button = mainView.findViewById(R.id.back_arrow_item);
        back_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener_back.OnBackItemClick(v);
            }
        });

        favourite_button = mainView.findViewById(R.id.favourite_item_button);
        favourite_button.setOnClickListener(OnFavouriteAddListener());

        desc_title_text.setOnClickListener(OnTitleClick(expand_desc_text));
        cond_title_text.setOnClickListener(OnTitleClick(expand_cond_text));
        care_title_text.setOnClickListener(OnTitleClick(expand_care_text));
        leave_comment.setOnClickListener(OnLeaveComment());

        applyTheme();
        applySize();
    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.plant_item_inf_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private View.OnClickListener OnFavouriteAddListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor check_fav = db.query(Const.TABLE_FAVOURITE_PLANTS, null, Const.FAVOURITE_PLANT_ID + "= '" + global_id + "'", null, null, null, null);
                check_fav.moveToFirst();
                if (!check_fav.isAfterLast()){
                    db.delete(Const.TABLE_FAVOURITE_PLANTS, Const.FAVOURITE_PLANT_ID + "= '" + global_id + "'", null);
                    favourite_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24, null));
                }else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Const.FAVOURITE_PLANT_ID, global_id);
                    db.insert(Const.TABLE_FAVOURITE_PLANTS, null, contentValues);
                    favourite_button.setImageDrawable(getResources().getDrawable(R.drawable.favorite_icon, null));
                }
            }
        };
    }

    private void Configure(){
        Cursor check_fav = db.query(Const.TABLE_FAVOURITE_PLANTS, null, Const.FAVOURITE_PLANT_ID + "= '" + global_id + "'", null, null, null, null);
        check_fav.moveToFirst();
        if (!check_fav.isAfterLast()){
            favourite_button.setImageDrawable(getResources().getDrawable(R.drawable.favorite_icon, null));
        }
        Toast.makeText(getContext(), Integer.toString(global_id), Toast.LENGTH_SHORT).show();
        Cursor temp_cursor = db.query(Const.TABLE_PLANT_ITEMS, null, Const.ITEMS_ID + "= '" + global_id + "'", null, null, null, null);
        temp_cursor.moveToFirst();
        String desc;
        String cond;
        String care;
        if (temp_cursor.isAfterLast()){
            desc = "ERROR";
            cond = "ERROR";
            care = "ERROR";
        }else{
            desc = getStringText(temp_cursor.getString(1));
            cond =getStringText( temp_cursor.getString(2));
            care = getStringText( temp_cursor.getString(3));
        }

        expand_desc_text.setText(desc);
        expand_cond_text.setText(cond);
        expand_care_text.setText(care);
        //set text
    }

    private String getStringText(String name){
       return getResources().getString(getResources().getIdentifier(name , "string", getContext().getPackageName()));
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

    private View.OnClickListener OnLeaveComment(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeaveCommentFragment fragment = new LeaveCommentFragment(db);
                Bundle bundle = new Bundle();
                bundle.putInt("image", image);
                bundle.putInt("id", global_id);
                fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        };
    }

    private void applyTheme(){
        boolean is_dark = settings.getBoolean(Const.APP_THEME, true);
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
        back.setBackgroundColor(back_color);

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
