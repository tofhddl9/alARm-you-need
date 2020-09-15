package com.alarm.alARm_you_need

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity

open class BaseRingActivity : AppCompatActivity() {

    open var alarmId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUGGING LOG", "BaseRingActivity::onCreate is called")
        super.onCreate(savedInstanceState)
        alarmId = intent.getStringExtra("ALARM_ID")
    }

    open fun alarmRingOff() {
        Log.d("DEBUGGING LOG", "BaseRingActivity::AlarmRingOff()")
        stopService(AlarmService.service)
        AlarmService.service = null
        AlarmService.normalExit = true
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
        Log.d("DEBUGGING LOG", "BaseRingActivity::onDestroy is called")

        if (!AlarmService.normalExit) {
            Log.d("DEBUGGING LOG", "BaseRingActivity ...  abnormal exit")
            if (AlarmService.service != null) {
                stopService(AlarmService.service)
            }
        }
        else {
            Log.d("DEBUGGING LOG", "BaseRingActivity ... normal exit")
            if (AlarmService.service != null) {
                stopService(AlarmService.service)
                AlarmService.service = null
            }
        }
    }

}