package com.example.alarm
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_default_ring.*

class DefaultRingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onCreate is called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_ring)

        alarm_off.setOnClickListener {
            AlarmRingOff()
        }

        if (supportActionBar != null) {
            Log.d("DEBUGGING LOG", "hide back button")
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.setHomeButtonEnabled(false)
        }
    }

    fun AlarmRingOff() {
        stopService(AlarmService.service)
        AlarmService.service = null
        AlarmService.normalExit = true

        val int = Intent(this, MainActivity::class.java)
        startActivity(int)
        /*todo : GoodMorningActivity로 바꾸기
        * */
    }

    @Override
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,/* it may be helpful >_<*/
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
                return true;
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
                return true;
            }
            else -> {
                return super.onKeyDown(keyCode, event)
            }
        }
    }

    @Override
    override fun onBackPressed() {
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onBackPressed()");
        //nop
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onUserLeaveHint()")
        if (AlarmService.service != null) {
            val intent = Intent(this, DefaultRingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    @Override
    override fun onResume() {
        super.onResume()
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onResume is called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onDestroy is called")

        if (!AlarmService.normalExit) {
            Log.d("DEBUGGING LOG", "DefaultRingActivity ...  abnormal exit")
            if (AlarmService.service != null) {
                stopService(AlarmService.service)
            }
        }
        else {
            Log.d("DEBUGGING LOG", "DefaultRingActivity ... normal exit")
            if (AlarmService.service != null) {
                stopService(AlarmService.service)
                AlarmService.service = null
            }
        }
    }

}