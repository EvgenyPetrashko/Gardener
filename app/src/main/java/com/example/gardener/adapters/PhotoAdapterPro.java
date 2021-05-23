package com.example.gardener.adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.example.gardener.Const;
import com.example.gardener.DrawablePro;
import com.example.gardener.R;

import java.io.File;
import java.util.ArrayList;

public class PhotoAdapterPro extends BaseAdapter {
    ArrayList<DrawablePro> arrayList;
    ArrayList<String> deleted_paths = new ArrayList<>();
    SQLiteDatabase db;
    boolean is_dark;
    public PhotoAdapterPro(ArrayList<DrawablePro> arrayList, SQLiteDatabase db, boolean is_dark){
        this.arrayList = arrayList;
        this.db = db;
        this.is_dark = is_dark;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_layout, parent ,false);
        view.setBackground(arrayList.get(position).img);
        final ImageButton delete = view.findViewById(R.id.photo_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(parent.getContext(), (R.style.DialogThemeLight))
                        .setTitle(parent.getResources().getString(R.string.delete_image_toast))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleted_paths.add(arrayList.get(position).path_img);
                                arrayList.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(parent.getResources().getString(R.string.cancel_event), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        return view;
    }

    public void ApplyDeleting(){
        for (int i = 0; i <deleted_paths.size() ; i++) {
            if (!deleted_paths.get(i).equals("NEW")) {
                db.delete(Const.TABLE_OWN_IMAGES, Const.OWN_IMAGES_PATH + "= '" + deleted_paths.get(i) + "'", null);
                String full_path = Environment.getExternalStorageDirectory() + "/saved_images/" + deleted_paths.get(i);
                new File(full_path).delete();
            }
        }
    }
}
