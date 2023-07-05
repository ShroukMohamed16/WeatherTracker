package com.example.weathertracker.model

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface RepositoryInterface {
    suspend fun getWeather(lat:Double , lon:Double, apiKey:String): Flow<MyResponse>?
}