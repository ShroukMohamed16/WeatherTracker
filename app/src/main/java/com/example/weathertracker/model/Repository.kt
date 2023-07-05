package com.example.weathertracker.model

import android.util.Log
import com.example.weathertracker.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.ResponseBody
import kotlin.math.log

private const val TAG = "Repository"
class Repository(private var remoteSource: RemoteSource) :RepositoryInterface{

    companion object{
        private var instance:Repository? = null

        fun getInstance(remoteSource:RemoteSource):Repository{
            return instance?: synchronized(this){
                val temp = Repository(remoteSource)
                instance = temp
                temp
            }

        }
    }

    override suspend fun getWeather(lat: Double, lon: Double, apiKey: String): Flow<MyResponse>? {
        Log.i(TAG, "getWeather: data found")
        return flow{
            val result = remoteSource.getWeather(lat,lon,apiKey)
            emit(result)
            Log.i(TAG, "getWeather: ${remoteSource.getWeather(lat,lon,apiKey).current.uvi}")}
    }
}