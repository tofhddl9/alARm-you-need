package com.alarm.alARm_you_need

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class BaseRingActivity : AppCompatActivity() {

    open lateinit var alarmId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUGGING LOG", "BaseRingActivity::onCreate is called")
        super.onCreate(savedInstanceState)
        AlarmTool.hideAlarmNotification(this)
        alarmId = intent.getStringExtra("ALARM_ID")!!

        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    open fun alarmRingOff() {
        Log.d("DEBUGGING LOG", "BaseRingActivity::AlarmRingOff()")
        AlarmService.service?.let {
            stopService(it)
            AlarmService.normalExit = true
        }
        finish()

        val intent = Intent(this, GoodMorningActivity::class.java)
        intent.putExtra("ALARM_ID", alarmId)
        startActivity(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN-> {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,/* it may be helpful >_<*/
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
                return true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
                return true
            }
            else -> {
                return super.onKeyDown(keyCode, event)
            }
        }
    }

    override fun onBackPressed() {
        //nop
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DEBUGGING LOG", "BaseRingActivity::onDestroy() status:${AlarmService.normalExit}")
        AlarmService.service?.let {
            stopService(it)
        }
    }

}