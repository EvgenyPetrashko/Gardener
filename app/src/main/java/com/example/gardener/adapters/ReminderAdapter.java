package com.example.gardener.adapters;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.R;
import com.example.gardener.ReminderEvent;
import com.example.gardener.fragments.ReminderInfoFragment;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderHolder> {
    ArrayList<ReminderEvent> arrayList;
    FragmentActivity fragmentActivity;
    SQLiteDatabase db;
    public ReminderAdapter(ArrayList<ReminderEvent> arrayList, SQLiteDatabase db, FragmentActivity fragmentActivity){
        this.arrayList = arrayList;
        this.db = db;
        this.fragmentActivity = fragmentActivity;
    }
    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReminderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item_layout , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderHolder holder, int position) {
        final ReminderEvent temp  = arrayList.get(position);
        holder.name.setText(temp.name);
        holder.date.setText(temp.date + " " + temp.time);
        Calendar temp_calendar = Calendar.getInstance();
        temp_calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp.time.substring(0,2)));
        temp_calendar.set(Calendar.MINUTE, Integer.parseInt(temp.time.substring(3)));
        temp_calendar.set(Calendar.SECOND, 0);
        Calendar input = Calendar.getInstance();
        input.set(Calendar.YEAR, Integer.parseInt(temp.date.substring(6)));
        input.set(Calendar.MONTH, Integer.parseInt(temp.date.substring(3,5) ) - 1);
        input.set(Calendar.DAY_OF_MONTH, Integer.parseInt(temp.date.substring(0, 2)));
        input.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp.time.substring(0,2)));
        input.set(Calendar.MINUTE, Integer.parseInt(temp.time.substring(3)));
        input.set(Calendar.SECOND, 0);
        holder.date_left.setText(holder.date_left.getText().toString() +  (input.getTimeInMillis() - temp_calendar.getTimeInMillis())/(1000*3600*24));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderInfoFragment fragment = new ReminderInfoFragment(db);
                Bundle bundle = new Bundle();
                bundle.putString("name", temp.name);
                bundle.putString("date", temp.date);
                bundle.putString("time", temp.time);
                bundle.putString("desc", temp.desc);
                fragment.setArguments(bundle);
                fragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ReminderHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private TextView date_left;
        public ReminderHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.reminder_name);
            date = itemView.findViewById(R.id.reminder_date_time);
            date_left = itemView.findViewById(R.id.reminder_days_left);
        }
    }
}
