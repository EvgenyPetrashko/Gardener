package com.example.gardener.adapters;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.Const;
import com.example.gardener.OwnPlant;
import com.example.gardener.R;
import com.example.gardener.fragments.InfoOwnPlantFragment;

import java.io.File;
import java.util.ArrayList;

public class OwnPlantAdapter extends RecyclerView.Adapter<OwnPlantAdapter.OwnPlantHolder> {
    private ArrayList<OwnPlant> array;
    private boolean dark;
    private FragmentActivity activity;
    private SQLiteDatabase db;
    private Context context;

    public OwnPlantAdapter(ArrayList<OwnPlant> array, boolean dark, FragmentActivity activity, SQLiteDatabase db, Context context) {
        this.array = array;
        this.dark = dark;
        this.activity = activity;
        this.db = db;
        this.context = context;
    }

    @NonNull
    @Override
    public OwnPlantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OwnPlantHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.own_plant_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final OwnPlantHolder holder, final int position) {
        final OwnPlant temp = array.get(position);
        holder.name.setText(temp.name);
        holder.date.setText(temp.date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoOwnPlantFragment fragment = new InfoOwnPlantFragment(db);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.OWN_GARDEN_ID, temp.id);
                bundle.putString(Const.OWN_GARDEN_NAME, temp.name);
                bundle.putString(Const.OWN_GARDEN_DESC, temp.desc);
                bundle.putString(Const.OWN_GARDEN_DATE, temp.date);
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context, R.style.DialogThemeLight)
                        .setTitle(context.getResources().getString(R.string.delete_plant_toast))
                        .setPositiveButton(context.getResources().getString(R.string.delete_event_option_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int delete_id = temp.id;
                                array.remove(position);
                                db.delete(Const.TABLE_OWN_GARDEN, Const.OWN_GARDEN_ID + "= '" + delete_id + "'", null);
                                Cursor delete_images = db.query(Const.TABLE_OWN_IMAGES, null,Const.OWN_IMAGES_ID + "= '" + delete_id + "'", null, null, null, null);
                                delete_images.moveToFirst();
                                while (!delete_images.isAfterLast()){
                                    String temp_path = Environment.getExternalStorageDirectory() + "/saved_images/" +delete_images.getString(1);
                                    deleteImage(temp_path);
                                    delete_images.moveToNext();
                                }
                                db.delete(Const.TABLE_OWN_IMAGES, Const.OWN_IMAGES_ID + "= '" + delete_id + "'", null);
                                Toast.makeText(context,  context.getResources().getString(R.string.delete_plant_toast_result), Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton( context.getResources().getString(R.string.cancel_event), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class OwnPlantHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private ImageButton delete;
        public OwnPlantHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.own_plant_title);
            date = itemView.findViewById(R.id.own_plant_date);
            delete = itemView.findViewById(R.id.own_plant_delete);
            if (dark){
                itemView.findViewById(R.id.own_plant_card_back).setBackgroundColor(Color.DKGRAY);
                name.setTextColor(Color.WHITE);
                date.setTextColor(Color.WHITE);
            }
        }
    }


    public void deleteImage(String imagePath) {
      new File(imagePath).delete();
    }

}
