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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper
import kotlinx.android.synthetic.main.fragment_alarm_setting.*
import java.util.*

class AlarmSettingFragment(): Fragment(), ImageDialogListener{
    private var viewModel: AlarmSettingViewModel? = null
    private var alarmId: String? = null

    private var uriArImage: String? = null
    private lateinit var uriRingtone: String
    private lateinit var alarmType: String

    private var imageDialog: ImageDialog? = null


    private val REQ_RINGTONE_SELECT = 1
    private val REQ_CAMERA_OPEN = 2
    private val REQ_GALLERY_OPEN = 3

    private val VALID_SETTING = 1
    private val INVALID_NO_DAY_OF_WEEK = 2
    private val INVALID_NO_IMAGE = 3

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_alarm_setting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = requireActivity().application!!.let {
            ViewModelProvider(viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(it))
                .get(AlarmSettingViewModel::class.java)
        }

        viewModel!!.let {
            it.title.observe(requireActivity(), Observer { alarm_title.setText(it) })
            it.hour.observe(requireActivity(), Observer { timePicker.hour = it })
            it.minute.observe(requireActivity(), Observer { timePicker.minute = it })

            it.sun.observe(requireActivity(), Observer { sun_box.isChecked = it })
            it.mon.observe(requireActivity(), Observer { mon_box.isChecked = it })
            it.tue.observe(requireActivity(), Observer { tue_box.isChecked = it })
            it.wed.observe(requireActivity(), Observer { wed_box.isChecked = it })
            it.thur.observe(requireActivity(), Observer { thur_box.isChecked = it })
            it.fri.observe(requireActivity(), Observer { fri_box.isChecked = it })
            it.sat.observe(requireActivity(), Observer { sat_box.isChecked = it })

            it.uriRingtone.observe(requireActivity(), Observer { uriRingtone = it })
            it.uriImage.observe(requireActivity(), Observer { uriArImage = it })
            it.volume.observe(requireActivity(), Observer { volume_bar.progress = it })
            it.alarmType.observe(requireActivity(), Observer { alarmType = it })
        }

        val defaultUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val defaultRingtone = RingtoneManager.getRingtone(requireContext(), defaultUri)
        ringtone_btn.text = defaultRingtone.getTitle(requireContext())
        alarm_type_btn.text = AlarmData.TYPE_DEFAULT

        alarmId = arguments?.getString("ALARM_ID")
        if (alarmId != null)
            loadAlarm(alarmId!!)

        setOnClickListeners()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
            Toast.makeText(requireActivity(), "AR 기능을 사용하기 위해서 카메라 권한이 필요합니다", Toast.LENGTH_LONG).show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(requireActivity())) {
                Log.d("DEBUGGING LOG", "HERE I AM")
                CameraPermissionHelper.launchPermissionSettings(requireActivity())
            }
        }
        if (CameraPermissionHelper.hasCameraPermission(requireActivity())) {
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
                    val uri = data?.data
                    Log.d("DEBUGGING LOG", "selected image URI : ${uri}")
                    try {
                        setArImage(uri!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            //nop
        }
    }

    private fun setOnClickListeners() {
        ringtone_btn.setOnClickListener {
            selectAlarmRingtone()
        }


        alarm_type_btn.setOnClickListener {
            selectAlarmType()
        }

        ar_image.setOnClickListener {
            imageDialog = ImageDialog(requireContext(), this)
            imageDialog?.show()
        }
    }

    fun showConfirmAlert(alarmId: String) {
        val builder = AlertDialog.Builder(
            ContextThemeWrapper(requireContext(), R.style.Theme_AppCompat_Light_Dialog)
        )
        builder.setTitle("알람 삭제")
        builder.setMessage("알람을 삭제하시겠습니까?")

        builder.setPositiveButton("확인") { _, _ ->
            Log.d("DEBUGGING LOG", "Id:[${alarmId}] alarm is deleted")
            viewModel?.deleteAlarm(alarmId)
            requireActivity().finish()
        }
        builder.setNegativeButton("취소") { _, _ ->

        }
        builder.show()
    }

    private fun setArImage(uriImage: Uri) {
        uriArImage = uriImage.toString()
        val input = requireActivity().contentResolver.openInputStream(uriImage)
        val image = BitmapFactory.decodeStream(input)
        ar_image.setImageBitmap(image)
        Glide.with(this).load(image).into(ar_image)
    }

    private fun writeRingtoneTitle(uri: Uri) {
        val ringtone = RingtoneManager.getRingtone(requireContext(), uri)
        ringtone_btn.text = ringtone.getTitle(requireContext())
    }

    private fun writeAlarmTypeTitle(alarmType: String?) {
        alarm_type_btn.text = alarmType
    }

    private fun selectAlarmRingtone() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, null as Uri?)
        this.startActivityForResult(intent, REQ_RINGTONE_SELECT)
    }

    private fun selectAlarmType() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알람 유형을 고르세요")
        val alarmTypes = arrayOf("DEFAULT", "AR")
        val checkedItem = alarmTypes.indexOf(alarmType)
        var type = "DEFAULT"
        builder.setSingleChoiceItems(alarmTypes, checkedItem) { _, which ->
            type = alarmTypes[which]
            if (type == "AR") {
                if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
                    type = "DEFAULT"
                    ar_image.visibility = View.GONE
                    CameraPermissionHelper.requestCameraPermission(requireActivity())
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

    private fun isAlarmValid(): Int {
        val isDayOfWeekValid = sun_box.isChecked or mon_box.isChecked or tue_box.isChecked or
                wed_box.isChecked or thur_box.isChecked or fri_box.isChecked or sat_box.isChecked

        var isARImageSelected = true
        if (alarmType == AlarmData.TYPE_AR && uriArImage == null)
            isARImageSelected = false

        if (!isDayOfWeekValid) {
            return INVALID_NO_DAY_OF_WEEK
        } else if (!isARImageSelected) {
            return INVALID_NO_IMAGE
        } else {
            notifyRemainingTime()
            return VALID_SETTING
        }
    }

    fun saveAlarm() {
        when (isAlarmValid()) {
            VALID_SETTING -> {
                viewModel?.addOrUpdateAlarm(requireContext(), alarm_title.text.toString(),
                    timePicker.hour, timePicker.minute,
                    if (timePicker.hour in 12..23) "오후" else "오전",
                    sun_box.isChecked, mon_box.isChecked, tue_box.isChecked, wed_box.isChecked,
                    thur_box.isChecked, fri_box.isChecked, sat_box.isChecked,
                    true, uriRingtone, uriArImage, volume_bar.progress, alarmType
                )
                requireActivity().finish()
            }
            INVALID_NO_DAY_OF_WEEK -> {
                Toast.makeText(requireContext(), "요일을 선택해 주세요", Toast.LENGTH_SHORT).show()
            }
            else -> { // INVALID_NO_IMAGE
                Toast.makeText(requireContext(), "AR로 인식할 이미지를 선택해 주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun notifyRemainingTime() {
        val targetDays = BooleanArray(7)
        if (sun_box.isChecked) targetDays[0] = true
        if (mon_box.isChecked) targetDays[1] = true
        if (tue_box.isChecked) targetDays[2] = true
        if (wed_box.isChecked) targetDays[3] = true
        if (thur_box.isChecked) targetDays[4] = true
        if (fri_box.isChecked) targetDays[5] = true
        if (sat_box.isChecked) targetDays[6] = true

        var targetTimeInMillis = AlarmTool.getTimeInMillis(false, timePicker.hour, timePicker.minute)

        val currentTime = GregorianCalendar.getInstance() as GregorianCalendar
        val currentTimeInMillis = currentTime.timeInMillis
        var dayOfToday = currentTime.get(GregorianCalendar.DAY_OF_WEEK) - 1

        var dDay = 0
        if (targetTimeInMillis < currentTimeInMillis) {
            dayOfToday++
            dDay++
        }
        dayOfToday %= 7

        while (!targetDays[dayOfToday]) {
            dayOfToday++
            dayOfToday %= 7
            dDay++
        }
        targetTimeInMillis += (dDay * 1000 * 60 * 60 * 24)

        val remainTimeInMills = targetTimeInMillis - currentTimeInMillis
        val remainMinute = (remainTimeInMills/(1000*60)) % 60
        val remainHour = (remainTimeInMills/(1000*60*60)) % 24
        val remainDay = (remainTimeInMills/(1000*60*60)) / 24

        val hourPadding = if(remainHour < 10) "0" else ""
        val minutePadding = if(remainMinute < 10) "0" else ""

        val remainTime = if (remainDay.toInt() == 0 && remainHour.toInt() == 0 && remainMinute.toInt() == 0) {
            "1분 이내에 알람이 울립니다"
        } else {
            "${remainDay}일 ${hourPadding}${remainHour}시간 ${minutePadding}${remainMinute}분 뒤에 알람이 울립니다"
        }
        Toast.makeText(requireContext(), remainTime, Toast.LENGTH_LONG).show()
    }

    override fun onCameraBtnClicked() {
        Toast.makeText(requireContext(), "아직 미지원 기능입니다", Toast.LENGTH_SHORT).show()
    }

    override fun onGalleryBtnClicked() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        this.startActivityForResult(intent, REQ_GALLERY_OPEN)
    }

    override fun onPreviewBtnClicked() {
        if (uriArImage == null) {
            Toast.makeText(requireContext(), "사진을 먼저 선택해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(requireContext(), "Tip : 미로가 생성되지 않는다면 이미지를 바꿔보세요.",Toast.LENGTH_LONG).show()
        val intent = Intent(requireContext(), ArPreviewActivity::class.java)
        intent.putExtra("IMAGE_URI", uriArImage)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
    }

}