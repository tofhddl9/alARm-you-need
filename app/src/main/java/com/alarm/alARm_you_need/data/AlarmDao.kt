package com.alarm.alARm_you_need
import android.util.Log
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class AlarmDao(private val realm: Realm) {

    fun getAllAlarms() : RealmResults<AlarmData> {
        return realm.where(AlarmData::class.java)
            .sort("active", Sort.DESCENDING)
            .findAll()
    }

    fun selectAlarm(id: String) : AlarmData {
        return realm.where(AlarmData::class.java)
            .equalTo("alarmId", id)
            .findFirst() as AlarmData
    }

    fun toggleAlarm(alarmData: AlarmData) {
        realm.executeTransaction {
            alarmData.active = !alarmData.active
            Log.d("AlarmDao::", "toggleAlarm() ... alarm is on now?" + alarmData.active)
        }
    }

    fun addOrUpdateAlarm(alarmData : AlarmData, title: String, hour: Int,  minute: Int,
                         apm: String, sun: Boolean, mon: Boolean, tue: Boolean, wed: Boolean,
                         thur: Boolean, fri: Boolean, sat: Boolean, active: Boolean,
                         uriRingtone: String, uriImage: String?, volume: Int, alarmType : String) {
        realm.executeTransaction {

            alarmData.title = title

            alarmData.hour = hour
            alarmData.minute = minute
            alarmData.apm = apm

            alarmData.sun = sun; alarmData.mon = mon; alarmData.tue = tue; alarmData.wed = wed
            alarmData.thur = thur; alarmData.fri = fri; alarmData.sat = sat

            alarmData.active = active
            alarmData.uriRingtone = uriRingtone
            if (uriImage != null)
                alarmData.uriImage = uriImage
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
            .equalTo("active", true)
            //.and()
            .findAll()
    }
}