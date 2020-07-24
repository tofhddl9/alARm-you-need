package com.example.alarm
import androidx.lifecycle.ViewModel
import io.realm.Realm

class ListViewModel : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val alarmDao: AlarmDao by lazy {
        AlarmDao(realm)
    }

    val alarmLiveData: RealmLiveData<AlarmData> by lazy {
        RealmLiveData(alarmDao.getAllAlarms())
    }

    private var alarmData = AlarmData()

    fun toggleAlarm(id: String, onoff: Boolean) {
        alarmData = alarmDao.selectAlarm(id)
        alarmDao.onOffAlarm(alarmData, onoff)
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}