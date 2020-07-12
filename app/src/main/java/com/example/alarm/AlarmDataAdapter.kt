package com.example.alarm

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.alarm_item.view.*

class AlarmDataAdapter(val alarmList : ArrayList<AlarmModel>): RecyclerView.Adapter<AlarmDataViewHolder>() {

    private val DAY : List<String> = listOf("일 ", "월 ", "화 ", "수 ", "목 ", "금 ", "토 ")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmDataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val id = v?.tag
                if (selectionList.contains(id)) selectionList.remove(id)
                else selectionList.add(id as Long)
                notifyDataSetChanged()
                onItemSelectionChangedListener?.let { it(selectionList) }

            }
        })

        val checkView = view.alarm_on_off
        checkView.setOnCheckedChangeListener { compoundButton, b ->
            if (checkView.isChecked) {
                view.item_view.setBackgroundColor(Color.parseColor("#ffccdeff"))
                //Todo : update alarmList[id].onoff
            } else {
                view.item_view.setBackgroundColor(Color.parseColor("#ffffffff"))

            }
        }

        return AlarmDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("AlarmAdapter", "list count : "+alarmList.count())
        return alarmList.count()
    }

    override fun onBindViewHolder(holder: AlarmDataViewHolder, position: Int) {
        holder.containerView.tag = getItemId(position)
        selectionList.contains(getItemId(position))

        //holder.containerView.alarm_on_off.isChecked = alarmList[position].onoff

        var days = ""
        for (index in 0..6)
            if (alarmList[position].day[index])
                days += DAY[index]
        holder.containerView.alarm_day.text = days
        holder.containerView.alarm_title.text = alarmList[position].title
        holder.containerView.alarm_apm.text = alarmList[position].apm
        holder.containerView.alarm_time.text = alarmList[position].time
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    val selectionList = mutableListOf<Long>()
    var onItemSelectionChangedListener: ((MutableList<Long>) -> Unit)? = null
}