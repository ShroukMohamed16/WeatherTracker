package com.example.weathertracker.model

import android.util.Log
import com.example.weathertracker.db.LocalSource
import com.example.weathertracker.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.ResponseBody
import kotlin.math.log

private const val TAG = "Repository"
class Repository(private var remoteSource: RemoteSource,var localSource: LocalSource) :RepositoryInterface{

    companion object{
        private var instance:Repository? = null

        fun getInstance(remoteSource:RemoteSource,localSource: LocalSource):Repository{
            return instance?: synchronized(this){
                val temp = Repository(remoteSource,localSource)
                instance = temp
                temp
            }

        }
    }

    override suspend fun getWeather(lat: Double, lon: Double, units:String,lang:String,apiKey: String): Flow<MyResponse>? {
        Log.i(TAG, "getWeather: data found")
        return flow{
            val result = remoteSource.getWeather(lat,lon,units,lang,apiKey)
            emit(result)
            Log.i(TAG, "getWeather: ${remoteSource.getWeather(lat,lon,units,lang,apiKey).current.uvi}")}
    }

    override fun getFavPlacesFromRoom(): Flow<List<FavoriteItem>> {
        return localSource.getFavPlaces()
    }

    override suspend fun insertToFavPlacesFromRoom(favoriteItem: FavoriteItem) {
        localSource.insertToFavPlaces(favoriteItem)
    }

    override suspend fun deleteFromFavPlacesFromRoom(favoriteItem: FavoriteItem) {
        localSource.deleteFromFavPlaces(favoriteItem)
    }

    override fun getAlarmsFromRoom(): Flow<List<Alarm>> {
        return localSource.getAlarms()
    }

    override suspend fun insertAlarmToRoom(alarm: Alarm) {
        localSource.insertToAlarms(alarm)
    }

    override suspend fun deleteAlarmFromRoom(start:Long , end:Long) {
        Log.i(TAG, "deleteAlarmFromRoom: delet alarm in repo")
        localSource.deleteFromAlarms(start , end)
    }

   override suspend fun insertWeatherToRoom(weatherEntity: WeatherEntity) {
        localSource.insertToWeather(weatherEntity)
    }

    override fun getWeatherFromRoom(): Flow<WeatherEntity>? {
        return localSource.getFromWeather()
    }
}