package com.example.alarm

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.alarm_item.view.*

class AlarmDataAdapter(val list : List<AlarmModel>): RecyclerView.Adapter<AlarmDataViewHolder>() {

    private val DAY : List<String> = listOf("일 ", "월 ", "화 ", "수 ", "목 ", "금 ", "토 ")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmDataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        return AlarmDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("AlarmAdapter", "list count : "+list.count())
        return list.count()
    }

    override fun onBindViewHolder(holder: AlarmDataViewHolder, position: Int) {
        holder.containerView.alarm_title.text = list[position].title
        holder.containerView.alarm_apm.text = list[position].apm
        holder.containerView.alarm_time.text = list[position].time
        var days = ""
        for (index in 0..6) {
            if (list[position].day[index]) {
                days += DAY[index]
            }
        }
        holder.containerView.alarm_day.text = days
        holder.containerView.alarm_on_off.text = list[position].onoff
    }

}