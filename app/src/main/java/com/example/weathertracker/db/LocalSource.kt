package com.example.weathertracker.db

import androidx.room.Delete
import com.example.weathertracker.model.FavoriteItem
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    fun getFavPlaces(): Flow<List<FavoriteItem>>
    suspend fun insertToFavPlaces(favoriteItem: FavoriteItem)
    suspend fun deleteFromFavPlaces(favoriteItem: FavoriteItem)
}