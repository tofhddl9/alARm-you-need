package com.alarm.alARm_you_need

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_good_morning.*

class GoodMorningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUGGING LOG", "GoodMorningActivity::onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_good_morning)

        val realm = Realm.getDefaultInstance()
        val alarmId = intent.getStringExtra("ALARM_ID")
        val alarmData = AlarmDao(realm).selectAlarm(alarmId!!)

        val title = alarmData.title
        val apm = alarmData.apm
        val time = "${apm} ${alarmData.hour}시 ${alarmData.minute}분"

        alarm_title.text = title
        clock.text = time
        AlarmTool.updateAlarmNotification(this)
    }

    override fun onBackPressed() {
        Log.d("DEBUGGING LOG", "GoodMorningActivity::onBackPressed()")
        finish()
        gotoMain()
    }

    /* todo
    * 일어났을 때 유용한 정보를 제공한다면 (시간, 날씨, 일정 등)
    * goodMorningActivity를 MainActivity 위의 GoodMorningFragment로 구현하여서 메인과 통합하자
    */
    private fun gotoMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}