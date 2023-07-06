package com.example.weathertracker.network

import com.example.weathertracker.model.MyResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody

class ApiClient : RemoteSource{

    private val apiService :ApiService by lazy {
        RetrofitHelper.retrofit.create(ApiService::class.java)
    }

    companion object {
        private var instance: ApiClient? = null
        fun getInstance(): ApiClient{
            return instance?: synchronized(this){
                    val temp = ApiClient()
                    instance = temp
                    temp
            }
        }
    }


    override suspend fun getWeather(lat: Double, lon: Double, units:String,lang:String,apiKey: String): MyResponse {
        return apiService.getWeather(lat,lon,units,lang,apiKey)

    }
}