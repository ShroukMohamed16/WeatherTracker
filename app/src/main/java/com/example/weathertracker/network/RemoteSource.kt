package com.example.weathertracker.network

import com.example.weathertracker.model.MyResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface RemoteSource {
    suspend fun getWeather(lat:Double,lon:Double,units:String,apiKey:String):MyResponse
}