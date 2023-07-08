package com.example.weathertracker.alarm.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathertracker.R
import com.example.weathertracker.databinding.AlertItemBinding
import com.example.weathertracker.model.Alarm
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "AlarmAdapter"
class AlarmAdapter(private var myListener:(Alarm)->Unit):ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder>(AlarmDiffUtil()) {

    lateinit var binding: AlertItemBinding
   inner class AlarmViewHolder(binding: AlertItemBinding):RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.alert_item,parent,false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        var currentObject = getItem(position)

        binding.startDateAlertItem.text = formatDate(currentObject.startDate , "EEE,d MMM")
        binding.endDateAlertItem.text = formatDate(currentObject.endDate , "EEE,d MMM")
        binding.startTimeAlertItem.text = formatTime(currentObject.startTime,"hh:mm a")
        binding.endTimeAlertItem.text = formatTime(currentObject.endTime,"hh:mm a")

        binding.deleteIcon.setOnClickListener {
            myListener(currentObject)
        }
    }
    fun formatDate(milliseconds: Long, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val date = Date(milliseconds)
        return sdf.format(date)
    }
    fun formatTime(milliseconds: Long, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = Date(milliseconds)
        return sdf.format(date)
    }

}