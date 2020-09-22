package com.alarm.alARm_you_need

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class RestartAlarmService : Service() {

    @Override
    override fun onCreate() {
        super.onCreate()
        Log.d("DEBUGGING LOG", "RestartAlarmService::OnCreate()")
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
    }

    @Override
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("DEBUGGING LOG", "RestartAlarmService::onStartCommand()")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "restart",
                "restart",
                NotificationManager.IMPORTANCE_NONE
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        val notification = NotificationCompat.Builder(this, "restart")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(9, notification)

        val alarmId = intent.getStringExtra("ALARM_ID")
        val serviceIntent = Intent(this, AlarmService::class.java)
        serviceIntent.putExtra("ALARM_ID", alarmId)
        Log.d("DEBUGGING LOG", "alarmId : $alarmId")

        startService(serviceIntent)

        stopForeground(true)
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


}