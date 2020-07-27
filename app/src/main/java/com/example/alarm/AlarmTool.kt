package com.example.alarm
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import io.realm.Realm
import java.util.*

class AlarmTool: BroadcastReceiver() {
    companion object
    {
        private const val ACTION_RUN_ALARM = "RUN_ALARM"

        private fun createAlarmIntent(context: Context, id: String): PendingIntent {
            val intent = Intent(context, AlarmTool::class.java)
            intent.data = Uri.parse("id$id")
            intent.putExtra("ALARM_ID", id)
            intent.action = ACTION_RUN_ALARM

            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        fun addAlarm(context: Context, id: String, hourOfDay: Int, minute: Int) {
            val alarmIntent = createAlarmIntent(context, id)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        }

        fun deleteAlarm(context: Context, id: String) {
            val alarmIntent = createAlarmIntent(context, id)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(alarmIntent)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            ACTION_RUN_ALARM -> {
            }

            Intent.ACTION_BOOT_COMPLETED -> {
            }
        }

    }


}