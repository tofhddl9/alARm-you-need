package com.example.alarm

import android.content.Intent
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

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        setting_btn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val alarmList = listOf(
            AlarmModel("기상","11:30","오후","월,화,목","ON"),
            AlarmModel("일이삼사오육칠팔구십일이삼사","14:30","오후","월,화,금,토","OFF")
        )

        val adapter = AlarmDataAdapter(alarmList)
        alarm_list_view.adapter = adapter
        alarm_list_view.layoutManager = LinearLayoutManager(this)
    }
}