package com.example.weathertracker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.protobuf.Value
import java.text.SimpleDateFormat
import android.content.res.Resources

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle


import java.util.*

object Constants {
    const val LOCATION_SOURCE: String = "map"
    const val HOME_DESTINATIO = "homeDestination"
    const val isInitializedTag = "INIT_STATE"
    const val PREFERENCE_NAME = "my_prefs"
    const val Lat_KEY = "latitude"
    const val Lon_Key = "longitude"
    const val FAV_Lat_KEY = "fav_latitude"
    const val FAV_Lon_Key = "fav_longitude"
    const val LOCATION_PERMISSION_REQUEST_CODE = 100
    const val LOCATION_PERMISSION_REQUEST_CODE_GPS = 1
    const val LOCATION_NAME = "locationName"
    const val FAV_LOCATION_NAME = "favLocationName"
    const val PERMISSIONS_IS_ENABLED = "PermissionISEnabled"
    const val NOTIFICATIONS_IS_ENABLED = "NotificationISEnabled"
    const val MAP_DESTINATION = "mapDestination"
    const val API_KEY = "eaa7758b00a4ea8b138646fe349149d1"
    const val CHANNEL_ID = "my_channel_id"
    const val NOTIFICATION_ID = 1
    const val LOCAL_LANGUAGE = "lang"
    const val DRAW_OVER_OTHER_APPS_REQUEST_CODE = 5469
    const val UNITS = "units"
    const val UNITS_CHARACTER = "unitCharacter"
    const val WIND_SPEED_UNIT = "wind_units"


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
    fun getTime(dt:Long, timeZone: String,lang: String): String {
        val instant = Instant.ofEpochSecond(dt)
        val zoneId = ZoneId.of(timeZone)
        val zonedDateTime = instant.atZone(zoneId)
        val loc = Locale(lang)
        val formatter = DateTimeFormatter.ofPattern("K:mm a").withLocale(loc)
        return formatter.format(zonedDateTime)
        }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDay(dt: Int,lang:String): String {
        val format = SimpleDateFormat("E",Locale(lang))
        val date = Date(dt.toLong() * 1000)
        return format.format(date)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayWithSpecificFormat(lang:String):String{
        val localDate = LocalDate.now()
        val loc = Locale(lang)
        val formatter = DateTimeFormatter.ofPattern("EEE,d MMM").withLocale(loc)
        val formattedDate = localDate.format(formatter)
        return formattedDate
    }
    fun showNoNetworkDialog(context: Context){
        val resources = context.resources
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setMessage(resources.getString(R.string.no_network_connection))
        val dialog = builder.create()
        dialog.show()
    }


}