package com.example.weathertracker.db

import android.content.Context
import com.example.weathertracker.model.FavoriteItem
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(context: Context):LocalSource {

    private val favDao:FavoriteWeatherDAO by lazy{
        val db:WeatherDataBase = WeatherDataBase.getInstance(context)
        db.getFavouriteWeatherDao()
    }
    override fun getFavPlaces(): Flow<List<FavoriteItem>> {
        return favDao.getAllFavorites()
    }

    override suspend fun insertToFavPlaces(favoriteItem: FavoriteItem) {
        favDao.insertFavPlace(favoriteItem)
    }

    override suspend fun deleteFromFavPlaces(favoriteItem: FavoriteItem) {
        favDao.deleteFavPlace(favoriteItem)
    }

}