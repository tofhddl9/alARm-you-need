package com.alarm.alARm_you_need

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.close_app_dialog.*

class CloseAppDialog: DialogFragment() {

    private lateinit var size: Point

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.close_app_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        MobileAds.initialize(requireContext()) { }

        val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
            .forUnifiedNativeAd {
                close_dialog_ad?.setNativeAd(it)
            }
            .build()
        adLoader.loadAd(AdRequest.Builder().build())

        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        size = Point()
        display.getSize(size)

        close_app_btn.setOnClickListener {
            (activity as MainActivity).finish()
        }

        close_app_cancel_btn.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

}