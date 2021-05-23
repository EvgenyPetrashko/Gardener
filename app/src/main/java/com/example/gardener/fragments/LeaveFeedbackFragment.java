package com.example.gardener.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.EventRemind;
import com.example.gardener.MainActivity;
import com.example.gardener.R;

public class LeaveFeedbackFragment extends Fragment {
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.leave_comment_fragment_layout, container ,false);
        init();
        settings = (getContext()).getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        TitleConfigure();
        applyTheme(settings.getBoolean(Const.APP_THEME, true));
        TurnOffBNV();
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
       title.setText(getContext().getResources().getString(R.string.leave_feedback_title));
    }

    private void init() {
        back = mainView.findViewById(R.id.leave_comment_fragment_back);

        title = mainView.findViewById(R.id.leave_comment_text_title);
        comment = mainView.findViewById(R.id.leave_comment_text);

        cancel = mainView.findViewById(R.id.leave_comment_cancel_button);
        cancel.setOnClickListener(OnCancelFeedback());
        send = mainView.findViewById(R.id.leave_comment_send_button);
        send.setOnClickListener(OnFeedbackSend());
    }

    private View.OnClickListener OnCancelFeedback(){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), (R.style.DialogThemeLight))
                        .setTitle(getString(R.string.feedback_cancel_title))
                        .setPositiveButton(R.string.comment_option_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SettingsFragment fragment = new SettingsFragment();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                TurnOnBNV();
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

    private View.OnClickListener OnFeedbackSend(){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "16leftguy@mail.ru"));
                intent.putExtra(Intent.EXTRA_SUBJECT, title.getText().toString());
                intent.putExtra(Intent.EXTRA_TEXT, comment.getText().toString());
                startActivity(intent);*/
               String rel = "e.petrashko19@gmail.com";
                EventRemind api = new EventRemind(getContext(), rel, title.getText().toString(), comment.getText().toString());
                api.execute();
                SettingsFragment fragment = new SettingsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                TurnOnBNV();

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
