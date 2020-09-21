package com.alarm.alARm_you_need

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class NotificationService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val upcomingAlarmInMillis = intent.getLongExtra("upcomingTime", 0L)
        val notification = NotificationCompat.Builder(this, AlarmTool.CHANNEL_ID)
            .setContentTitle("AR람")
            .setContentText(AlarmTool.convertMillisToTimeFormat(upcomingAlarmInMillis))
            .setSmallIcon(R.mipmap.alarm_you_need_icon)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}