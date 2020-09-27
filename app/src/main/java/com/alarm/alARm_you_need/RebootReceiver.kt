package com.alarm.alARm_you_need

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import io.realm.Realm

class RebootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DEBUGGING LOG", "RebootReceiver::onReceive()")
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                registerActiveAlarms(context)
                showNotificationIfActive(context)
            }
        }
    }

    private fun registerActiveAlarms(context: Context) {
        val realm = Realm.getDefaultInstance()
        val activeAlarms = AlarmDao(realm).getActiveAlarms()

        if (activeAlarms != null) {
            for (alarmData in activeAlarms) {
                AlarmTool.addAlarm(context, alarmData.alarmId, alarmData.hour, alarmData.minute)
            }
        }
    }

    private fun showNotificationIfActive(context: Context) {
        if (isStatusbarNotificationSwtichOn(context)) {
            createNotificationChannel(context)
            startNotificationService(context)
        }
    }

    private fun isStatusbarNotificationSwtichOn(context: Context): Boolean {
        val sharedPreference =
            context.getSharedPreferences("configurationPreference", Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("isNotifying", false)
    }

    private fun startNotificationService(context: Context) {
        val notificationIntent = Intent(context, NotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(notificationIntent)
        } else {
            context.startService(notificationIntent)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                AlarmTool.NOTIFICATION_CHANNEL_ID,
                "Notification Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}