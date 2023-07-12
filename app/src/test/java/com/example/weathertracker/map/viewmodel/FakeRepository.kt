package com.example.weathertracker.map.viewmodel

import com.example.weathertracker.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository(var favList: MutableList<FavoriteItem> = mutableListOf()
                     ,var alarmList:MutableList<Alarm> = mutableListOf() , var weatherList: MutableList<WeatherEntity>,
                     var myResponse: MyResponse):RepositoryInterface {
    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String,
        apiKey: String
    ): Flow<MyResponse>? {
        return flowOf(myResponse)
    }

    override fun getFavPlacesFromRoom(): Flow<List<FavoriteItem>> {
        return flowOf(favList)
    }

    override suspend fun insertToFavPlacesFromRoom(favoriteItem: FavoriteItem) {
        favList.add(favoriteItem)
    }

    override suspend fun deleteFromFavPlacesFromRoom(favoriteItem: FavoriteItem) {
        favList.remove(favoriteItem)
    }

    override fun getAlarmsFromRoom(): Flow<List<Alarm>> {
        return flowOf(alarmList)
    }

    override suspend fun insertAlarmToRoom(alarm: Alarm) {
        alarmList.add(alarm)
    }

    override suspend fun deleteAlarmFromRoom(start:Long ,end:Long) {
        for(alarm in alarmList){
            if (start == alarm.startTime && end == alarm.endTime)
                alarmList.remove(alarm)
        }
    }

    override suspend fun insertWeatherToRoom(weatherEntity: WeatherEntity) {
        weatherList.add(weatherEntity)
    }

    override fun getWeatherFromRoom(): Flow<WeatherEntity>? {
        return flowOf(weatherList.last())
    }
}