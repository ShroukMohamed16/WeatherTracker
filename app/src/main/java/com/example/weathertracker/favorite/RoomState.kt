package com.example.weathertracker.favorite

import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.WeatherEntity

sealed class RoomState {
    class Success(val data: List<FavoriteItem>): RoomState()
    class Failure(val msg:Throwable): RoomState()
    object Loading: RoomState()

}

sealed class HomeRoomState {
    class Success(val data: WeatherEntity): HomeRoomState()
    class Failure(val msg:Throwable): HomeRoomState()
    object Loading: HomeRoomState()

}
