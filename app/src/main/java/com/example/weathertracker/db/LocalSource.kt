package com.example.weathertracker.db

import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.FavoriteItem
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    //For Favorite Places
    fun getFavPlaces(): Flow<List<FavoriteItem>>
    suspend fun insertToFavPlaces(favoriteItem: FavoriteItem)
    suspend fun deleteFromFavPlaces(favoriteItem: FavoriteItem)
    //For Alarms
    fun getAlarms():Flow<List<Alarm>>
    suspend fun insertToAlarms(alarm: Alarm)
    suspend fun deleteFromAlarms(alarm: Alarm)
}