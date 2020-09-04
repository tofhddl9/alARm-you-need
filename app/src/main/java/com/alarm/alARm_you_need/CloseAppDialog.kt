package com.alarm.alARm_you_need

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView

class CloseAppDialog constructor(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val closeBtn = findViewById<TextView>(R.id.close_app_btn)
        val cancelBtn = findViewById<TextView>(R.id.close_app_cancel_btn)

        closeBtn.setOnClickListener {
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
    }

}