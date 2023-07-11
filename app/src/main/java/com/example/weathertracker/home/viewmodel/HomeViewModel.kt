package com.example.weathertracker.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertracker.home.ApiState
import com.example.weathertracker.favorite.HomeRoomState
import com.example.weathertracker.model.RepositoryInterface
import com.example.weathertracker.model.WeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"
class HomeViewModel(private val repositoryInterface: RepositoryInterface):ViewModel() {

    private val mutableWeather:MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)

    val weather :StateFlow<ApiState> = mutableWeather

    private val mutableWeatherFromRoom:MutableStateFlow<HomeRoomState> = MutableStateFlow(HomeRoomState.Loading)

    val weatherFromRoom :StateFlow<HomeRoomState> = mutableWeatherFromRoom


    fun getWeatherFromRetrofit(lat:Double, lon:Double,units:String, lang:String,apiKey:String) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.getWeather(lat, lon, units, lang, apiKey)
                    ?.catch { e->
                        mutableWeather.value= ApiState.Failure(e)
                    }
                    ?.collect{ response->
                        mutableWeather.value = ApiState.Success(response)

                    }
            }
    }

    fun insertWeatherDataToRoom(weather:WeatherEntity){
        viewModelScope.launch(Dispatchers.IO){
            repositoryInterface.insertWeatherToRoom(weather)
        }
    }
    fun getWeatherDataFromRoom(){
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.getWeatherFromRoom()
                ?.catch { e->
                    mutableWeatherFromRoom.value= HomeRoomState.Failure(e)
                }
                ?.collect{ response->
                       if(response == null){
                           mutableWeatherFromRoom.value= HomeRoomState.Failure(Throwable())
                       }else {
                           mutableWeatherFromRoom.value = HomeRoomState.Success(response)
                       }
                }
        }

    }

}