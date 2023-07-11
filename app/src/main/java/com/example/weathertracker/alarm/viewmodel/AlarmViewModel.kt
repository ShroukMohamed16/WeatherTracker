package com.example.weathertracker.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertracker.alarm.AlarmRoomState
import com.example.weathertracker.favorite.HomeRoomState
import com.example.weathertracker.favorite.RoomState
import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class AlarmViewModel(private val repositoryInterface: RepositoryInterface): ViewModel() {
    private val mutableAlarms: MutableStateFlow<AlarmRoomState> = MutableStateFlow(AlarmRoomState.Loading)
    val alarmsList : StateFlow<AlarmRoomState> = mutableAlarms

    private val mutableWeatherAlarms: MutableStateFlow<HomeRoomState> = MutableStateFlow(HomeRoomState.Loading)
    val weatherAlarmsList : StateFlow<HomeRoomState> = mutableWeatherAlarms


    fun getAllAlarms(){
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.getAlarmsFromRoom()
                ?.catch { e->
                    mutableAlarms.value= AlarmRoomState.Failure(e)
                }
                ?.collect{ alarm->
                    mutableAlarms.value = AlarmRoomState.Success(alarm)
                }
        }
    }
    fun getWeatherAlarms(){
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.getWeatherFromRoom()
                ?.catch { e->
                    mutableWeatherAlarms.value= HomeRoomState.Failure(e)
                }
                ?.collect{ weather->
                    mutableWeatherAlarms.value = HomeRoomState.Success(weather)
                }
        }
    }
    fun insertAlarm(alarm: Alarm){
        viewModelScope.launch(Dispatchers.IO){
            repositoryInterface.insertAlarmToRoom(alarm)
            getAllAlarms()
        }
    }
    fun deleteAlarm(alarm: Alarm){
        viewModelScope.launch(Dispatchers.IO){
            repositoryInterface.deleteAlarmFromRoom(alarm)
            getAllAlarms()
        }
    }



}