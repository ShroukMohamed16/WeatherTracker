package com.example.weathertracker.model

import androidx.room.PrimaryKey

data class Alarm(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    var startTime:Long,
    var endTime:Long,
    var startDate:Long,
    var endDate:Long
)
