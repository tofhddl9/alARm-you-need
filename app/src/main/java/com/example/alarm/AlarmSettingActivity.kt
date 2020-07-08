package com.example.alarm

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_alarm_setting.*
import java.io.Serializable

class AlarmSettingActivity : AppCompatActivity() {

    private lateinit var alarmData : AlarmModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_setting)

        //For Test
        alarmData = AlarmModel("it is for test!","11:00", "pm", "Tue", "On")

        back_btn.setOnClickListener {
            onBackPressed()
            //finish()
        }

        cancel_btn.setOnClickListener {

        }

        save_btn.setOnClickListener {
            Log.d("AlarmSettingActivity", "save_btn clicked")
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("alarmData", alarmData as Serializable)
//            startActivityForResult(intent, 2)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        //OnClickTime()
    }

    /*
    private fun OnClickTime() {
        val remainTimeVew = findViewById<TextView>(R.id.remain_time_view)
        val targetTime = findViewById<TimePicker>(R.id.target_time)
        targetTime.setOnTimeChangedListener { _, hour, minute ->
            var hour = hour
            var min = minute
            var am_pm = ""

            // AM_PM decider logic
            when {hour == 0 -> { hour += 12
                am_pm = "AM"
            }
                hour == 12 -> am_pm = "PM"
                hour > 12 -> { hour -= 12
                    am_pm = "PM"
                }
                else -> am_pm = "AM"
            }
            if (remainTimeVew != null) {
                val target_hour = if (hour < 10) "0" + hour else hour

                val target_min = if (minute < 10) "0" + minute else minute
                // display format of time
                val msg = "$hour 시 $min 분에 알람이 울립니다"
                remainTimeVew.text = msg
                //textView.visibility = ViewGroup.VISIBLE
            }
        }
    }*/

}