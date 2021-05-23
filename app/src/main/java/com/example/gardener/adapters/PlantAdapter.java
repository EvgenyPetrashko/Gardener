package com.example.gardener.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.MainActivity;
import com.example.gardener.Plant;
import com.example.gardener.R;
import com.example.gardener.fragments.PlantItemInformationFragment;
import com.example.gardener.intefaces.CardOnItemClickListener;

import java.util.ArrayList;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantHolder> {
    private ArrayList<Plant> array;
    private Context context;
    private CardOnItemClickListener listener;

    public PlantAdapter(ArrayList<Plant> array, Context context) {
        this.array = array;
        this.context = context;
        //this.listener = listener;
        listener = (CardOnItemClickListener) context;
    }

    @NonNull
    @Override
    public PlantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlantHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_card_item,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlantHolder holder, int position) {
        Plant plant = array.get(position);
        holder.name.setText(plant.name);
        holder.back.setBackgroundResource(plant.image_id);
        holder.img_id = plant.image_id;
        holder._id = plant.id;
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class PlantHolder extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout back;
        int img_id;
        int _id;
        public PlantHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.plant_name);
            back = itemView.findViewById(R.id.plant_card_back);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.CardOnItemClick(v, img_id, _id);
                }
            });
        }
            //((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlantItemInformationFragment(context)).commit();
        }
}
