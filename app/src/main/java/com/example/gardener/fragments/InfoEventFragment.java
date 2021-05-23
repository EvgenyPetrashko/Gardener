package com.example.gardener.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.MainActivity;
import com.example.gardener.R;
import com.example.gardener.ReminderBroadcast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class InfoEventFragment extends Fragment {
    private View mainView;
    private SQLiteDatabase db;
    private NestedScrollView back;
    private SharedPreferences settings;

    private Toolbar toolbar;
    private ImageButton back_arrow;

    private TextView event_name_title;
    private TextView event_name_text;

    private TextView event_desc_title;
    private TextView event_desc_text;

    private RelativeLayout time_line;
    private TextView event_notify_title;
    private TextView event_notify_text;

    private int mId;
    private String mTitle;
    private String mDesc;
    private String mDate;
    private String mTime;
    private boolean mIs_checked;

    private AdView ad_block;
    public InfoEventFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.event_info_fragment_layout, container, false);
        init();
        Configure();
        settings = getContext().getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        applyTheme();
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }else {
            showAds();
        }
        TurnOffBNV();
        return mainView;
    }

    private void init() {
        toolbar = mainView.findViewById(R.id.info_fragment_toolbar);
        toolbar.setOnMenuItemClickListener(OnMenuClick());

        back = mainView.findViewById(R.id.info_fragment_back);

        back_arrow = mainView.findViewById(R.id.info_fragment_back_arrow);
        back_arrow.setOnClickListener(OnBackPressed());

        event_name_title = mainView.findViewById(R.id.info_fragment_event_name_title);
        event_name_text = mainView.findViewById(R.id.info_fragment_event_name_text);

        event_desc_title = mainView.findViewById(R.id.info_fragment_event_desc_title);
        event_desc_text = mainView.findViewById(R.id.info_fragment_event_desc_text);

        time_line = mainView.findViewById(R.id.info_fragment_time_block);
        event_notify_title = mainView.findViewById(R.id.info_fragment_event_time_title);
        event_notify_text = mainView.findViewById(R.id.info_fragment_event_time_text);
    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.event_info_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private void Configure(){
        Bundle bundle = getArguments();
        event_name_text.setText(bundle.getString(Const.EVENT_NAME, getResources().getString(R.string.event_name_hint)));
        event_desc_text.setText(bundle.getString(Const.EVENT_DESC, getResources().getString(R.string.event_description_hint)));
        if (bundle.getBoolean(Const.EVENT_IS_CHECKED)){
            time_line.setVisibility(View.VISIBLE);
            event_notify_text.setText(bundle.getString(Const.EVENT_TIME, "10.00"));
        }else {
            time_line.setVisibility(View.GONE);
        }
        mId = bundle.getInt(Const.EVENT_ID);
        mTitle = bundle.getString(Const.EVENT_NAME);
        mDesc = bundle.getString(Const.EVENT_DESC);
        mDate = bundle.getString(Const.EVENT_DATE);
        mTime = bundle.getString(Const.EVENT_TIME);
        mIs_checked = bundle.getBoolean(Const.EVENT_IS_CHECKED);
    }

    private void applyTheme(){
        boolean is_dark = settings.getBoolean(Const.APP_THEME, true);
        int back_color = Color.BLACK;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
        }

        event_name_title.setTextColor(text_color);
        event_desc_title.setTextColor(text_color);
        event_notify_title.setTextColor(text_color);
        back.setBackgroundColor(back_color);
    }

    private View.OnClickListener OnBackPressed(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarFragment(db)).commit();
                TurnOnBNV();
            }
        };
    }

    private Toolbar.OnMenuItemClickListener OnMenuClick(){
        return new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.change_info_event: {
                        ChangeEventFragment fragment = new ChangeEventFragment(db);
                        Bundle bundle = new Bundle();
                        bundle.putInt(Const.EVENT_ID, mId);
                        bundle.putString(Const.EVENT_NAME, mTitle);
                        bundle.putString(Const.EVENT_DESC, mDesc);
                        bundle.putString(Const.EVENT_DATE, mDate);
                        bundle.putString(Const.EVENT_TIME, mTime);
                        bundle.putBoolean(Const.EVENT_IS_CHECKED, mIs_checked);
                        fragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    }
                        break;
                    case R.id.delete_info_event: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(getResources().getString(R.string.delete_event_title))
                                .setPositiveButton(R.string.delete_event_option_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CancelNotification(mId);
                                        String selection = Const.EVENT_ID + "= '" + mId + "'";
                                        db.delete(Const.TABLE_EVENT_LIST, selection, null);
                                        (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarFragment(db)).commit();
                                        TurnOnBNV();
                                        Toast.makeText(getContext(), getResources().getString(R.string.result_deleted_string), Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton(R.string.delete_event_option_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                        break;
                }

                return true;
            }
        };
    }

    private void CancelNotification(int db_id){
        Intent intent = new Intent(getContext(), ReminderBroadcast.class);
        Cursor temp = db.query(Const.TABLE_EVENT_LIST, null, Const.EVENT_ID + "= '" + db_id + "'", null, null , null, null);
        temp.moveToFirst();
        String title = temp.getString(1);
        String desc = temp.getString(2);
        String date = temp.getString(4);
        String time = temp.getString(5);
        intent.putExtra("title", title);
        intent.putExtra("desc", desc);
        intent.putExtra("date", date);
        intent.putExtra("time", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), db_id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) (getActivity()).getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

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
