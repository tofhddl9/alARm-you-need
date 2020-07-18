package com.example.alarm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class AlarmData(
    @PrimaryKey
    var alarmId : Int,
    var title:String,
    var hour:Int,
    var minute:Int,
    var apm:String,
    var day:BooleanArray,
    var onoff:Boolean,
    var uriRingtone: String,
    var volume : Int) : RealmObject(), Serializable {

}