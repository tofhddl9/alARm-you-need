package com.example.alarm

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alarm_item.*
import kotlinx.android.synthetic.main.alarm_item.view.*

class MainActivity : AppCompatActivity() {

    private var alarmList : ArrayList<AlarmModel> = ArrayList()
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

        alarmList.add(AlarmModel("sample", "11:00", "오후", BooleanArray(7), true, 10))
        adapter = AlarmDataAdapter(alarmList)
        alarm_list_view.adapter = adapter
        alarm_list_view.layoutManager = LinearLayoutManager(this)

        adapter.onItemSelectionChangedListener = {
            val intent = Intent(this, AlarmSettingActivity::class.java)
            startActivityForResult(intent, 1)
        }



    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                2 -> {
                    var test_obj = data?.getSerializableExtra("alarmData") as? AlarmModel
                    if (test_obj != null) {
                        Log.d("MainActivity","alarmData appended")
                        adapter.alarmList.add(test_obj)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

}