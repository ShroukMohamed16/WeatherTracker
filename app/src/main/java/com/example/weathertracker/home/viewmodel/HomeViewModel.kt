package com.example.weathertracker.home.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertracker.ApiState
import com.example.weathertracker.model.MyResponse
import com.example.weathertracker.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"
class HomeViewModel(private val repositoryInterface: RepositoryInterface):ViewModel() {

    private val mutableWeather:MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)

    val weather :StateFlow<ApiState> = mutableWeather



     fun getWeatherFromRetrofit(lat:Double, lon:Double,units:String, apiKey:String) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.getWeather(lat, lon, units,apiKey)
                ?.catch { e ->
                    mutableWeather.value = ApiState.Failure(e)
                }
                ?.collect{ myWeather ->
                    Log.i(TAG, "getWeatherFromRetrofit: data collected")
                    mutableWeather.value = ApiState.Success(myWeather)
                    Log.i(TAG, "getWeatherFromRetrofit: data collected ${myWeather.timezone}")

                }
        }
    }

}