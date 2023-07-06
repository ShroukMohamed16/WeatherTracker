package com.example.weathertracker.db

import androidx.room.*
import com.example.weathertracker.model.FavoriteItem
import kotlinx.coroutines.flow.Flow
@Dao
interface FavoriteWeatherDAO {
        @Query("SELECT * FROM fav_places")
        fun getAllFavorites(): Flow<List<FavoriteItem>>

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertFavPlace(favoriteItem: FavoriteItem)

        @Delete
        suspend fun deleteFavPlace(favoriteItem: FavoriteItem)

}