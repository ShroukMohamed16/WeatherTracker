package com.example.weathertracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_places")
data class FavoriteItem(
    @PrimaryKey
    var id:Double,
    var cityName:String,
    var lat:Double,
    var lon:Double
)
