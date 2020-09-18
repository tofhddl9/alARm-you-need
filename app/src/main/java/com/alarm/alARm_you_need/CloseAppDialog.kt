package com.alarm.alARm_you_need

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.close_app_dialog.*

class CloseAppDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.close_app_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        close_app_btn.setOnClickListener {
            (activity as MainActivity).finish()
        }

        close_app_cancel_btn.setOnClickListener {
            dismiss()
        }
    }
}