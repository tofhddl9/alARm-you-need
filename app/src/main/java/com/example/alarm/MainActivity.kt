package com.example.alarm

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var alarmList : List<AlarmModel> = listOf()
    private lateinit var adapter : AlarmDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        make_alarm_btn.setOnClickListener {
            //Todo : Need to check at least one day checked
            val intent = Intent(this, AlarmSettingActivity::class.java)
            startActivityForResult(intent, 2)
        }

        setting_btn.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
        }

        adapter = AlarmDataAdapter(alarmList)
        alarm_list_view.adapter = adapter
        alarm_list_view.layoutManager = LinearLayoutManager(this)
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                2 -> {
                    var test_obj = data?.getSerializableExtra("alarmData") as? AlarmModel
                    if (test_obj != null) {
                        Log.d("MainActivity","alarmData appended")
                        alarmList += test_obj
                        adapter = AlarmDataAdapter(alarmList)
                        alarm_list_view.adapter = adapter
                        alarm_list_view.layoutManager = LinearLayoutManager(this)
                    }
                }
            }
        }
    }
}