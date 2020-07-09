package com.example.alarm

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import kotlinx.android.synthetic.main.activity_alarm_setting.*
import java.io.Serializable
import java.time.DayOfWeek

class AlarmSettingActivity : AppCompatActivity() {

    private lateinit var alarmData : AlarmModel
    private var days : BooleanArray = BooleanArray(7)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_setting)

        //For Test
        alarmData = AlarmModel("it is for test!","11:00", "pm", days, "ON")

        //Button setOnClickListeners

        back_btn.setOnClickListener {
            onBackPressed()
        }

        cancel_btn.setOnClickListener {

        }

        save_btn.setOnClickListener {
            Log.d("AlarmSettingActivity", "save_btn clicked")
            val intent = Intent(this, MainActivity::class.java)
            alarmData.title = alarm_title.text.toString()
            intent.putExtra("alarmData", alarmData as Serializable)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        OnClickTime()
    }

    fun OnCheckboxClicked(view : View) {
        if (view is CheckBox) {
            val checked : Boolean = view.isChecked

            when (view.id) {
                R.id.sun_box -> {
                    days[0] = checked
                }
                R.id.mon_box -> {
                    days[1] = checked
                }
                R.id.tue_box -> {
                    days[2] = checked
                }
                R.id.wed_box -> {
                    days[3] = checked
                }
                R.id.thur_box -> {
                    days[4] = checked
                }
                R.id.fri_box -> {
                    days[5] = checked
                }
                R.id.sat_box -> {
                    days[6] = checked
                }
            }
        }
    }

    private fun OnClickTime() {
        val remainTimeView = remain_time_view
        val timePicker = target_time
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            var hour = hour
            var min = minute
            var apm = ""

            // AM_PM decider logic
            if (hour/12 > 0) {
                apm = "오후"
                if (hour != 12) {
                    hour %= 12
                }
            } else {
                apm = "오전"
            }

            if (remainTimeView != null) {
                //Todo : calculate upcoming time
                val target_hour = if (hour < 10) "0" + hour else hour
                val target_min = if (minute < 10) "0" + minute else minute
                val msg = "$apm $target_hour 시 $target_min 분에 알람이 울립니다"
                alarmData.apm = apm
                alarmData.time = "$target_hour:$target_min"
                remainTimeView.text = msg
                remainTimeView.visibility = ViewGroup.VISIBLE
            }
        }
    }

}