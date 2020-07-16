package com.example.alarm

import java.io.Serializable

class AlarmModel(var alarmId : Int, var title:String, var hour:Int, var minute:Int, var apm:String,
                 var day:BooleanArray, var onoff:Boolean, var volume : Int) : Serializable {

}