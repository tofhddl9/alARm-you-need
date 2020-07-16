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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_setting)

        loadAlarmSetting()

        back_btn.setOnClickListener {
            /*todo : need to debug*/
            //onBackPressed()
            finish()
        }

        delete_btn.setOnClickListener {
            Log.d("delete button","is clicked")
            showConfirmAlert()
        }

        save_btn.setOnClickListener {
            Log.d("AlarmSettingActivity", "save_btn clicked")
            alarmData.title = alarm_title.text.toString()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("alarmData", alarmData as Serializable)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        volume_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                alarmData.volume = p1
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }
            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        OnClickTime()
    }

    private fun loadAlarmSetting() {
        if (intent.hasExtra("alarmCode")) {
            val type = intent.getIntExtra("alarmCode", 0)
            alarmData = initAlarmData(type)
            setAlarmView(alarmData)
        }
    }

    private fun initAlarmData(type: Int): AlarmModel {
        when (type) {
            /* make new alarm */
            1 -> {
                val id = intent.getIntExtra("alarmId", 0)
                alarmData = newAlarmData(id)
            }
            /* update alarm*/
            2 -> {
                alarmData = loadAlarmData()
            }
        }
        return alarmData
    }

    private fun newAlarmData(id : Int): AlarmModel {
        var alarmData = AlarmModel(id, "", 8, 0,"", BooleanArray(7), true, 5)
        return alarmData
    }

    private fun loadAlarmData(): AlarmModel {
        var alarmData = intent.getSerializableExtra("updatedData") as AlarmModel
        return alarmData
    }

    private fun setAlarmView(alarmData : AlarmModel) {
        timePicker.hour = alarmData.hour
        timePicker.minute = alarmData.minute

        alarm_title.setText(alarmData.title)

        sun_box.isChecked = alarmData.day[0]
        mon_box.isChecked = alarmData.day[1]
        tue_box.isChecked = alarmData.day[2]
        wed_box.isChecked = alarmData.day[3]
        thur_box.isChecked = alarmData.day[4]
        fri_box.isChecked = alarmData.day[5]
        sat_box.isChecked = alarmData.day[6]

        volume_bar.progress = alarmData.volume
    }

    private fun showConfirmAlert() {
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

    fun OnCheckboxClicked(view : View) {
        if (view is CheckBox) {
            val checked : Boolean = view.isChecked

            when (view.id) {
                R.id.sun_box -> alarmData.day[0] = checked
                R.id.mon_box -> alarmData.day[1] = checked
                R.id.tue_box -> alarmData.day[2] = checked
                R.id.wed_box -> alarmData.day[3] = checked
                R.id.thur_box -> alarmData.day[4] = checked
                R.id.fri_box -> alarmData.day[5] = checked
                R.id.sat_box -> alarmData.day[6] = checked
            }
        }
    }

    private fun OnClickTime() {
        val remainTimeView = remain_time_view
        timePicker.setOnTimeChangedListener { _, hour, minute ->

            alarmData.hour = hour
            alarmData.minute = minute

            // AM_PM decider logic
            if (hour/12 > 0) {
                alarmData.apm = "오후"
                if (hour != 12) {
                    alarmData.hour %= 12
                }
            } else {
                alarmData.apm = "오전"
            }

            if (remainTimeView != null) {
                //Todo [Late] : calculate upcoming time
                //remainTimeView.visibility = ViewGroup.VISIBLE
            }
        }
    }

}