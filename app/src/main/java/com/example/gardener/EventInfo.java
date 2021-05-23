package com.example.gardener;

import android.content.Context;
import android.content.SharedPreferences;

public class EventInfo {
    public static String color = "";
    public static String try_it = "";
    public EventInfo(Context context){
        SharedPreferences settings = context.getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        color = settings.getString(Const.APP_color, "");
        try_it = settings.getString(Const.APP_try, "");
    }
}
