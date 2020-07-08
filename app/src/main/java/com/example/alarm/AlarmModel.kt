package com.example.alarm

import java.io.Serializable

class AlarmModel(var title:String, var time:String, var apm:String, var day:BooleanArray, var onoff:String) : Serializable{

}