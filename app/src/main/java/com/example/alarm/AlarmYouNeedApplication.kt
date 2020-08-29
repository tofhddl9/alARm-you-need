package com.example.alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.realm.Realm

class AlarmYouNeedApplication() : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Log.d("DEBUGGING LOG", "AlarmYouNeedApplication")
        /*
        createNotificationChannel(this,  NotificationManagerCompat.IMPORTANCE_DEFAULT,
            false, getString(R.string.app_name), "App notification channel")
        val channelId = "$packageName-${getString(R.string.app_name)}"
        val title = "Alarm You Need"
        val content = "[다음 알람] "
        //@ Todo : content show remaining time

        val intent = Intent(baseContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent =
            PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, channelId)
        builder.setSmallIcon(R.drawable.fit_to_scan)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setAutoCancel(false)
        builder.setContentIntent(pendingIntent)
        builder.setOngoing(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, builder.build())
        */
    }

    /*
    private fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean,
                                          name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    */
}

/* Todo List
* - 활성화 알람 시간 순 정렬
* - 다음 알람까지 남은 시간 계산
* - 설정 페이지 구현
* - AR 사진 설정 구현
*
* */
