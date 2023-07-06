package com.example.weathertracker.network

import com.example.weathertracker.model.MyResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("onecall")
    suspend fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double,@Query("units") units:String,
                           @Query("appid") apiKey: String): MyResponse
}