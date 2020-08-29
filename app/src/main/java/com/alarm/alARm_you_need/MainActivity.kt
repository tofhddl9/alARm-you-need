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
import kotlinx.android.synthetic.main.activity_list.*

class MainActivity : AppCompatActivity() {

    private var viewModel: ListViewModel? = null
    private val resultCode = 12345

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUGGING LOG", "MainActivity::onCreate() is called")
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
            val intent = Intent(this, AlarmSettingActivity::class.java)
            startActivity(intent)
        }

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
                    startActivityForResult(intent, resultCode)
                    dialog.dismiss()
                }
            }
            builder.show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == this.resultCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "권한을 수락해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}