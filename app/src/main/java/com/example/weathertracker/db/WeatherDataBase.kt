package com.example.weathertracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.FavoriteItem

@Database(entities = [FavoriteItem::class, Alarm::class], version = 9)
abstract class WeatherDataBase:RoomDatabase(){
        abstract fun getDao(): DAO
        companion object {
            @Volatile
            private var INSTANCE: WeatherDataBase? = null

            fun getInstance(context: Context): WeatherDataBase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDataBase::class.java, "weather_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                    instance
                }
                }
            }

}