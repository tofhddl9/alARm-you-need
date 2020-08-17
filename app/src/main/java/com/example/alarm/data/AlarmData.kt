package com.example.alarm

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
    var onoff:Boolean = true,
    var uriRingtone: String = "",
    var volume : Int = 0) : RealmObject(), Serializable {

}