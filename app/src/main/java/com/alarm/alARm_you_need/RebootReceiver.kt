package com.alarm.alARm_you_need

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.realm.Realm

class RebootReceiver : BroadcastReceiver(){

    @Override
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DEBUGGING LOG", "RebootReceiver::onReceive()")
        val realm = Realm.getDefaultInstance()
        val activeAlarms = AlarmDao(realm).getActiveAlarms()

        if (activeAlarms != null) {
            for (alarmData in activeAlarms) {
                AlarmTool.addAlarm(context, alarmData.alarmId, alarmData.hour, alarmData.minute)
            }
        }
    }
}