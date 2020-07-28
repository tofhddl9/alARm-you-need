package com.example.alarm
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_ring.*

class RingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ring)

        val alarmData = intent.getStringExtra("ALARM_ID")

        val intent = Intent(this, AlarmService::class.java)
        intent.putExtra("ALARM_ID", alarmData)
        startService(intent)
        Log.d("RingActivity::onCreate", "is called")

        alarm_off.setOnClickListener { view ->
            finish()
        }
    }
}