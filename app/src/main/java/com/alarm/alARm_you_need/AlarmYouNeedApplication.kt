package com.alarm.alARm_you_need

import android.app.*
import android.util.Log
import io.realm.Realm

class AlarmYouNeedApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Log.d("DEBUGGING LOG", "AlarmYouNeedApplication")

        Thread.sleep(1500)
        setTheme(R.style.AppTheme)
    }
}