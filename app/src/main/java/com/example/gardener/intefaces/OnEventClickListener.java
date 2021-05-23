package com.example.gardener.intefaces;

import android.view.View;

public interface OnEventClickListener {
    void OnEventClick(View view,int id, String title, String desc, String time, boolean is_checked);
}
