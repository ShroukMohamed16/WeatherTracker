package com.example.weathertracker.model

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface RepositoryInterface {
    suspend fun getWeather(lat:Double , lon:Double, units:String,lang:String,apiKey:String): Flow<MyResponse>?

    //for favorites
    fun getFavPlacesFromRoom(): Flow<List<FavoriteItem>>
    suspend fun insertToFavPlacesFromRoom(favoriteItem: FavoriteItem)
    suspend fun deleteFromFavPlacesFromRoom(favoriteItem: FavoriteItem)

    //for alarms
    fun getAlarmsFromRoom(): Flow<List<Alarm>>
    suspend fun insertAlarmToRoom(alarm: Alarm)
    suspend fun deleteAlarmFromRoom(alarm: Alarm)

    //for Weather
    suspend fun insertWeatherToRoom(weatherEntity: WeatherEntity)
    fun getWeatherFromRoom(): Flow<WeatherEntity>?

}