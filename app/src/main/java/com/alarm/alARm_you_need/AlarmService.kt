package com.alarm.alARm_you_need

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import io.realm.Realm
import java.util.*

class AlarmService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator
    private lateinit var alarmId: String
    private val ACTION_RUN_ALARM = "RUN_ALARM"

    companion object {
        var service: Intent? = null
        var normalExit: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("DEBUGGING LOG", "AlarmService::onCreate() is called")
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        normalExit = false
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("DEBUGGING LOG", "AlarmService::onStartCommand() is called")

        service = intent
        alarmId = intent.getStringExtra("ALARM_ID")!!
        val realm = Realm.getDefaultInstance()
        val alarmData = AlarmDao(realm).selectAlarm(alarmId)
        val sharedPreferences =
            getSharedPreferences("configurationPreference", Context.MODE_PRIVATE)
        val isAlwaysMaxVolumeSet = sharedPreferences.getBoolean("pref_always_max_volume", false)

        mediaPlayer = MediaPlayer.create(this, Uri.parse(alarmData.uriRingtone))
        mediaPlayer.isLooping = true

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (isAlwaysMaxVolumeSet) {
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                15,
                AudioManager.FLAG_PLAY_SOUND
            )
        } else {
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                alarmData.volume,
                AudioManager.FLAG_PLAY_SOUND
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(500, 500), 0))
        else
            vibrator.vibrate(longArrayOf(500, 500), 0)
        mediaPlayer.start()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d("DEBUGGING LOG", "AlarmService::onDestroy() is called")
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        vibrator.cancel()

        if (!normalExit) {
            setAlarmTimer()
        }
    }

    private fun setAlarmTimer() {
        Log.d("DEBUGGING LOG", "AlarmService::setAlarmTimer() is called")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, 1)

        val intent = Intent(this, AlarmTool::class.java)
        intent.putExtra("ALARM_ID", alarmId)
        intent.action = ACTION_RUN_ALARM
        val sender = PendingIntent.getBroadcast(this, 0, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            sender
        )
    }
}


