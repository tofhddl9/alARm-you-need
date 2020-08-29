package com.alarm.alARm_you_need

import android.app.*
import android.content.Context
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
        val builder = NotificationCompat.Builder(this, "default")
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle(null)
        builder.setContentText(null)

        Log.d("DEBUGGING LOG", "RESTART!")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        builder.setContentIntent(pendingIntent)

        val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    "default",
                    "기본 채널",
                    NotificationManager.IMPORTANCE_NONE
                )
            )
        }

        val notification: Notification = builder.build()
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