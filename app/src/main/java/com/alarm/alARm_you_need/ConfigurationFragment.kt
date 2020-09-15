package com.alarm.alARm_you_need

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class ConfigurationFragment : PreferenceFragmentCompat() {

    private var bStatusbarnotiswtich: Boolean = false
    private lateinit var channelId: String
    private val notificationId = 12

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_setting, rootKey)
        createNotificationChannel(
            requireContext(), NotificationManagerCompat.IMPORTANCE_DEFAULT,
            false, getString(R.string.app_name), "App notification channel"
        )
    }

    @Override
    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        Log.d("DEBUGGING LOG", "onPreferenceTreeClick()")
        if (preference.key == "pref_status_bar_notification") {
            Toast.makeText(requireContext(), "업데이트 예정입니다.", Toast.LENGTH_SHORT).show()
            /* todo : 앱과 함께 꺼지지 않도록 구현
            if (bStatusbarnotiswtich) {
                Log.d("DEBUGGING LOG", "show noti")
                hideStatusNotification()
                bStatusbarnotiswtich = false
            } else {
                Log.d("DEBUGGING LOG", "hide noti")
                showStatusNotification()
                bStatusbarnotiswtich = true
            }
             */
        }
        else if (preference.key == "pref_disturb_mode") {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
        else if (preference.key == "pref_onboarding") {
            val onboardingIntent = Intent(requireContext(), OnboardingActivity::class.java)
            onboardingIntent.putExtra("isReview", true)
            startActivity(onboardingIntent)
        }
        else if (preference.key == "pref_app_info") {
            val appInfoIntent = Intent(requireContext(), AppInfoActivity::class.java)
            startActivity(appInfoIntent)
        }

        return super.onPreferenceTreeClick(preference)
    }


    private fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean,
                                          name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showStatusNotification() {
        val title = "Alarm You Need"
        val content = "[다음 알람] "
        /* Todo : content show remaining time */

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(requireContext(), channelId)
        builder.setSmallIcon(R.drawable.ic_alarm)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setAutoCancel(false)
        builder.setContentIntent(pendingIntent)
        builder.setOngoing(true)

        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.notify(notificationId, builder.build())
    }

    private fun hideStatusNotification() {
        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.cancel(notificationId)
    }

}

