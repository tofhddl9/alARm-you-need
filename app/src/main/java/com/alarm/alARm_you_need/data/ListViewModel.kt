package com.alarm.alARm_you_need
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

    fun toggleAlarm(id: String) {
        alarmData = alarmDao.selectAlarm(id)
        alarmDao.toggleAlarm(alarmData)
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}