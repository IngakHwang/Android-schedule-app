package com.example.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmRecevier extends BroadcastReceiver {
    public AlarmRecevier(){}

    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "channel1";

    NotificationManager manager;
    NotificationCompat.Builder builder;

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        builder = null;
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        }else{
            builder = new NotificationCompat.Builder(context);
        }
        String title = intent.getStringExtra("Title");
        String memo = intent.getStringExtra("Memo");

        Intent intent2 = new Intent(context, todayAct.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(title);
        builder.setContentText(memo);
        builder.setSmallIcon(R.drawable.today);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        manager.notify(1,notification);

       /* builder.setSmallIcon(R.drawable.today);
        builder.setContentTitle("알림 제목");
        builder.setContentText("알림 세부 텍스트");
        builder.setColor(Color.RED);
        builder.setAutoCancel(true); // 푸시 알림창 터치시 사라짐
        builder.setDefaults(Notification.DEFAULT_VIBRATE); // 소리(이건 진동)
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT); // 중요도
        builder.setContentIntent(intent);*/


    }
}
