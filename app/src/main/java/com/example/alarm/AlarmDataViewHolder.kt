package com.example.alarm

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.alarm_item.view.*

//class AlarmDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
class AlarmDataViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    val TAG : String = "로그"

    private val title = itemView.alarm_title
    private val time = itemView.alarm_time
    private val apm = itemView.alarm_apm
    private val day = itemView.alarm_day
    private val onoff = itemView.alarm_on_off

    fun bind(alarmModel : AlarmModel) {
        Log.d(TAG, "AlarmListViewHolder - bind() called")

        title.text = alarmModel.title
        time.text = alarmModel.time

    }

}