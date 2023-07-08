package com.example.weathertracker.alarm

import com.example.weathertracker.model.Alarm


sealed class AlarmRoomState{
    class Success(val data: List<Alarm>): AlarmRoomState()
    class Failure(val msg:Throwable): AlarmRoomState()
    object Loading: AlarmRoomState()
}
