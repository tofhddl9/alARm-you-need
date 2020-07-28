package com.example.alarm
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
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
        Log.d("AlarmService::","onStartCommand() is called")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val alarmId = intent.getStringExtra("ALARM_ID")
        val realm = Realm.getDefaultInstance()
        val alarmData = AlarmDao(realm).selectAlarm(alarmId!!)

        /*For debug*/
        val uri = Uri.parse(alarmData.uriRingtone)
        val ringtone = RingtoneManager.getRingtone(this, uri)
        Log.d("ringtone : ", ringtone.getTitle(this))
        Log.d("volume : ", ""+alarmData.volume)

        mediaPlayer = MediaPlayer.create(this, Uri.parse(alarmData.uriRingtone))
        mediaPlayer.setVolume(1.0F*alarmData.volume,1.0F*alarmData.volume)
        mediaPlayer.isLooping = true

        mediaPlayer.start()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}