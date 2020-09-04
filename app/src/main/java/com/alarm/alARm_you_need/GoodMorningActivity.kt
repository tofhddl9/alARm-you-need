package com.alarm.alARm_you_need

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_good_morning.*

class GoodMorningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_good_morning)

        val realm = Realm.getDefaultInstance()
        val alarmId = intent.getStringExtra("ALARM_ID")
        val alarmData = AlarmDao(realm).selectAlarm(alarmId!!)

        val title = alarmData.title
        val time = "${alarmData.hour}시 ${alarmData.minute}분입니다"
        /*Todo Fragment로 구현하자*/

        alarm_title.text = "ex) 잘 주무셨나요? 설마 다시 눕는건 아니겠죠?\n" + title
        clock.text = time

        back_btn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        gotoMain()
    }

    /* main으로 갈지 종료 popup을 띄울지*/
    private fun gotoMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}