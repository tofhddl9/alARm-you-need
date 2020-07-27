package com.example.alarm

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.alarm_item.view.*

class AlarmListAdapter(val alarmList: MutableList<AlarmData>): RecyclerView.Adapter<AlarmDataViewHolder> () {

    private val DAY : List<String> = listOf("일 ", "월 ", "화 ", "수 ", "목 ", "금 ", "토 ")

    lateinit var itemClickListener: (itemId: String) -> Unit
    lateinit var checkBoxClickListner : (itemId: String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmDataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        val newViewHolder = AlarmDataViewHolder(view)

        view.setOnClickListener {
            itemClickListener?.run {
                val alarmId = it.tag as String
                this(alarmId)
            }
        }
        return newViewHolder
    }

    override fun getItemCount(): Int {
        return alarmList.count()
    }

    override fun onBindViewHolder(holder: AlarmDataViewHolder, position: Int) {
        holder.containerView.tag = alarmList[position].alarmId

        val checkBox = holder.itemView.alarm_on_off
        checkBox.isChecked = alarmList[position].onoff
        setCheckBoxBackgroundColor(holder, checkBox)
        registerCheckBoxListener(holder, checkBox, position)

        var days = ""
        if (alarmList[position].sun) days += DAY[0]
        if (alarmList[position].mon) days += DAY[1]
        if (alarmList[position].tue) days += DAY[2]
        if (alarmList[position].wed) days += DAY[3]
        if (alarmList[position].thur) days += DAY[4]
        if (alarmList[position].fri) days += DAY[5]
        if (alarmList[position].sat) days += DAY[6]
        holder.containerView.alarm_day.text = days

        holder.containerView.alarm_title.text = alarmList[position].title

        holder.containerView.alarm_apm.text = alarmList[position].apm

        var time= if(alarmList[position].hour < 10) "0" + alarmList[position].hour else ""+alarmList[position].hour
        time += ":"
        time += if(alarmList[position].minute < 10) "0" + alarmList[position].minute else ""+alarmList[position].minute
        holder.containerView.alarm_time.text = time
    }

    private fun setCheckBoxBackgroundColor(holder: AlarmDataViewHolder, checkBox: CheckBox) {
        if (checkBox.isChecked)
            holder.itemView.item_view.setBackgroundColor(Color.parseColor("#ffccdeff"))
        else
            holder.itemView.item_view.setBackgroundColor(Color.parseColor("#ffffffff"))
    }

    private fun registerCheckBoxListener(holder: AlarmDataViewHolder, checkBox: CheckBox, position: Int) {
        checkBox.setOnCheckedChangeListener { _, b ->
            setCheckBoxBackgroundColor(holder, checkBox)

        }
    }



    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}