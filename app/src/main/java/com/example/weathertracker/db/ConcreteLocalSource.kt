package com.example.weathertracker.db

import android.content.Context
import android.util.Log
import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.MyResponse
import com.example.weathertracker.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

private const val TAG = "ConcreteLocalSource"
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

    override fun getAlarms(): Flow<List<Alarm>> {
        return dao.getAllAlarms()

    }

    override suspend fun insertToAlarms(alarm: Alarm) {
        dao.insertAlarm(alarm)
    }

    override suspend fun deleteFromAlarms(start:Long , end:Long) {
        Log.i(TAG, "deleteFromAlarms: delete alarm in local")
        dao.deleteAlarm(start,end)
    }

  override suspend fun insertToWeather(weatherEntity: WeatherEntity) {
        dao.insertWeather(weatherEntity)
    }

    override fun getFromWeather(): Flow<WeatherEntity>? {
        return dao.getWeather()
    }

}