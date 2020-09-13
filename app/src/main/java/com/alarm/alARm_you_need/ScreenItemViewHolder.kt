package com.alarm.alARm_you_need

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_screen.view.*

class ScreenItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val title = view.onboarding_title
    val description = view.onboarding_description
    val image = view.onboarding_img
}