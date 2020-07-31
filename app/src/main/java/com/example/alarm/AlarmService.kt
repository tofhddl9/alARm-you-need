package com.example.alarm
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
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
        Log.d("AlarmService::","onStartCommand() is called")

        val alarmId = intent.getStringExtra("ALARM_ID")
        val realm = Realm.getDefaultInstance()
        val alarmData = AlarmDao(realm).selectAlarm(alarmId!!)

        /*For debug*/
        val uri = Uri.parse(alarmData.uriRingtone)
        val ringtone = RingtoneManager.getRingtone(this, uri)
        Log.d("ringtone : ", ringtone.getTitle(this))
        Log.d("volume : ", ""+alarmData.volume)

        mediaPlayer = MediaPlayer.create(this, Uri.parse(alarmData.uriRingtone))
        mediaPlayer.setVolume(1.0F*alarmData.volume, 1.0F*alarmData.volume)
        mediaPlayer.isLooping = true

        mediaPlayer.start()
        return START_STICKY

        /*운동 다녀와서
        todo
         volume 자료형 flaot으로 바꾸고 realm 충돌 확인
         요일 설정 강제할지
         AR 구현 시작해야겠다.
         */
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}