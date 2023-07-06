package com.example.weathertracker.home.view

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathertracker.Constants
import com.example.weathertracker.R
import com.example.weathertracker.databinding.HourItemBinding
import com.example.weathertracker.model.HourlyWeather

class HourAdapter:ListAdapter<HourlyWeather, HourAdapter.HourlyViewHolder>(HourDiffUtil()) {

    lateinit var binding:HourItemBinding
    inner class HourlyViewHolder(binding: HourItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context)
            ,R.layout.hour_item,parent,false)
        return HourlyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        var currentObject = getItem(position)
        binding.hourWeather = currentObject
        //Remove static TimeZone
        binding.hour.text=Constants.getTime(currentObject.dt,"America/Chicago")
        binding.hourStateIcon.setImageResource(Constants.setIcon(currentObject.weather.get(0).icon))
    }

}


