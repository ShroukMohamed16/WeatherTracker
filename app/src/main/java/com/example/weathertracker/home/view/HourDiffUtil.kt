package com.example.weathertracker.home.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weathertracker.model.HourlyWeather

class HourDiffUtil: DiffUtil.ItemCallback<HourlyWeather>() {
    override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
         return oldItem == newItem
    }

}