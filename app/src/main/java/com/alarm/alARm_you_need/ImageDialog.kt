package com.alarm.alARm_you_need

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button

class ImageDialog constructor(context: Context) : Dialog(context) {

    init {
        val alertDialog = AlertDialog.Builder(context).create()
        val view = layoutInflater.inflate(R.layout.image_select_dialog, null)
        alertDialog.setView(view)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.popup_background)
        alertDialog.window?.setLayout(700, WindowManager.LayoutParams.WRAP_CONTENT)

        val cameraButton = view.findViewById<Button>(R.id.camera_req)
        val galeryButton = view.findViewById<Button>(R.id.galery_req)
        val removeButton = view.findViewById<Button>(R.id.remove_img)

        cameraButton.setOnClickListener {

        }

        galeryButton.setOnClickListener {

        }

        removeButton.setOnClickListener {

        }

    }

}