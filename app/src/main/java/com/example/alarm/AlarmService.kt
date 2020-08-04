package com.example.alarm
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import io.realm.Realm

class AlarmService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator

    override fun onCreate() {
        super.onCreate()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        Log.d("AlarmService::","onCreate() is called")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("AlarmService::", "onStartCommand() is called")

        val alarmId = intent.getStringExtra("ALARM_ID")
        val realm = Realm.getDefaultInstance()
        val alarmData = AlarmDao(realm).selectAlarm(alarmId!!)

        mediaPlayer = MediaPlayer.create(this, Uri.parse(alarmData.uriRingtone))
        mediaPlayer.setVolume(1.0F * alarmData.volume, 1.0F * alarmData.volume)
        mediaPlayer.isLooping = true

        /*
        * Todo : Make Vibration
        *  */
        vibrator.vibrate(3000)
        mediaPlayer.start()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d("AlarmService::", "onDestroy() is called")
        super.onDestroy()
        mediaPlayer.stop()
        vibrator.cancel()
    }
}