package com.example.alarm

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.alarm_item.view.*
import java.io.Serializable

class AlarmDataAdapter(val context : Context, val alarmList : ArrayList<AlarmModel>): RecyclerView.Adapter<AlarmDataViewHolder>() {

    private val DAY : List<String> = listOf("일 ", "월 ", "화 ", "수 ", "목 ", "금 ", "토 ")
    private val mainActivity = context as MainActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmDataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val id = v?.tag as Long
                updateAlarm(2, id)
                if (selectionList.contains(id)) selectionList.remove(id)
                else selectionList.add(id)
                notifyDataSetChanged()
                onItemSelectionChangedListener?.let { it(selectionList) }
            }
        })

        return AlarmDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alarmList.count()
    }

    override fun onBindViewHolder(holder: AlarmDataViewHolder, position: Int) {
        holder.containerView.tag = getItemId(position)
        selectionList.contains(getItemId(position))

        val checkView = holder.itemView.alarm_on_off
        checkView.setOnCheckedChangeListener { compoundButton, b ->
            if (checkView.isChecked) {
                holder.itemView.item_view.setBackgroundColor(Color.parseColor("#ffccdeff"))
            } else {
                holder.itemView.item_view.setBackgroundColor(Color.parseColor("#ffffffff"))
            }
            alarmList[position].onoff = checkView.isChecked
        }

        var days = ""
        for (index in 0..6)
            if (alarmList[position].day[index])
                days += DAY[index]
        holder.containerView.alarm_day.text = days
        holder.containerView.alarm_title.text = alarmList[position].title
        holder.containerView.alarm_apm.text = alarmList[position].apm

        var time= if(alarmList[position].hour < 10) "0" + alarmList[position].hour else ""+alarmList[position].hour
        time += ":"
        time += if(alarmList[position].minute < 10) "0" + alarmList[position].minute else ""+alarmList[position].minute
        holder.containerView.alarm_time.text = time
    }

    fun updateAlarm(requestCode: Int, position: Long) {
        /*Todo [1]: Add Ringtone status*/
        /*Todo [late]: Add vibration status*/
        val intent = Intent(context, AlarmSettingActivity::class.java)
        intent.putExtra("alarmCode", requestCode)
        intent.putExtra("alarmId", alarmList.size)
        if (requestCode == 2) {
            Log.d("Updated view ID",""+position)
            alarmList[position.toInt()].alarmId = position.toInt()
            intent.putExtra("updatedData", alarmList[position.toInt()] as Serializable)
        }
        mainActivity.startActivityForResult(intent, requestCode)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    val selectionList = mutableListOf<Long>()
    var onItemSelectionChangedListener: ((MutableList<Long>) -> Unit)? = null

}