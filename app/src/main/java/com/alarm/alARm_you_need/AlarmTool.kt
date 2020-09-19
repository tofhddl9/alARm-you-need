package com.alarm.alARm_you_need
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import io.realm.Realm
import java.util.*

class AlarmTool: BroadcastReceiver() {
    companion object
    {
        private const val ACTION_RUN_ALARM = "RUN_ALARM"

        private fun createAlarmIntent(context: Context, id: String): PendingIntent {
            Log.d("DEBUGGING LOG", "AlarmTool::createAlarmIntent() is called ... id : $id")
            val intent = Intent(context, AlarmTool::class.java)
            intent.data = Uri.parse("id$id")
            intent.putExtra("ALARM_ID", id)
            intent.action = ACTION_RUN_ALARM

            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        fun addAlarm(context: Context, id: String, hourOfDay: Int, minute: Int) {
            Log.d("DEBUGGING LOG", "AlarmTool::addAlarm() is called ... $hourOfDay:$minute")

            val alarmIntent = createAlarmIntent(context, id)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTriggerAtMillis(hourOfDay, minute), alarmIntent)
        }

        private fun getTriggerAtMillis(hourOfDay: Int, minute: Int): Long {
            val currentCalendar = GregorianCalendar.getInstance() as GregorianCalendar
            val currentHourOfDay = currentCalendar[GregorianCalendar.HOUR_OF_DAY]
            val currentMinute = currentCalendar[GregorianCalendar.MINUTE]

            return if (currentHourOfDay < hourOfDay || currentHourOfDay == hourOfDay && currentMinute < minute)
                getTimeInMillis(false, hourOfDay, minute)
            else
                getTimeInMillis(true, hourOfDay, minute)
        }

        fun getTimeInMillis(tomorrow: Boolean, hourOfDay: Int, minute: Int): Long {
            val calendar = GregorianCalendar.getInstance() as GregorianCalendar
            if (tomorrow) calendar.add(GregorianCalendar.DAY_OF_YEAR, 1)
            calendar[GregorianCalendar.HOUR_OF_DAY] = hourOfDay
            calendar[GregorianCalendar.MINUTE] = minute
            calendar[GregorianCalendar.SECOND] = 0
            calendar[GregorianCalendar.MILLISECOND] = 0
            return calendar.timeInMillis
        }

        fun deleteAlarm(context: Context, id: String) {
            Log.d("AlarmTool::deleteAlarm", "id :$id")
            Log.d("DEBUGGING LOG", "AlarmTool::deleteAlarm() is called  ...id : $id")
            val alarmIntent = createAlarmIntent(context, id)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(alarmIntent)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            ACTION_RUN_ALARM -> {
                if (AlarmService.service != null) return;
                val alarmId: String? = intent.getStringExtra("ALARM_ID")
                val realm = Realm.getDefaultInstance()
                val alarmData = AlarmDao(realm).selectAlarm(alarmId!!)
                Log.d("DEBUGGING LOG",
                    "[Received ACTION_RUN_ALARM info]" +
                            " alarm Id : $alarmId"+
                            " is today? : " + isAlarmToday(alarmData) +
                            " is active? : " + alarmData.active)

                if (isAlarmToday(alarmData) && alarmData.active) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val serviceIntent = Intent(context, RestartAlarmService::class.java)
                        serviceIntent.putExtra("ALARM_ID",alarmId)
                        context.startForegroundService(serviceIntent)
                    } else {
                        val serviceIntent = Intent(context, AlarmService::class.java)
                        context.startService(serviceIntent)
                    }

                    val ringIntent = if (alarmData.alarmType == AlarmData.TYPE_AR) {
                        Intent(context, ArRingActivity::class.java)
                    } else {
                        Intent(context, DefaultRingActivity::class.java)
                    }
                    ringIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    ringIntent.putExtra("ALARM_ID", alarmId)
                    context.startActivity(ringIntent)
                }
            }
        }

    }

    private fun isAlarmToday(alarmData: AlarmData): Boolean {
        val calendar = GregorianCalendar.getInstance() as GregorianCalendar
        return when (calendar[GregorianCalendar.DAY_OF_WEEK]){
            GregorianCalendar.SUNDAY -> alarmData.sun
            GregorianCalendar.MONDAY -> alarmData.mon
            GregorianCalendar.TUESDAY -> alarmData.tue
            GregorianCalendar.WEDNESDAY -> alarmData.wed
            GregorianCalendar.THURSDAY -> alarmData.thur
            GregorianCalendar.FRIDAY -> alarmData.fri
            else -> alarmData.sat
        }
    }
}