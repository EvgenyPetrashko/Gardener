package com.example.gardener.adapters;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.gardener.R;

import java.util.ArrayList;


public class GridPhotoOwnAdapter extends BaseAdapter {
    ArrayList<Drawable> arrayList;
    public GridPhotoOwnAdapter(ArrayList<Drawable> arrayList){
        this.arrayList = arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_own_plant_photo_layout, parent, false);
        view.setBackground(arrayList.get(position));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImage(parent, arrayList.get(position));
            }
        });
        return view;
    }

    private void ShowImage(ViewGroup parent, Drawable dr_img){
        Dialog builder = new Dialog(parent.getContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        View dialog_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_image_layout, null);
        ((ImageView)dialog_view.findViewById(R.id.image_full)).setImageDrawable(dr_img);


        builder.setContentView(dialog_view);
        builder.setCancelable(true);
        builder.show();
    }
}
