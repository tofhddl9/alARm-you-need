package com.alarm.alARm_you_need

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.alarm_item.view.*

class AlarmListAdapter(val alarmList: MutableList<AlarmData>): RecyclerView.Adapter<AlarmDataViewHolder> () {

    private val DAY : List<String> = listOf("일 ", "월 ", "화 ", "수 ", "목 ", "금 ", "토 ")

    lateinit var itemClickListener: (itemId: String) -> Unit
    lateinit var checkBoxClickListener : (itemId: String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmDataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        val newViewHolder = AlarmDataViewHolder(view)

        view.setOnClickListener {
            itemClickListener.run {
                val alarmId = it.tag as String
                this(alarmId)
            }
        }
        view.alarm_active.setOnClickListener {
            checkBoxClickListener.run {
                val alarmId = view.tag as String
                this(alarmId)
            }
        }
        return newViewHolder
    }

    override fun getItemCount(): Int {
        return alarmList.count()
    }

    override fun onBindViewHolder(holder: AlarmDataViewHolder, position: Int) {
        val alarmData = alarmList[position]

        holder.containerView.tag = alarmData.alarmId
        val checkBox = holder.itemView.alarm_active
        checkBox.isChecked = alarmData.active
        setCheckBoxBackgroundColor(holder, checkBox)
        registerCheckBoxListener(holder, checkBox)

        var days = ""
        if (alarmData.sun) days += DAY[0]
        if (alarmData.mon) days += DAY[1]
        if (alarmData.tue) days += DAY[2]
        if (alarmData.wed) days += DAY[3]
        if (alarmData.thur) days += DAY[4]
        if (alarmData.fri) days += DAY[5]
        if (alarmData.sat) days += DAY[6]
        holder.containerView.alarm_day.text = days

        holder.containerView.alarm_title.text = alarmData.title

        holder.containerView.alarm_apm.text = alarmData.apm

        holder.containerView.alarm_time.text = makeTime12HourClock(alarmData.hour, alarmData.minute)
    }

    private fun makeTime12HourClock(hour: Int, min: Int) : String{
        var time = ""
        if (hour % 12 in 0..9)
            time += "0"
        time += hour % 12
        if (hour == 12)
            time = "12"
        time += ":"
        if (min < 10) {
            time += "0"
        }
        time += min
        return time
    }

    private fun setCheckBoxBackgroundColor(holder: AlarmDataViewHolder, checkBox: CheckBox) {
        if (checkBox.isChecked)
            holder.itemView.item_view.setBackgroundColor(Color.parseColor("#ffccdeff"))
        else
            holder.itemView.item_view.setBackgroundColor(Color.parseColor("#ffffffff"))
    }

    private fun registerCheckBoxListener(holder: AlarmDataViewHolder, checkBox: CheckBox) {
        checkBox.setOnCheckedChangeListener { _, _ ->
            setCheckBoxBackgroundColor(holder, checkBox)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}