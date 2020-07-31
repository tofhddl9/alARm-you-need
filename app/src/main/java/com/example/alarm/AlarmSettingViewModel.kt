package com.example.alarm
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm

class AlarmSettingViewModel: ViewModel() {

    val title: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    val hour: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 8 }
    val minute: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 30 }
    val apm: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    val sun: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }
    val mon: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }
    val tue: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }
    val wed: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }
    val thur: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }
    val fri: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }
    val sat: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }
    val onoff: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }

    val defaultUriRingtone: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    var uriRingtone: MutableLiveData<String> = MutableLiveData<String>().apply { value = defaultUriRingtone.toString()}
    val volume: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 50 }

    private var alarmData = AlarmData()

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val alarmDao: AlarmDao by lazy {
        AlarmDao(realm)
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun loadAlarm(id: String) {
        Log.d("loadAlarm : ", "Alarm is Loaded")
        alarmData = alarmDao.selectAlarm(id)
        title.value = alarmData.title
        hour.value = alarmData.hour
        minute.value = alarmData.minute
        apm.value = alarmData.apm

        sun.value = alarmData.sun
        mon.value = alarmData.mon
        tue.value = alarmData.tue
        wed.value = alarmData.wed
        thur.value = alarmData.thur
        fri.value = alarmData.fri
        sat.value = alarmData.sat

        onoff.value = alarmData.onoff
        uriRingtone.value = alarmData.uriRingtone
        volume.value = alarmData.volume
    }

    fun addOrUpdateAlarm(context : Context, title: String, hour: Int, minute: Int, apm: String,
                         sun: Boolean, mon: Boolean, tue: Boolean, wed: Boolean, thur: Boolean,
                         fri: Boolean, sat: Boolean, onoff: Boolean, uriRingtone: String, volume: Int) {

        alarmDao.addOrUpdateAlarm(alarmData, title, hour, minute, apm,
            sun, mon, tue, wed, thur, fri, sat, onoff, uriRingtone, volume)

        AlarmTool.deleteAlarm(context, alarmData.alarmId)
        AlarmTool.addAlarm(context, alarmData.alarmId, alarmData.hour, alarmData.minute)
    }

    fun deleteAlarm(id: String) {
        alarmDao.deleteAlarm(id)
    }

}