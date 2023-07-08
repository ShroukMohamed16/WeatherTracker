package com.example.weathertracker.db

import android.content.Context
import com.example.weathertracker.model.FavoriteItem
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(context: Context):LocalSource {

    private val dao:DAO by lazy{
        val db:WeatherDataBase = WeatherDataBase.getInstance(context)
        db.getDao()
    }
    override fun getFavPlaces(): Flow<List<FavoriteItem>> {
        return dao.getAllFavorites()
    }

    override suspend fun insertToFavPlaces(favoriteItem: FavoriteItem) {
        dao.insertFavPlace(favoriteItem)
    }

    override suspend fun deleteFromFavPlaces(favoriteItem: FavoriteItem) {
        dao.deleteFavPlace(favoriteItem)
    }

  /*  override fun getAlarms(): Flow<List<Alarm>> {
        return dao.getAllAlarms()

    }

    override suspend fun insertToAlarms(alarm: Alarm) {
        dao.insertAlarm(alarm)
    }

    override suspend fun deleteFromAlarms(alarm: Alarm) {
        dao.deleteAlarm(alarm)
    }*/

}