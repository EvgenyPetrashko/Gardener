package com.example.gardener;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class ReminderBroadcast extends BroadcastReceiver {
    // class which is responsible for sending reminders on specified date and time
    @Override
    public void onReceive(Context context, Intent intent) {
        String input_date = intent.getStringExtra(Const.EVENT_DATE);
        String input_time = intent.getStringExtra(Const.EVENT_TIME);
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.YEAR, Integer.parseInt(input_date.substring(6)));
        temp.set(Calendar.MONTH, Integer.parseInt(input_date.substring(3,5))-1);
        temp.set(Calendar.DAY_OF_MONTH, Integer.parseInt(input_date.substring(0,2)));
        temp.set(Calendar.HOUR_OF_DAY, Integer.parseInt(input_time.substring(0,2)));
        temp.set(Calendar.MINUTE, Integer.parseInt(input_time.substring(3)));
        temp.set(Calendar.SECOND, 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel")
                .setSmallIcon(R.drawable.ic_baseline_local_florist_black_24)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("desc"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                playNotificationSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void playNotificationSound(Uri alarmSound, Context mContext) {
        try {
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        }catch (Exception e){

        }
    }

}
