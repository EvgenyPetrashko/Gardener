package com.example.gardener;

import android.graphics.drawable.Drawable;

public class DrawablePro {
    public Drawable img;
    public String path_img;
    public boolean is_new;

    public DrawablePro(Drawable img, String path_img, boolean is_new) {
        this.img = img;
        this.path_img = path_img;
        this.is_new = is_new;
    }
}
