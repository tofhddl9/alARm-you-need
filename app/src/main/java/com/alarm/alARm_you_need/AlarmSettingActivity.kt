package com.alarm.alARm_you_need
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_alarm_setting.*

class AlarmSettingActivity : AppCompatActivity() {

    private var viewModel: AlarmSettingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_setting)

        val alarmSettingFragment = AlarmSettingFragment()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_alarm_setting, alarmSettingFragment)
            .commit()

        viewModel = application!!.let {
            ViewModelProvider(
                viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(it)
            )
                .get(AlarmSettingViewModel::class.java)
        }

        val alarmId = intent.getStringExtra("ALARM_ID")
        if (alarmId != null) {
            val args = Bundle()
            args.putString("ALARM_ID", alarmId)
            alarmSettingFragment.arguments = args
        }

        setOnBottomNavigationListener(alarmId)
    }

    private fun setOnBottomNavigationListener(alarmId: String?) {
        val bottomNavigationListener =
            BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
                val frag = supportFragmentManager.findFragmentById(R.id.content_alarm_setting) as AlarmSettingFragment
                when (menuItem.itemId) {
                    R.id.cancel_button -> {
                        if (alarmId != null)
                            frag.showConfirmAlert(alarmId)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.save_button -> {
                        frag.saveAlarm()
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        bottom_navigation_view.setOnNavigationItemSelectedListener(bottomNavigationListener)
    }

}


