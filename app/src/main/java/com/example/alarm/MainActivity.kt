package com.example.alarm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper
import kotlinx.android.synthetic.main.activity_list.*

class MainActivity : AppCompatActivity() {

    private var viewModel: ListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        Thread.sleep(1500)
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contentLayout, AlarmListFragment())
        fragmentTransaction.commit()

        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(ListViewModel::class.java)
        }

        fab.setOnClickListener {
            // ARCore requires camera permission to operate.
            if (!CameraPermissionHelper.hasCameraPermission(this)) {
                CameraPermissionHelper.requestCameraPermission(this)
            }
            /*For AR Debugging*/
            //val intent = Intent(this, AlarmSettingActivity::class.java)
            val intent = Intent(this, AugmentedImageActivity::class.java)
            startActivity(intent)
        }
        /* TODO :
            앱을 껐다 켰을 때 알람 등록 확인
            백그라운드에서도 알람이 동작하는지 확인*/
    }

}