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
import com.example.weathertracker.databinding.DayItemBinding
import com.example.weathertracker.model.DailyWeather
import com.google.type.DateTime
import org.intellij.lang.annotations.Language
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class DayAdapter(var language: String):ListAdapter<DailyWeather, DayAdapter.DayViewHolder>(DayDiffUtil()){
    lateinit var binding:DayItemBinding

    inner class DayViewHolder(var binding: DayItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context)
            , R.layout.day_item,parent, false)

        return DayViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val currentObject  = getItem(position)
        binding.dayName.text = Constants.getDay(currentObject.dt.toInt(), language)
        binding.dayTemperature.text = currentObject.temp.max.toString()+"/"+currentObject.temp.min.toString()+"°"
        binding.dayState.text = currentObject.weather.get(0).description
        binding.stateIcon.setImageResource(Constants.setIcon(currentObject.weather.get(0).icon))


    }


}