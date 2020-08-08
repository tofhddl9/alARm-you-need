package com.example.alarm
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import kotlinx.android.synthetic.main.activity_ring.*;
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper;

class RingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("RingActivity::onCreate", "is called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ring)

        val alarmData = intent.getStringExtra("ALARM_ID")
        val serviceIntent = Intent(this, AlarmService::class.java)
        serviceIntent.putExtra("ALARM_ID", alarmData)
        startService(serviceIntent)

        alarm_off.setOnClickListener {
            val ringOffIntent = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(ringOffIntent)
            finish()
        }

        maybeEnableArButton()
    }

    @Override
    override fun onResume() {
        super.onResume()

        // ARCore requires camera permission to operate.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,  permissions: Array<out String>,  grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults!!)
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(
                this,
                "Camera permission is needed to run this application",
                Toast.LENGTH_LONG
            )
                .show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }

    private fun maybeEnableArButton() {
        val availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient) {
            // Re-query at 5Hz while compatibility is checked in the background.
            Handler().postDelayed({
                run {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported) {
            alarm_off.visibility = View.VISIBLE;
            alarm_off.isEnabled = true;
            // indicator on the button.
        } else { // Unsupported or unknown.
            alarm_off.visibility = View.INVISIBLE;
            alarm_off.isEnabled = false;
        }
    }
}