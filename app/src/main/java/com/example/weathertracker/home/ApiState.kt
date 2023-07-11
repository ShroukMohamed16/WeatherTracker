package com.example.weathertracker.home

import com.example.weathertracker.model.MyResponse

sealed class ApiState{
    class Success(val data:MyResponse): ApiState()
    class Failure(val msg:Throwable): ApiState()
    object Loading: ApiState()
}
