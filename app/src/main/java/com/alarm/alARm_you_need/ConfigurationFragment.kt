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

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        Log.d("DEBUGGING LOG", "onPreferenceTreeClick()")
        when (preference.key) {
            "pref_status_bar_notification", "pref_always_max_volume"-> {
                toggleSwitchPreference(preference.key)
            }
            "pref_disturb_mode"-> {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
            }
            "pref_onboarding"-> {
                val onboardingIntent = Intent(requireContext(), OnboardingActivity::class.java)
                onboardingIntent.putExtra("isReview", true)
                startActivity(onboardingIntent)
            }
            "pref_app_info"-> {
                val appInfoIntent = Intent(requireContext(), AppInfoActivity::class.java)
                startActivity(appInfoIntent)
            }
        }

        return super.onPreferenceTreeClick(preference)
    }

    private fun toggleSwitchPreference(key: String) {
        val sharedPreference =
            requireContext().getSharedPreferences("configurationPreference", Context.MODE_PRIVATE)
        val isSwitchOn = sharedPreference.getBoolean(key, false)

        val editor = sharedPreference.edit()
        editor.putBoolean(key, !isSwitchOn)
        editor.apply()

        when (key) {
            "pref_status_bar_notification" -> {
                toggleNotification(!isSwitchOn)
            }
            "pref_always_max_volume" -> {
                //nothing to do
            }
        }
    }

    private fun toggleNotification(isSwitchOn: Boolean) {
        if (isSwitchOn)
            showNotification()
        else
            hideNotification()
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

