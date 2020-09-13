package com.alarm.alARm_you_need

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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
    }
}