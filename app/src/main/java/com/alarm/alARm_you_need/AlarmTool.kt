package com.alarm.alARm_you_need

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
import io.realm.Realm
import java.util.*

class AlarmTool : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "다음 알람"
        const val ACTION_RUN_ALARM = "RUN_ALARM"
        private const val DAY_OF_WEEK_BASE = 4
        ////1970.01.01 == Thursday

        private val DAY_OF_WEEK = arrayOf("일", "월", "화", "수", "목", "금", "토")

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
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                getTriggerAtMillis(hourOfDay, minute),
                alarmIntent
            )

            updateAlarmNotification(context)
        }

        fun deleteAlarm(context: Context, id: String) {
            Log.d("DEBUGGING LOG", "AlarmTool::deleteAlarm() is called  ...id : $id")
            val alarmIntent = createAlarmIntent(context, id)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(alarmIntent)

            updateAlarmNotification(context)
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

        private fun getTimeInMillis(tomorrow: Boolean, hourOfDay: Int, minute: Int): Long {
            val calendar = GregorianCalendar.getInstance() as GregorianCalendar
            if (tomorrow) calendar.add(GregorianCalendar.DAY_OF_YEAR, 1)
            calendar[GregorianCalendar.HOUR_OF_DAY] = hourOfDay
            calendar[GregorianCalendar.MINUTE] = minute
            calendar[GregorianCalendar.SECOND] = 0
            calendar[GregorianCalendar.MILLISECOND] = 0
            return calendar.timeInMillis
        }

        fun updateAlarmNotification(context: Context) {
            createNotificationChannel(context, NOTIFICATION_CHANNEL_ID)
            val notificationIntent = Intent(context, NotificationService::class.java)
            val timeOfUpcomingAlarm = findTimeOfUpcomingAlarm(false)

            notificationIntent.putExtra("upcomingTime", timeOfUpcomingAlarm)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val sharedPreference =
                    context.getSharedPreferences("configurationPreference", Context.MODE_PRIVATE)
                val isNotificationSwitchOn =
                    sharedPreference.getBoolean("pref_status_bar_notification", false)
                if (isNotificationSwitchOn)
                    context.startForegroundService(notificationIntent)
            }
        }

        fun hideAlarmNotification(context: Context) {
            createNotificationChannel(context, NOTIFICATION_CHANNEL_ID)
            val notificationIntent = Intent(context, NotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val sharedPreference =
                    context.getSharedPreferences("notifyPref", Context.MODE_PRIVATE)
                val isNotificationSwitchOn =
                    sharedPreference.getBoolean("configurationPreference", false)
                if (isNotificationSwitchOn)
                    context.stopService(notificationIntent)
            }
        }

        private fun findTimeOfUpcomingAlarm(isRemainTime: Boolean): Long {
            val realm = Realm.getDefaultInstance()
            val alarmData = AlarmDao(realm).getActiveAlarms()
            var upcomingTime: Long = Long.MAX_VALUE

            if (alarmData != null) {
                for (alarm in alarmData) {
                    val remainTime = findTimeOfAlarm(
                        getTargetDays(alarm),
                        alarm.hour,
                        alarm.minute,
                        isRemainTime
                    )
                    if (remainTime < upcomingTime) {
                        upcomingTime = remainTime
                    }
                }
            }

            return upcomingTime
        }

        fun findTimeOfAlarm(
            targetDays: BooleanArray,
            targetHour: Int,
            targetMinute: Int,
            isRemainTime: Boolean
        ): Long {
            var targetTimeInMillis = getTimeInMillis(false, targetHour, targetMinute)
            val currentTime = GregorianCalendar.getInstance() as GregorianCalendar
            val currentTimeInMillis = currentTime.timeInMillis
            val dayOfToday = currentTime.get(GregorianCalendar.DAY_OF_WEEK) - 1

            val dDay = getDayOffsetFromToday(
                currentTimeInMillis,
                targetTimeInMillis,
                dayOfToday,
                targetDays
            )
            if (dDay == Int.MAX_VALUE)
                return Long.MAX_VALUE

            targetTimeInMillis += (dDay * 1000 * 60 * 60 * 24)

            return if (isRemainTime)
                targetTimeInMillis - currentTimeInMillis
            else
                targetTimeInMillis
        }

        private fun getDayOffsetFromToday(
            sourceTimeInMillis: Long, targetTimeInMillis: Long,
            dayOfToday: Int, targetDays: BooleanArray
        ): Int {
            var dDay = 0
            var dayOffset = dayOfToday
            if (targetTimeInMillis < sourceTimeInMillis) {
                dayOffset++
                dDay++
            }
            dayOffset %= 7

            while (!targetDays[dayOffset]) {
                dayOffset++
                dayOffset %= 7
                dDay++
                if (dDay > 10)
                    return Int.MAX_VALUE
            }

            return dDay
        }

        fun convertMillisToTimeFormat(timeInMillis: Long): String {
            if (timeInMillis == Long.MAX_VALUE)
                return "등록된 알람이 없습니다."

            val hourOffset = TimeZone.getDefault().getOffset(timeInMillis)
            val localTimeInMillis = timeInMillis + hourOffset
            val minute = (localTimeInMillis / (1000 * 60)) % 60
            val hour = (localTimeInMillis / (1000 * 60 * 60)) % 24
            val day = (localTimeInMillis / (1000 * 60 * 60)) / 24 + DAY_OF_WEEK_BASE

            return String.format(
                "[%s] %d 시 %d 분에 알람이 울립니다.",
                DAY_OF_WEEK[day.toInt() % 7],
                hour,
                minute
            )
        }

        fun convertMillisToRemainingTimeFormat(timeInMillis: Long): String {
            if (timeInMillis == Long.MAX_VALUE) return ""

            val minute = (timeInMillis / (1000 * 60)) % 60
            val hour = (timeInMillis / (1000 * 60 * 60)) % 24
            val day = (timeInMillis / (1000 * 60 * 60)) / 24

            return String.format("%d 일 %d시간 %d분 후에 알람이 울립니다.", day, hour, minute)
        }

        private fun getTargetDays(alarm: AlarmData): BooleanArray {
            val targetDays = BooleanArray(7)
            if (alarm.sun) targetDays[0] = true
            if (alarm.mon) targetDays[1] = true
            if (alarm.tue) targetDays[2] = true
            if (alarm.wed) targetDays[3] = true
            if (alarm.thur) targetDays[4] = true
            if (alarm.fri) targetDays[5] = true
            if (alarm.sat) targetDays[6] = true

            return targetDays
        }

        private fun createNotificationChannel(context: Context, channelId: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var channelName = ""
                when (channelId) {
                    NOTIFICATION_CHANNEL_ID -> channelName = "다음 알람"
                }
                val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_NONE
                )
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        private fun deleteNotificationChannel(context: Context, channelId: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.deleteNotificationChannel(channelId)
            }

        }

        fun showUpcomingAlarmNotification(context: Context) {
            updateAlarmNotification(context)
        }

        fun hideUpcomingAlarmNotification(context: Context) {
            val notificationIntent = Intent(context, NotificationService::class.java)
            context.stopService(notificationIntent)
            deleteNotificationChannel(context, NOTIFICATION_CHANNEL_ID)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DEBUGGING LOG", "onReceive: ${intent.action}")
        when (intent.action) {
            ACTION_RUN_ALARM -> {
                //if (AlarmService.service != null) return
                val alarmId: String? = intent.getStringExtra("ALARM_ID")
                val realm = Realm.getDefaultInstance()
                val alarmData = AlarmDao(realm).selectAlarm(alarmId!!)
                Log.d(
                    "DEBUGGING LOG",
                    "[Received ACTION_RUN_ALARM info]" +
                            " alarm Id : $alarmId" +
                            " is today? : " + isAlarmToday(alarmData) +
                            " is active? : " + alarmData.active
                )

                if (isAlarmToday(alarmData) && alarmData.active) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val serviceIntent = Intent(context, RestartAlarmService::class.java)
                        serviceIntent.putExtra("ALARM_ID", alarmId)
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
        return when (calendar[GregorianCalendar.DAY_OF_WEEK]) {
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