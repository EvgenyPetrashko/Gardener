package com.example.gardener.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.example.gardener.R;

import java.util.ArrayList;

public class GridPhotoAdapter extends BaseAdapter {
    ArrayList<Drawable> array;
    boolean is_dark;
    public GridPhotoAdapter(ArrayList<Drawable> array, boolean is_dark){
        this.array = array;
        this.is_dark = is_dark;
    }
    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_layout, parent ,false);
        view.setBackground(array.get(position));
        ImageButton delete = view.findViewById(R.id.photo_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(parent.getContext(), (R.style.DialogThemeLight))
                        .setTitle("Do you really want to delete this photo?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                array.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        return view;
    }
}
