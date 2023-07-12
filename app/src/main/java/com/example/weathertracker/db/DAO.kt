package com.example.weathertracker.db

import androidx.room.*
import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.MyResponse
import com.example.weathertracker.model.WeatherEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface DAO {

        //For Favorite Places
        @Query("SELECT * FROM fav_places")
        fun getAllFavorites(): Flow<List<FavoriteItem>>

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertFavPlace(favoriteItem: FavoriteItem)

        @Delete
        suspend fun deleteFavPlace(favoriteItem: FavoriteItem)

        //For Alarms

      @Query("SELECT * FROM alarm")
        fun getAllAlarms(): Flow<List<Alarm>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAlarm(alarm: Alarm)

        @Query("DELETE FROM alarm WHERE  startTime= :start And endTime=:end")
        suspend fun deleteAlarm(start:Long,end:Long)

        //For Weather
       @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertWeather(weatherEntity: WeatherEntity)

        @Query("SELECT * FROM weather")
        fun getWeather(): Flow<WeatherEntity>?



}