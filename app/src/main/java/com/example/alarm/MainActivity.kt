package com.example.alarm

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        make_alarm_btn.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
        }

        setting_btn.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)

        }

        // Example
        val alarmList = listOf(
            AlarmModel("기상","11:30","오후","월,화,목","ON"),
            AlarmModel("일이삼사오육칠팔구십일이삼사","14:30","오후","월,화,금,토","OFF")
        )

        val adapter = AlarmDataAdapter(alarmList)
        alarm_list_view.adapter = adapter
        alarm_list_view.layoutManager = LinearLayoutManager(this)
    }
}