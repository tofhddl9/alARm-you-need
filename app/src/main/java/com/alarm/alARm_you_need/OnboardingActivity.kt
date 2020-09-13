package com.alarm.alARm_you_need

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (restorePreferenceData()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        Thread.sleep(1500)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_onboarding)

        val list = ArrayList<ScreenItem>()
        list.add(ScreenItem("title1", "desc1", R.drawable.ic_alarm))
        list.add(ScreenItem("title2", "desc2", R.drawable.back_btn))
        list.add(ScreenItem("title3", "desc3", R.drawable.cancel_btn))

        val onboardingAdapter = OnboardingViewpagerAdapter(list)
        screen_viewpager.adapter = onboardingAdapter
        TabLayoutMediator(tab_indicator, screen_viewpager) {tab, position->

        }.attach()

        btn_next.setOnClickListener {
            var position = screen_viewpager.currentItem
            if (position < list.size) {
                position++
                screen_viewpager.currentItem = position
            }
            if (position == list.size - 1) {
                loadLastScreen()
            }
        }

        tab_indicator.addOnTabSelectedListener (object : OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                TODO("Not yet implemented")
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == list.size -1) {
                    loadLastScreen()
                }
            }
        })

        btn_get_started.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            savePreferenceData()
        }
    }

    private fun restorePreferenceData(): Boolean {
        val sharedPreference = applicationContext.getSharedPreferences("onboardingPref", Context.MODE_PRIVATE)

        return sharedPreference.getBoolean("isOnboardingOpened", false)
    }

    private fun savePreferenceData() {
        val sharedPreference = applicationContext.getSharedPreferences("onboardingPref", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean("isOnboardingOpened", true)
        editor.apply()
    }

    private fun loadLastScreen() {
        btn_next.visibility = View.INVISIBLE
        tab_indicator.visibility = View.INVISIBLE
        btn_get_started.visibility = View.VISIBLE

        val btnAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)
        btn_get_started.animation = btnAnimation
    }
}