package com.example.alarm

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_alarm_setting.*
import kotlinx.android.synthetic.main.activity_alarm_setting.alarm_title
import java.io.Serializable

class AlarmSettingActivity : AppCompatActivity() {

    private lateinit var alarmData : AlarmModel
    private var id : Int = 1
    private var days : BooleanArray = BooleanArray(7)
    private lateinit var time : String
    private lateinit var apm : String
    private var volume : Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_setting)
        time = "" + target_time.hour + ":" + target_time.minute
        apm = "오후"
        if (target_time.hour < 12 || target_time.hour == 24)
            apm = "오전"

        if (intent.hasExtra("alarmCode")) {
            val type = intent.getIntExtra("alarmCode", 0)
            alarmData = initAlarmData(type)
            Log.d("AlarmData.alarmID", ""+alarmData.alarmId)
        }

        back_btn.setOnClickListener {
            onBackPressed()
        }

        delete_btn.setOnClickListener {
            Log.d("delete button","is clicked")
            val builder = AlertDialog.Builder(ContextThemeWrapper(this@AlarmSettingActivity, R.style.Theme_AppCompat_Light_Dialog))
            builder.setTitle("알람 삭제")
            builder.setMessage("알람을 삭제하시겠습니까?")

            builder.setPositiveButton("확인") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("deleteAlarmId", alarmData.alarmId)
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
            builder.setNegativeButton("취소") { _, _ ->

            }
            builder.show()
        }

        save_btn.setOnClickListener {
            Log.d("AlarmSettingActivity", "save_btn clicked")
            setAlarmData(alarmData)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("alarmData", alarmData as Serializable)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        volume_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                volume = p1
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }
            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        OnClickTime()
    }

    private fun setAlarmData(alarmData: AlarmModel) {
        alarmData.title = alarm_title.text.toString()
        alarmData.time = time
        alarmData.apm = apm
        alarmData.day = days
        alarmData.onoff = true
        alarmData.volume = volume
    }

    private fun initAlarmData(type: Int): AlarmModel {
        when (type) {
            /* make new alarm */
            1 -> {
                id = intent.getIntExtra("alarmId", 0)
                alarmData = AlarmModel(id,"new alarm", time, apm, days, true, volume)
            }
            /* update alarm*/
            2 -> {
                alarmData = getExistingAlarmData()
            }
        }
        return alarmData
    }

    private fun getExistingAlarmData(): AlarmModel {
        var alarmModel = intent.getSerializableExtra("updatedData") as AlarmModel
        return alarmModel
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
            //var apm = ""

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
                val target_min = if (min < 10) "0" + min else min
                val msg = "$apm $target_hour 시 $target_min 분에 알람이 울립니다"
                //alarmData.apm = apm
                //alarmData.time = "$target_hour:$target_min"
                time = "$target_hour:$target_min"
                remainTimeView.text = msg
                remainTimeView.visibility = ViewGroup.VISIBLE
            }
        }
    }

}