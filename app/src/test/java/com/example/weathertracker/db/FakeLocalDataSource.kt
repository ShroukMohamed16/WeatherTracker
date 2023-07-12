package com.example.weathertracker.db

import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.WeatherEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource(var favList: MutableList<FavoriteItem> = mutableListOf()
,var alarmList:MutableList<Alarm> = mutableListOf() , var weatherList: MutableList<WeatherEntity>
):LocalSource {
    override fun getFavPlaces(): Flow<List<FavoriteItem>> {
        return flowOf(favList)
    }

    override suspend fun insertToFavPlaces(favoriteItem: FavoriteItem) {
        favList.add(favoriteItem)
    }

    override suspend fun deleteFromFavPlaces(favoriteItem: FavoriteItem) {
        favList.remove(favoriteItem)
    }

    override fun getAlarms(): Flow<List<Alarm>> {
        return flowOf(alarmList)
    }

    override suspend fun insertToAlarms(alarm: Alarm) {
        alarmList.add(alarm)
    }

    override suspend fun deleteFromAlarms(alarm: Alarm) {
        alarmList.remove(alarm)
    }

    override suspend fun insertToWeather(weatherEntity: WeatherEntity) {
       weatherList.add(weatherEntity)
    }

    override fun getFromWeather(): Flow<WeatherEntity>? {
        return flowOf(weatherList.last())
    }
}