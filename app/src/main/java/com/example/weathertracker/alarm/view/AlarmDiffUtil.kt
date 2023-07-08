package com.example.weathertracker.alarm.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weathertracker.model.Alarm

class AlarmDiffUtil: DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }
}