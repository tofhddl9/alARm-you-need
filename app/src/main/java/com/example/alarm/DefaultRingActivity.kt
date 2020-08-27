package com.example.alarm
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper;
import kotlinx.android.synthetic.main.activity_default_ring.*

class DefaultRingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUGGING LOG", "DefaultRingActivity::onCreate is called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_ring)

        val alarmData = intent.getStringExtra("ALARM_ID")
        val serviceIntent = Intent(this, AlarmService::class.java)
        serviceIntent.putExtra("ALARM_ID", alarmData)
        startService(serviceIntent)

        alarm_off.setOnClickListener {
            val ringOffIntent = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(ringOffIntent)
            finish()
        }
    }

    @Override
    override fun onResume() {
        super.onResume()
    }


}