package com.alarm.alARm_you_need

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var viewModel: ListViewModel? = null
    private val overlayPermissionResultCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUGGING LOG", "MainActivity::onCreate() is called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentLayout, AlarmListFragment())
            .commit()

        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(ListViewModel::class.java)
        }
        setOnBottomNaviClickedListener()
        requestPermissions()
    }

    private fun setOnBottomNaviClickedListener() {
        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.main_menu -> {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.contentLayout, AlarmListFragment())
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.schedule_menu -> {
                        Toast.makeText(this, "아직 미구현 기능입니다", Toast.LENGTH_SHORT).show()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.config_menu -> {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.contentLayout, ConfigurationFragment())
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        bottom_navigation_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    @Override
    override fun onBackPressed() {
        val closeAppDialog = CloseAppDialog()
        closeAppDialog.show(supportFragmentManager, "CloseDialog")
    }

    private fun requestPermissions() {
        requestOverlayPermission()
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            !Settings.canDrawOverlays(this)) {
            Log.d("DEBUGGING LOG", "MainActivity::onCreate() ... Need to getting canDrawOverlay")
            val builder = AlertDialog.Builder(this).apply {
                setTitle("권한 요청")
                setMessage("알람이 정상적으로 울리기 위해서 \n" +
                        "'다른 화면 위에 그리기 권한'이 필요합니다.\n" +
                        "꼭 수락해주세요 :)")
                setCancelable(false)
                setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton("수락") {dialog, _->
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivityForResult(intent, overlayPermissionResultCode)
                    dialog.dismiss()
                }
            }
            builder.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == overlayPermissionResultCode) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "권한을 수락해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}