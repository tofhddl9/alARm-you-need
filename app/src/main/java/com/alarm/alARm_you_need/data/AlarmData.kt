package com.alarm.alARm_you_need

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

open class AlarmData(
    @PrimaryKey
    var alarmId: String =  UUID.randomUUID().toString(),
    var title:String = "",
    var hour:Int = 0,
    var minute:Int = 0,
    var apm:String = "",
    var sun:Boolean = false, var mon:Boolean = false, var tue:Boolean = false,
    var wed:Boolean = false, var thur:Boolean = false,
    var fri:Boolean = false, var sat:Boolean = false,
    var active:Boolean = true,
    var uriRingtone: String = "",
    var uriImage: String? = null,
    var volume: Int = 0,
    var alarmType: String = TYPE_DEFAULT) : RealmObject() {

    companion object {
        const val TYPE_DEFAULT = "DEFAULT"
        const val TYPE_AR = "AR"
    }
}