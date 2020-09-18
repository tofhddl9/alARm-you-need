package com.alarm.alARm_you_need

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.alarm.alARm_you_need.NotificationService.Companion.CHANNEL_ID

class ConfigurationFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_setting, rootKey)
    }

    @Override
    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        Log.d("DEBUGGING LOG", "onPreferenceTreeClick()")
        if (preference.key == "pref_status_bar_notification") {
            Toast.makeText(requireContext(), "업데이트 예정입니다.", Toast.LENGTH_SHORT).show()

            /*todo : make clean*/
            val sharedPreference = requireContext().getSharedPreferences("notifyPref", Context.MODE_PRIVATE)
            val isStatusbarNotificationSwitchOn = sharedPreference.getBoolean("isNotifying", false)

            if (!isStatusbarNotificationSwitchOn) {
                Log.d("DEBUGGING LOG", "hide noti")
                showStatusbarNotification()

                val editor = sharedPreference.edit()
                editor.putBoolean("isNotifying", true)
                editor.apply()
            } else {
                Log.d("DEBUGGING LOG", "show noti")
                hideStatusbarNotification()

                val editor = sharedPreference.edit()
                editor.putBoolean("isNotifying", false)
                editor.apply()
            }

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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID,
                "Notification Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun deleteNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
            notificationManager.deleteNotificationChannel(CHANNEL_ID)
        }
    }

    private fun showStatusbarNotification() {
        Log.d("DEBUGGING LOG", "showStatusNotification")
        createNotificationChannel()
        val notificationIntent = Intent(requireContext(), NotificationService::class.java)
        ContextCompat.startForegroundService(requireContext() , notificationIntent)
    }

    private fun hideStatusbarNotification() {
        Log.d("DEBUGGING LOG", "hideStatusNotification")
        val notificationIntent = Intent(requireContext(), NotificationService::class.java)
        requireContext().stopService(notificationIntent)

        deleteNotificationChannel()
    }

}

