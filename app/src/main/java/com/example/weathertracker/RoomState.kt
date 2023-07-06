package com.example.weathertracker

import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.MyResponse

sealed class RoomState {
    class Success(val data: List<FavoriteItem>):RoomState()
    class Failure(val msg:Throwable):RoomState()
    object Loading:RoomState()
}