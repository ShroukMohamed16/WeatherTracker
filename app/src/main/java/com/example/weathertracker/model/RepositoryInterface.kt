package com.example.weathertracker.model

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface RepositoryInterface {
    suspend fun getWeather(lat:Double , lon:Double, units:String,lang:String,apiKey:String): Flow<MyResponse>?
    fun getFavPlacesFromRoom(): Flow<List<FavoriteItem>>
    suspend fun insertToFavPlacesFromRoom(favoriteItem: FavoriteItem)
    suspend fun deleteFromFavPlacesFromRoom(favoriteItem: FavoriteItem)
}