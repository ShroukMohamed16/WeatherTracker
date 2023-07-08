package com.example.weathertracker.model

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "alarm")
data class Alarm(
    var startTime:Long,
    var endTime:Long,
    var startDate:Long,
    var endDate:Long
)
{
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

}
