package com.example.weathertracker.network

import com.example.weathertracker.model.*

class FakeRemoteDataSource(var myResponse: MyResponse) : RemoteSource{

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String,
        apiKey: String
    ): MyResponse {
        return myResponse
    }
}