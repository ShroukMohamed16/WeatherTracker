package com.example.weathertracker

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

object Constants {
    const val PREFERENCE_NAME = "my_prefs"
    const val Lat_KEY = "latitude"
    const val Lon_Key = "longitude"
    const val FAV_Lat_KEY = "fav_latitude"
    const val FAV_Lon_Key = "fav_longitude"
    const val LOCATION_PERMISSION_REQUEST_CODE = 100
    const val LOCATION_PERMISSION_REQUEST_CODE_GPS = 200
    const val LOCATION_NAME = "locationName"
    const val FAV_LOCATION_NAME = "favLocationName"
    const val PERMISSIONS_IS_ENABLED = "PermissionISEnabled"
    const val MAP_DESTINATION = "mapDestination"


    fun setIcon(icon:String):Int {
        var newIcon:Int = 0
        when(icon){
            "01d" -> newIcon = R.drawable.clear_sun
            "02d" -> newIcon = R.drawable.few_clouds_sun
            "03d" -> newIcon = R.drawable.many_clouds_sun
            "04d","04n" -> newIcon = R.drawable.broken_clouds
            "09d" -> newIcon = R.drawable.shower_rain_sun
            "10d" -> newIcon = R.drawable.rain_sun
            "11d" -> newIcon = R.drawable.thunderstorm_sun
            "13d","13n" -> newIcon = R.drawable.snow_icon
            "50d","50n" -> newIcon = R.drawable.mist

            "01n" -> newIcon = R.drawable.clear_moon
            "02n" -> newIcon = R.drawable.few_clouds_moon
            "03n" -> newIcon = R.drawable.many_clouds_moon
            "09n" -> newIcon = R.drawable.shower_rain_moon
            "10n" -> newIcon = R.drawable.rain_moon
            "11n" -> newIcon = R.drawable.thunderstorm_moon
        }
        return newIcon
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(dt:Long, timeZone: String): String {
        val instant = Instant.ofEpochSecond(dt)
        val zoneId = ZoneId.of(timeZone)
        val zonedDateTime = instant.atZone(zoneId)
        val formatter = DateTimeFormatter.ofPattern("K:mm a")
        return formatter.format(zonedDateTime)
        }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDay(timestamp:Long):String{
        val instant = Instant.ofEpochSecond(timestamp)
        val zoneId = ZoneId.systemDefault()
        val zonedDateTime = instant.atZone(zoneId)
        val localDate = zonedDateTime.toLocalDate()
        val dayName:String = localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        return dayName
    }

    fun getDayWithSpecificFormat(timestamp: Long):String{
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("EEE,d MMM", Locale.getDefault())
        val formattedDate = sdf.format(date)
        return formattedDate
    }


}