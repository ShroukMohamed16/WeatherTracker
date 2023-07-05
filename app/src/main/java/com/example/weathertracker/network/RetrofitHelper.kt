package com.example.weathertracker.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    const val base_Url = "https://api.openweathermap.org/data/2.5/"
    val gson = GsonBuilder().create()
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(base_Url)
        .build()
}