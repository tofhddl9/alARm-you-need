package com.alarm.alARm_you_need
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper
import kotlinx.android.synthetic.main.activity_alarm_setting.*

class AlarmSettingActivity : AppCompatActivity() {

    private var viewModel: AlarmSettingViewModel? = null
    private lateinit var uriRingtone: String
    private var uriArImage: String? = null
    private lateinit var alarmType: String

    /*이 요청 코드를 ImageDialog랑 같이 들고 있는게 올바른가*/
    private val REQ_RINGTONE_SELECT = 1
    private val REQ_CAMERA_OPEN = 2
    private val REQ_GALLERY_OPEN = 3

    private val VALID_SETTING = 1
    private val INVALID_NO_DAY_OF_WEEK = 2
    private val INVALID_NO_IMAGE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_setting)

        val defaultUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val defaultRingtone = RingtoneManager.getRingtone(this, defaultUri)
        ringtone_btn.text = defaultRingtone.getTitle(this)
        alarm_type_btn.text = AlarmData.TYPE_DEFAULT

        ObserveViewModel()
        val alarmId = intent.getStringExtra("ALARM_ID")
        if (alarmId != null) {
            loadAlarm(alarmId)
        }

        setOnClickListeners(alarmId)
    }

    private fun ObserveViewModel() {
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
            it.uriImage.observe(this, Observer { uriArImage = it })
            it.volume.observe(this, Observer { volume_bar.progress = it })
            it.alarmType.observe(this, Observer { alarmType = it })
        }
    }

    private fun loadAlarm(id : String) {
        viewModel!!.loadAlarm(id)
        val uri = Uri.parse(viewModel!!.uriRingtone.value)
        writeRingtoneTitle(uri!!)

        writeAlarmTypeTitle(viewModel!!.alarmType.value)

        if (viewModel!!.uriImage.value != null) {
            if (viewModel!!.alarmType.value == AlarmData.TYPE_AR)
                ar_image.visibility = View.VISIBLE
            val uri2 = Uri.parse(viewModel!!.uriImage.value)
            if (uri2 != null) {
                Log.d("DEBUGGING LOG","Loaded image uri : "+uri2)
                setArImage(uri2)
            }
            else {
                Log.d("DEBUGGING LOG","No ARimage was selected : "+uri2)
            }
        }
    }

    private fun isAlarmValid(): Int {
        val isDayOfWeekValid = sun_box.isChecked or mon_box.isChecked or tue_box.isChecked or
                wed_box.isChecked or thur_box.isChecked or fri_box.isChecked or sat_box.isChecked

        var isARImageSelected = true
        if (alarmType == AlarmData.TYPE_AR && uriArImage == null)
            isARImageSelected = false

        return if (!isDayOfWeekValid) {
            INVALID_NO_DAY_OF_WEEK
        } else if (!isARImageSelected) {
            INVALID_NO_IMAGE
        } else {
            VALID_SETTING
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "AR 기능을 사용하기 위해서 카메라 권한이 필요합니다", Toast.LENGTH_LONG).show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                Log.d("DEBUGGING LOG", "HERE I AM")
                CameraPermissionHelper.launchPermissionSettings(this)
            }
        }
        if(CameraPermissionHelper.hasCameraPermission(this)) {
            alarmType = "AR"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_RINGTONE_SELECT -> {
                    val uri =
                        data!!.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    Log.d("DEBUGGING LOG", "selected ringtone URI : $uri")
                    if (uri != null) {
                        uriRingtone = uri.toString()
                        writeRingtoneTitle(uri)
                    }
                }

                REQ_GALLERY_OPEN -> {
                    val uri = data?.data!!
                    Log.d("DEBUGGING LOG", "selected image URI : $uri")
                    try {
                        setArImage(uri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            //nop
        }
    }

    private fun writeRingtoneTitle(uri: Uri) {
        val ringtone = RingtoneManager.getRingtone(this, uri)
        ringtone_btn.text = ringtone.getTitle(this)
    }

    private fun writeAlarmTypeTitle(alarmType: String?) {
        alarm_type_btn.text = alarmType
    }

    private fun setArImage(uriImage: Uri) {
        uriArImage = uriImage.toString()
        val input = contentResolver.openInputStream(uriImage)
        val image = BitmapFactory.decodeStream(input)
        ar_image.setImageBitmap(image)
        Glide.with(this).load(image).into(ar_image)
    }

    private fun setOnClickListeners(alarmId: String?) {
        back_btn.setOnClickListener {
            onBackPressed()
        }

        delete_btn.setOnClickListener {
            if(alarmId != null)
                showConfirmAlert(alarmId)
        }

        save_btn.setOnClickListener {
            saveAlarm()
        }

        ringtone_btn.setOnClickListener {
            selectAlarmRingtone()
        }


        alarm_type_btn.setOnClickListener {
            selectAlarmType()
        }

        ar_image.setOnClickListener {
            val dialog = ImageDialog(this)
        }

    }

    private fun showConfirmAlert(alarmId: String) {
        val builder = AlertDialog.Builder(
            ContextThemeWrapper(this@AlarmSettingActivity,
                R.style.Theme_AppCompat_Light_Dialog
            )
        )
        builder.setTitle("알람 삭제")
        builder.setMessage("알람을 삭제하시겠습니까?")

        builder.setPositiveButton("확인") { _, _ ->
            Log.d("DEBUGGING LOG", "Id:[${alarmId}] alarm is deleted")
            viewModel?.deleteAlarm(alarmId)
            finish()
        }
        builder.setNegativeButton("취소") { _, _ ->

        }
        builder.show()
    }

    private fun selectAlarmRingtone() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, null as Uri?)
        this.startActivityForResult(intent, REQ_RINGTONE_SELECT)
    }

    private fun selectAlarmType() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알람 유형을 고르세요")
        val alarmTypes = arrayOf("DEFAULT", "AR")
        val checkedItem = alarmTypes.indexOf(alarmType)
        var type = "DEFAULT"
        builder.setSingleChoiceItems(alarmTypes, checkedItem) { _, which ->
            type = alarmTypes[which]
            if (type == "AR") {
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    type = "DEFAULT"
                    ar_image.visibility = View.GONE
                    CameraPermissionHelper.requestCameraPermission(this)
                }
                else {
                    type = "AR"
                    ar_image.visibility = View.VISIBLE
                }
            }
            else {
                type = "DEFAULT"
                ar_image.visibility = View.GONE
            }
        }
        builder.setPositiveButton("Ok") { _, _ ->
            alarmType = type
            alarm_type_btn.text = alarmType
        }
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveAlarm() {
        when (isAlarmValid()) {
            VALID_SETTING -> {
                viewModel?.addOrUpdateAlarm(this, alarm_title.text.toString(),
                    timePicker.hour, timePicker.minute,
                    if (timePicker.hour in 12..23) "오후" else "오전",
                    sun_box.isChecked, mon_box.isChecked, tue_box.isChecked, wed_box.isChecked,
                    thur_box.isChecked, fri_box.isChecked, sat_box.isChecked,
                    true, uriRingtone, uriArImage, volume_bar.progress, alarmType
                )
                finish()
            }
            INVALID_NO_DAY_OF_WEEK -> {
                Toast.makeText(this@AlarmSettingActivity, "요일을 선택해 주세요", Toast.LENGTH_SHORT).show()
            }
            else -> { // INVALID_NO_IMAGE
                Toast.makeText(this@AlarmSettingActivity, "AR로 인식할 이미지를 선택해 주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }


}

