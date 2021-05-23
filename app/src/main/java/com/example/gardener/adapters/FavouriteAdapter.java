package com.example.gardener.adapters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.Const;
import com.example.gardener.FavouritePlant;
import com.example.gardener.R;
import com.example.gardener.fragments.InfoFavouriteFragment;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteHolder> {
    private ArrayList<FavouritePlant> array;
    private SQLiteDatabase db;
    private FragmentActivity activity;
    public FavouriteAdapter(ArrayList<FavouritePlant> array, SQLiteDatabase db, FragmentActivity activity){
        this.array = array;
        this.db = db;
        this.activity = activity;
    }
    @NonNull
    @Override
    public FavouriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavouriteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FavouriteHolder holder, final int position) {
        final FavouritePlant temp = array.get(position);
        holder.back.setBackgroundResource(temp.img_id);
        holder.name.setText(temp.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp_id = array.get(position).id;
                Cursor fav = db.query(Const.TABLE_PLANT_ITEMS, null, Const.ITEMS_ID + "= '" + temp_id + "'", null, null, null, null);
                fav.moveToFirst();
                Bundle bundle = new Bundle();
                bundle.putInt(Const.ITEMS_ID, temp_id);
                bundle.putInt("image", temp.img_id);
                if (fav.isAfterLast()){
                    bundle.putString(Const.ITEMS_COND, "ERROR");
                    bundle.putString(Const.ITEMS_DESC, "ERROR");
                    bundle.putString(Const.ITEMS_CARE, "ERROR");
                }else{
                    String desc = fav.getString(1);
                    String cond = fav.getString(2);
                    String care = fav.getString(3);
                    bundle.putString(Const.ITEMS_COND, cond);
                    bundle.putString(Const.ITEMS_DESC, desc);
                    bundle.putString(Const.ITEMS_CARE, care);
                }
                InfoFavouriteFragment fragment = new InfoFavouriteFragment(db);
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class FavouriteHolder extends RecyclerView.ViewHolder {
        LinearLayout back;
        TextView name;
        public FavouriteHolder(@NonNull View itemView) {
            super(itemView);
            back = itemView.findViewById(R.id.plant_card_back);
            name = itemView.findViewById(R.id.plant_name);
        }
    }
}
