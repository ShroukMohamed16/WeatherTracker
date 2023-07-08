package com.example.weathertracker.favorite

import com.example.weathertracker.model.FavoriteItem

sealed class RoomState {
    class Success(val data: List<FavoriteItem>): RoomState()
    class Failure(val msg:Throwable): RoomState()
    object Loading: RoomState()
}