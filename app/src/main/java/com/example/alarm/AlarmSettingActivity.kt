package com.example.alarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_alarm_setting.*

class AlarmSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_setting)

        back_btn.setOnClickListener {
            onBackPressed()
        }

        cancel_btn.setOnClickListener {

        }

        save_btn.setOnClickListener {

        }
    }
}