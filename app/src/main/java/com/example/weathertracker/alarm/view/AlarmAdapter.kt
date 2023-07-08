package com.example.weathertracker.alarm.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathertracker.R
import com.example.weathertracker.databinding.AlertItemBinding
import com.example.weathertracker.model.Alarm

class AlarmAdapter(private var myListener:(Alarm)->Unit):ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder>(AlarmDiffUtil()) {

    lateinit var binding: AlertItemBinding
   inner class AlarmViewHolder(binding: AlertItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.alert_item,parent,false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
       /* var currentObject = getItem(position)

        binding.deleteIcon.setOnClickListener {
            myListener(currentObject)
        }*/
    }
}