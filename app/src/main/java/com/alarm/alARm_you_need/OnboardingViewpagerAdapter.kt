package com.alarm.alARm_you_need

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OnboardingViewpagerAdapter(private val screenList: ArrayList<ScreenItem>) : RecyclerView.Adapter<ScreenItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenItemViewHolder =
        ScreenItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_screen,parent,false))

    override fun getItemCount(): Int = screenList.size

    override fun onBindViewHolder(holder: ScreenItemViewHolder, position: Int) {
        holder.title.text = screenList[position].title
        holder.description.text = screenList[position].description
        holder.image.setImageResource(screenList[position].screenImage)
    }

}