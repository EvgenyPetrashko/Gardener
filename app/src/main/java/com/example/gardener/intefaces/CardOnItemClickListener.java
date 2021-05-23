package com.example.gardener.intefaces;

import android.view.View;
//We need this method for listening clicks on CardItems via MainActivity
public interface CardOnItemClickListener {
    void CardOnItemClick(View view, int img_id, int id);
}
