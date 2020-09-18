package com.alarm.alARm_you_need

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val isReview = intent.getBooleanExtra("isReview", false)
        if (isOnboardingOpenedBefore() && !isReview) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val list = ArrayList<ScreenItem>()
        list.add(ScreenItem(resources.getString(R.string.onboarding1_title), resources.getString(R.string.onboarding1_desc), R.drawable.onboarding_img1))
        list.add(ScreenItem(resources.getString(R.string.onboarding2_title), resources.getString(R.string.onboarding2_desc), R.drawable.onboarding_img2))
        list.add(ScreenItem(resources.getString(R.string.onboarding3_title), resources.getString(R.string.onboarding3_desc), R.drawable.onboarding_img3))
        list.add(ScreenItem(resources.getString(R.string.onboarding4_title), resources.getString(R.string.onboarding4_desc), R.drawable.onboarding_img4))
        list.add(ScreenItem(resources.getString(R.string.onboarding5_title), resources.getString(R.string.onboarding5_desc), R.drawable.onboarding_img5))

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
            if (isReview)
                onBackPressed()
            else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                saveIsOnboardingOpenedBeforeTrue()
            }

        }
    }

    private fun isOnboardingOpenedBefore(): Boolean {
        val sharedPreference = applicationContext.getSharedPreferences("onboardingPref", Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("isOnboardingOpenedBefore", false)
    }

    private fun saveIsOnboardingOpenedBeforeTrue() {
        val sharedPreference = applicationContext.getSharedPreferences("onboardingPref", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean("isOnboardingOpenedBefore", true)
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