package com.example.alarm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /*todo : need to introduce realm*/

    private var alarmList : ArrayList<AlarmData> = ArrayList()
    private lateinit var adapter : AlarmDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        make_alarm_btn.setOnClickListener {
            adapter.updateAlarm(1, 0)
        }

        setting_btn.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
        }

        adapter = AlarmDataAdapter(this, alarmList)
        alarm_list_view.adapter = adapter
        alarm_list_view.layoutManager = LinearLayoutManager(this)
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            //val alarmId = intent.getIntExtra("alarmId", 0)
            when (requestCode) {
                /*make new alarm*/
                1 -> {
                    Log.d("resultCode", "1")
                    var test_obj = data?.getSerializableExtra("alarmData") as? AlarmData
                    if (test_obj != null) {
                        Log.d("newID is assigned", ""+test_obj.alarmId)
                        adapter.alarmList.add(test_obj)
                        adapter.notifyDataSetChanged()
                    }
                }
                /*update new alarm*/
                2 -> {
                    Log.d("resultCode", "2")
                    var test_obj = data?.getSerializableExtra("alarmData") as? AlarmData
                    if (test_obj != null) {
                        Log.d("updated view id", ""+test_obj.alarmId)
                        adapter.alarmList[test_obj.alarmId] = test_obj
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
        /* delete alarm */
        else if (resultCode == Activity.RESULT_CANCELED) {
            when (requestCode) {
                2 -> {
                    var deleteId = data?.getIntExtra("deleteAlarmId", 0) as Int
                    Log.d("request : delete", ""+ deleteId)
                    if (deleteId != -1) {
                        adapter.alarmList.removeAt(deleteId)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

}