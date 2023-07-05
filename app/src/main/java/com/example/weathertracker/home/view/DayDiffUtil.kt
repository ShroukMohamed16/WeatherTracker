package com.example.weathertracker.home.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weathertracker.model.DailyWeather
import com.example.weathertracker.model.HourlyWeather

class DayDiffUtil: DiffUtil.ItemCallback<DailyWeather>() {
    override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem == newItem
    }


}