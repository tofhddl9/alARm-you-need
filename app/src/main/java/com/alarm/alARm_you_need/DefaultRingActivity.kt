package com.alarm.alARm_you_need
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_default_ring.*

class DefaultRingActivity : BaseRingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onCreate is called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_ring)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adview_banner.loadAd(adRequest)

        alarm_off.setOnClickListener {
            alarmRingOff()
        }
    }

    override fun onResume() {
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onResume is called")
        super.onResume()
    }

    override fun onDestroy() {
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onDestroy is called")
        super.onDestroy()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onUserLeaveHint()")

        AlarmService.service?.let{
            val intent = Intent(this, DefaultRingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    override fun onStop() {
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onStop()")
        super.onStop()
    }

}