package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmRecevierKT : BroadcastReceiver() {

    val CHANNEL_NAME = "channel1"
    val CHANNEL_ID = "channel1"

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        lateinit var builder: NotificationCompat.Builder

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            )
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
        } else {
            builder = NotificationCompat.Builder(context)
        }

        val title = intent.getStringExtra("Title")
        val memo = intent.getStringExtra("Memo")

        val intent = Intent(context, TodayActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,101,intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentTitle(title)
        builder.setContentText(memo)
        builder.setSmallIcon(R.drawable.today)
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)

        manager.notify(1,builder.build())
    }
}