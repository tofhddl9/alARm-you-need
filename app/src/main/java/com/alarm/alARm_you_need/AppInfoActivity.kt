package com.alarm.alARm_you_need

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_app_info.*

class AppInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)

        val appVersion = "Ver ${packageManager.getPackageInfo(packageName,0).versionName}"
        app_version.text = appVersion

        btn_suggestion.setOnClickListener {
            Toast.makeText(this, "업데이트 예정입니다.", Toast.LENGTH_SHORT).show()
        }

        btn_show_license.setOnClickListener {
            Toast.makeText(this, "업데이트 예정입니다.", Toast.LENGTH_SHORT).show()
        }
    }
}