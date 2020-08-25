package com.example.alarm
import android.util.Log
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*

class AlarmDao(private val realm: Realm) {
    fun getAllAlarms() : RealmResults<AlarmData> {
        return realm.where(AlarmData::class.java)
            .sort("onoff", Sort.DESCENDING)
            .findAll()
    }

    fun selectAlarm(id: String) : AlarmData {
        return realm.where(AlarmData::class.java)
            .equalTo("alarmId", id)
            .findFirst() as AlarmData
    }

    fun toggleAlarm(alarmData: AlarmData) {
        realm.executeTransaction {
            alarmData.onoff = !alarmData.onoff
            Log.d("AlarmDao::", "toggleAlarm() ... alarm is on now?" + alarmData.onoff)
        }
    }

    fun addOrUpdateAlarm(alarmData : AlarmData, title: String, hour: Int,  minute: Int,
                         apm: String, sun: Boolean, mon: Boolean, tue: Boolean, wed: Boolean,
                         thur: Boolean, fri: Boolean, sat: Boolean, onoff: Boolean,
                         uriRingtone: String, volume: Int, alarmType : AlarmType) {
        realm.executeTransaction {

            alarmData.title = title

            alarmData.hour = hour
            alarmData.minute = minute
            alarmData.apm = apm

            alarmData.sun = sun; alarmData.mon = mon; alarmData.tue = tue; alarmData.wed = wed
            alarmData.thur = thur; alarmData.fri = fri; alarmData.sat = sat

            alarmData.onoff = onoff
            alarmData.uriRingtone = uriRingtone
            alarmData.volume = volume
            alarmData.alarmType = alarmType

            if(!alarmData.isManaged) {
                it.copyToRealm(alarmData)
            }
        }
    }

    fun deleteAlarm(id: String) {
        realm.executeTransaction {
            val deleteItem = realm.where(AlarmData::class.java)
                .equalTo("alarmId", id)
                .findFirst() as AlarmData
            deleteItem.deleteFromRealm()
        }
    }

    fun getActiveAlarms(): RealmResults<AlarmData>? {
        return realm.where(AlarmData::class.java)
            .equalTo("onoff", true)
            //.and()
            .findAll()
    }
}