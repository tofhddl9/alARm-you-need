package com.alarm.alARm_you_need

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

class ImageDialog constructor(context: Context) : Dialog(context) {

    private val activity = context as Activity
    private val REQ_CAMERA_OPEN = 2
    private val REQ_GALLERY_OPEN = 3
    private val REQ_PREVIEW_OPEN = 4

    init {
        val alertDialog = AlertDialog.Builder(context).create()
        val view = layoutInflater.inflate(R.layout.image_select_dialog, null)
        alertDialog.setView(view)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.popup_background)
        alertDialog.window?.setLayout(700, WindowManager.LayoutParams.WRAP_CONTENT)

        val cameraButton = view.findViewById<Button>(R.id.camera_req)
        val galleryButton = view.findViewById<Button>(R.id.galery_req)
        val previewButton = view.findViewById<Button>(R.id.preview_img)

        cameraButton.setOnClickListener {
            Toast.makeText(context, "아직 미지원 기능입니다", Toast.LENGTH_SHORT).show()
            /* 단순히 사진 찍기x. 맞춰서 자르는 기능 필요 */
        }

        galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            activity.startActivityForResult(intent, REQ_GALLERY_OPEN)
        }

        previewButton.setOnClickListener {
            Toast.makeText(context, "아직 미지원 기능입니다", Toast.LENGTH_SHORT).show()
        }

    }

}