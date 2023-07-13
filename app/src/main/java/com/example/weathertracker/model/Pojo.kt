package com.example.weathertracker.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezoneOffset: Int,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val alerts: List<Alert>? )
{
    @PrimaryKey
    var id:Int = 0

}

data class MyResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezoneOffset: Int,
    val current: CurrentWeather,
    val minutely: List<MinutelyWeather>,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val alerts: List<Alert>? )



data class CurrentWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val windSpeed: Double,
    val windDeg: Int,
    val weather: List<Weather>
)

data class MinutelyWeather(
    val dt: Long,
    val precipitation: Double
)

data class HourlyWeather(
    val dt: Long,
    val temp: Double,
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val windSpeed: Double,
    val windDeg: Int,
    val windGust: Double?,
    val weather: List<Weather>,
    val pop: Double
)

data class DailyWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moonPhase: Double,
    val temp: Temperature,
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val windSpeed: Double,
    val windDeg: Int,
    val windGust: Double?,
    val weather: List<Weather>,
    val clouds: Int,
    val pop: Double,
    val uvi: Double
)

data class Alert(
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val tag:String,
    val description: String
)

data class Temperature(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)