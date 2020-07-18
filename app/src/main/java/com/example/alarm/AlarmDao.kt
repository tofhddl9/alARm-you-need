package com.example.alarm

import io.realm.Realm
import io.realm.RealmResults

class AlarmDao(private val realm: Realm) {
    fun getAllAlarms() : RealmResults<AlarmData>
}