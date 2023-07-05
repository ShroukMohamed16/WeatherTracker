package com.example.weathertracker.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathertracker.Constants
import com.example.weathertracker.R
import com.example.weathertracker.databinding.DayItemBinding
import com.example.weathertracker.model.DailyWeather

class DayAdapter:ListAdapter<DailyWeather, DayAdapter.DayViewHolder>(DayDiffUtil()){
    lateinit var binding:DayItemBinding

    inner class DayViewHolder(binding: DayItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context)
            , R.layout.day_item,parent, false)

        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        var currentObject  = getItem(position)
        binding.dailyWeather = currentObject
        binding.dayTemperature.text = currentObject.temp.max.toString()+"/"+currentObject.temp.min.toString()
        binding.stateIcon.setImageResource(Constants.setIcon(currentObject.weather.get(0).icon))

    }
}