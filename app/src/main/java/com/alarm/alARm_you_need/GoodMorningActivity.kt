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
        val time = "${alarmData.hour}시 ${alarmData.minute}분입니다"

        alarm_title.text = "ex) 잘 주무셨나요? 설마 다시 눕는건 아니겠죠?\n" + title
        clock.text = time

        back_btn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        Log.d("DEBUGGING LOG", "GoodMorningActivity::onBackPressed()")
        finish()
        gotoMain()
    }

    /* todo
    * 기존 MainActivity를 MainFragment로 바꾸고
    * goodMorningActivity를 MainActivity 위의 GoodMorningFragment로 구현하여서 메인과 통합하자
    */
    private fun gotoMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}