package com.example.weathertracker.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.MyResponse
import com.example.weathertracker.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    //For Favorite Places
    fun getFavPlaces(): Flow<List<FavoriteItem>>
    suspend fun insertToFavPlaces(favoriteItem: FavoriteItem)
    suspend fun deleteFromFavPlaces(favoriteItem: FavoriteItem)

    //For Alarms
    fun getAlarms():Flow<List<Alarm>>
    suspend fun insertToAlarms(alarm: Alarm)
    suspend fun deleteFromAlarms(start:Long,end:Long)

    //for Weather
   suspend fun insertToWeather(weatherEntity: WeatherEntity)
    fun getFromWeather(): Flow<WeatherEntity>?

}