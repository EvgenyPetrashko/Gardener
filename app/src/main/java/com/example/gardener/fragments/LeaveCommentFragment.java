package com.example.gardener.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.EventRemind;
import com.example.gardener.R;

public class LeaveCommentFragment  extends Fragment {
    private View mainView;
    private LinearLayout back;
    private int input_img;
    private int global_id;
    private boolean theme;

    private TextView title;
    private EditText comment;

    private ImageButton cancel;
    private ImageButton send;

    private SharedPreferences settings;
    private SQLiteDatabase db;
    public LeaveCommentFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.leave_comment_fragment_layout, container, false);
        settings = (getContext()).getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        input_img = getArguments().getInt("image", R.drawable.strawberry_berries_card_back);
        global_id = getArguments().getInt("id", 0);
        init();
        TitleConfigure();
        applyTheme(settings.getBoolean(Const.APP_THEME, true));
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }
        return mainView;
    }

    private void applyTheme(boolean is_dark) {
        theme = is_dark;
        int back_color = Color.BLACK;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
        }
        title.setTextColor(text_color);
        comment.setTextColor(text_color);
        comment.setHintTextColor(text_color);
        back.setBackgroundColor(back_color);
    }

    private void TitleConfigure(){
        Cursor temp = db.query(Const.TABLE_PLANT_LIST, null, Const.LIST_ID + "= '" + global_id + "'", null, null, null, null);
        temp.moveToFirst();
        if (temp.isAfterLast()){
            title.setText("FEEDBACK");
        }else{
            title.setText(getResources().getString(getResources().getIdentifier(temp.getString(1), "string", getContext().getPackageName())));
        }
    }

    private void init() {
        back = mainView.findViewById(R.id.leave_comment_fragment_back);

        title = mainView.findViewById(R.id.leave_comment_text_title);
        comment = mainView.findViewById(R.id.leave_comment_text);

        cancel = mainView.findViewById(R.id.leave_comment_cancel_button);
        cancel.setOnClickListener(OnCancelComment());
        send = mainView.findViewById(R.id.leave_comment_send_button);
        send.setOnClickListener(OnCommentSend());
    }

    private View.OnClickListener OnCancelComment(){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), (R.style.DialogThemeLight))
                        .setTitle(getString(R.string.comment_cancel))
                        .setPositiveButton(R.string.comment_option_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PlantItemInformationFragment fragment = new PlantItemInformationFragment(getContext(), db);
                                Bundle bundle = new Bundle();
                                bundle.putInt("image", input_img);
                                bundle.putInt("id", global_id);
                                fragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                            }
                        }).setNegativeButton(R.string.comment_option_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
            }
        };
    }

    private View.OnClickListener OnCommentSend(){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String rel = "e.petrashko19@gmail.com";
                EventRemind api = new EventRemind(getContext(), rel, title.getText().toString(), comment.getText().toString());
                api.execute();
                PlantItemInformationFragment fragment = new PlantItemInformationFragment(getContext(), db);
                Bundle bundle = new Bundle();
                bundle.putInt("image", input_img);
                bundle.putInt("id", global_id);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

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
