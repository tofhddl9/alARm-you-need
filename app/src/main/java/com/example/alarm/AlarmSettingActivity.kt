package com.example.alarm
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_alarm_setting.*

class AlarmSettingActivity : AppCompatActivity() {

    private var viewModel: AlarmSettingViewModel? = null
    private lateinit var uriRingtone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_setting)

        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(AlarmSettingViewModel::class.java)
        }

        viewModel!!.let {
            it.title.observe(this, Observer { alarm_title.setText(it) })
            it.hour.observe(this, Observer { timePicker.hour = it })
            it.minute.observe(this, Observer { timePicker.minute = it })

            it.sun.observe(this, Observer { sun_box.isChecked = it })
            it.mon.observe(this, Observer { mon_box.isChecked = it })
            it.tue.observe(this, Observer { tue_box.isChecked = it })
            it.wed.observe(this, Observer { wed_box.isChecked = it })
            it.thur.observe(this, Observer { thur_box.isChecked = it })
            it.fri.observe(this, Observer { fri_box.isChecked = it })
            it.sat.observe(this, Observer { sat_box.isChecked = it })

            it.uriRingtone.observe(this, Observer { uriRingtone = it })
            it.volume.observe(this, Observer {volume_bar.progress = it})
        }

        val alarmId = intent.getStringExtra("ALARM_ID")
        if (alarmId != null) {
            viewModel!!.loadAlarm(alarmId)
            val uri = Uri.parse(viewModel!!.uriRingtone.value)
            writeRingtoneTitle(uri!!)
        }

        back_btn.setOnClickListener {
            onBackPressed()
        }

        delete_btn.setOnClickListener {
            if(alarmId != null)
                showConfirmAlert(alarmId)
        }

        save_btn.setOnClickListener {
            viewModel?.addOrUpdateAlarm(
                this,
                alarm_title.text.toString(),
                timePicker.hour,
                timePicker.minute,
                if(timePicker.hour in 12..23) "오후" else "오전",
                sun_box.isChecked, mon_box.isChecked, tue_box.isChecked, wed_box.isChecked,
                thur_box.isChecked, fri_box.isChecked, sat_box.isChecked,
                true,
                uriRingtone,
                volume_bar.progress
            )
            finish()
        }

        ringtone_btn.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, null as Uri?)
            this.startActivityForResult(intent, 5)
        }

    }

    private fun writeRingtoneTitle(uri: Uri) {
       val ringtone = RingtoneManager.getRingtone(this, uri)
        ringtone_btn.text = ringtone.getTitle(this)
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            val uri = data!!.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            uriRingtone = uri.toString()
            writeRingtoneTitle(uri!!)
        }
    }

    private fun showConfirmAlert(alarmId: String) {
        val builder = AlertDialog.Builder(
            ContextThemeWrapper(
                this@AlarmSettingActivity,
                R.style.Theme_AppCompat_Light_Dialog
            )
        )
        builder.setTitle("알람 삭제")
        builder.setMessage("알람을 삭제하시겠습니까?")

        builder.setPositiveButton("확인") { _, _ ->
            Log.d("delete button", "is clicked")
            viewModel?.deleteAlarm(alarmId)
            finish()
        }
        builder.setNegativeButton("취소") { _, _ ->

        }
        builder.show()
    }

}