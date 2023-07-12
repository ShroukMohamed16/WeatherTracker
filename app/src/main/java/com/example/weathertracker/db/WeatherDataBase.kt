package com.example.weathertracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weathertracker.model.*

@Database(entities = [FavoriteItem::class, Alarm::class,WeatherEntity::class], version = 3)
@TypeConverters(TypeConverter::class)

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