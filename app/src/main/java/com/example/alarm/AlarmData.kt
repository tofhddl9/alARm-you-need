package com.example.alarm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

open class AlarmData(
    @PrimaryKey
    var alarmId : Int = 0,
    var title:String = "",
    var hour:Int = 12,
    var minute:Int = 34,
    var apm:String = "",
    var day:ByteArray = ByteArray(7),
    var onoff:Boolean = true,
    var uriRingtone: String = "",
    var volume : Int = 5) : RealmObject(), Serializable {

}