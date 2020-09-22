package com.alarm.alARm_you_need

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class ConfigurationFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_setting, rootKey)
    }

    @Override
    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        Log.d("DEBUGGING LOG", "onPreferenceTreeClick()")
        if (preference.key == "pref_status_bar_notification") {

            val sharedPreference =
                requireContext().getSharedPreferences("notifyPref", Context.MODE_PRIVATE)
            val isNotificationSwitchOn = sharedPreference.getBoolean("isNotifying", false)
            val editor = sharedPreference.edit()

            if (!isNotificationSwitchOn) {
                Log.d("DEBUGGING LOG", "hide noti")
                editor.putBoolean("isNotifying", true)
                editor.apply()
                showNotification()
            } else {
                Log.d("DEBUGGING LOG", "show noti")
                editor.putBoolean("isNotifying", false)
                editor.apply()
                hideNotification()
            }
        } else if (preference.key == "pref_disturb_mode") {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        } else if (preference.key == "pref_onboarding") {
            val onboardingIntent = Intent(requireContext(), OnboardingActivity::class.java)
            onboardingIntent.putExtra("isReview", true)
            startActivity(onboardingIntent)
        } else if (preference.key == "pref_app_info") {
            val appInfoIntent = Intent(requireContext(), AppInfoActivity::class.java)
            startActivity(appInfoIntent)
        }

        return super.onPreferenceTreeClick(preference)
    }

    private fun showNotification() {
        Log.d("DEBUGGING LOG", "showStatusNotification")
        AlarmTool.showUpcomingAlarmNotification(requireContext())
    }

    private fun hideNotification() {
        Log.d("DEBUGGING LOG", "hideStatusNotification")
        AlarmTool.hideUpcomingAlarmNotification(requireContext())
    }

}

