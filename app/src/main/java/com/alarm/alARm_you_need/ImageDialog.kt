package com.alarm.alARm_you_need

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.image_select_dialog.*


class ImageDialog constructor(context: Context, imageDialogListener: ImageDialogListener)
                              : Dialog(context), View.OnClickListener
{

    private var imageDialogListener: ImageDialogListener? = null

    init {
        this.imageDialogListener = imageDialogListener
        preview_req?.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_select_dialog)
        window?.setBackgroundDrawableResource(R.drawable.popup_background)
        window?.setLayout(700, WindowManager.LayoutParams.WRAP_CONTENT)

        camera_req.setOnClickListener(this)
        gallery_req.setOnClickListener(this)
        preview_req.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            camera_req -> {
                Log.d("DEBUGGING LOG", "onClick: camera_req")
                this.imageDialogListener?.onCameraBtnClicked()
                dismiss()
            }
            gallery_req -> {
                Log.d("DEBUGGING LOG", "onClick: gallery_req")
                this.imageDialogListener?.onGalleryBtnClicked()
                dismiss()
            }
            preview_req -> {
                Log.d("DEBUGGING LOG", "onClick: preview_req")
                this.imageDialogListener?.onPreviewBtnClicked()
            }
        }
    }

}

interface ImageDialogListener {
    fun onCameraBtnClicked()
    fun onGalleryBtnClicked()
    fun onPreviewBtnClicked()
}