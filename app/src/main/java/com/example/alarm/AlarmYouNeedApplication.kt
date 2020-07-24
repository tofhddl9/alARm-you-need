package com.example.alarm

import android.app.Application
import io.realm.Realm

class AlarmYouNeedApplication() : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}