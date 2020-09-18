package com.alarm.alARm_you_need

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class NotificationService : Service() {

    companion object {
        const val CHANNEL_ID = "다음 알람"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AR람")
            .setContentText("다음 알람은 시 분 후에 울립니다.")
            .setSmallIcon(R.mipmap.alarm_you_need_icon)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0))
            .build()

        startForeground(1, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}