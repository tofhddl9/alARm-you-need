package com.example.alarm
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import kotlinx.android.synthetic.main.alarm_item.view.*
import java.util.*

class AlarmSettingViewModel: ViewModel() {

    val title: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    val hour: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 0 }
    val minute: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 0 }
    val alarmTime: MutableLiveData<Date> = MutableLiveData<Date>().apply { value = Date(0) }
    var apm: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    var sun: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var mon: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val tue: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val wed: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val thur: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val fri: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val sat: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val onoff: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val uriRingtone: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    val volume: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 0 }

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
        alarmTime.value = alarmData.alarmTime //
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

    fun onOffAlarm(id: String, onoff: Boolean) {
        alarmDao.onOffAlarm(alarmData, onoff)
    }

    fun addOrUpdateAlarm(context : Context, title: String, hour: Int, minute: Int, apm: String,
                         sun: Boolean, mon: Boolean, tue: Boolean, wed: Boolean, thur: Boolean,
                         fri: Boolean, sat: Boolean, onoff: Boolean, uriRingtone: String, volume: Int) {

        val alarmTimeValue = alarmTime.value!!
        alarmDao.addOrUpdateAlarm(alarmData, title, hour, minute, alarmTimeValue, apm,
            sun, mon, tue, wed, thur, fri, sat, onoff, uriRingtone, volume)

        //AlarmTool.deleteAlarm(context, alarmData.alarmId)
        if (alarmTimeValue.after(Date())) {
            //AlarmTool.addAlarm(context, alarmData.alarmId, alarmTimeValue)
        }
    }

    fun deleteAlarm(id: String) {
        alarmDao.deleteAlarm(id)
    }

}