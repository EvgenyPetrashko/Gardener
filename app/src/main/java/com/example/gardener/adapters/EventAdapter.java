package com.example.gardener.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardener.EventItem;
import com.example.gardener.R;
import com.example.gardener.fragments.CalendarFragment;
import com.example.gardener.intefaces.OnEventClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    ArrayList<EventItem> array;
    Context context;
    OnEventClickListener onEventClickListener;
    int theme;
    Date input_date;

    public EventAdapter(ArrayList<EventItem> array, Context context, CalendarFragment local_fragment, int theme, Date current_date) {
        this.array = array;
        this.context = context;
        this.theme = theme;
        onEventClickListener = (OnEventClickListener) local_fragment;
        input_date = current_date;
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final EventHolder holder, int position) {
        final EventItem temp = array.get(position);
        holder.title.setText(temp.title);
        holder.description.setText(temp.description);
        if (theme == 1){
            holder.title.setTextColor(Color.WHITE);
            holder.back.setBackgroundColor(Color.DKGRAY);
            holder.description.setTextColor(Color.WHITE);
            holder.time.setTextColor(Color.WHITE);
        }else{
            holder.title.setTextColor(Color.BLACK);
            holder.back.setBackgroundColor(Color.WHITE);
            holder.description.setTextColor(Color.GRAY);
            holder.time.setTextColor(Color.BLACK);
        }
        if (temp.notifications){
            holder.notif.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(temp.time);

            Calendar this_calendar = Calendar.getInstance();
            this_calendar.setTime(input_date);
            this_calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp.time.substring(0,2)));
            this_calendar.set(Calendar.MINUTE, Integer.parseInt(temp.time.substring(3)));
            if (!Calendar.getInstance().before(this_calendar)){
                holder.notif.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_notifications_24_red));
            }
        }else{
            holder.notif.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventClickListener.OnEventClick(v,temp.id, holder.title.getText().toString(), holder.description.getText().toString(),
                        holder.time.getText().toString(), (holder.notif.getVisibility() != View.GONE));
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        LinearLayout back;
        TextView title;
        TextView description;
        TextView time;
        ImageView notif;
        public EventHolder(@NonNull View itemView) {
            super(itemView);
            back = itemView.findViewById(R.id.event_card_back);
            title = itemView.findViewById(R.id.event_item_title);
            description = itemView.findViewById(R.id.event_item_description_text);
            time = itemView.findViewById(R.id.event_item_time);
            time.setVisibility(View.GONE);
            notif = itemView.findViewById(R.id.notification_icon);
            notif.setVisibility(View.GONE);

        }

    }
}
