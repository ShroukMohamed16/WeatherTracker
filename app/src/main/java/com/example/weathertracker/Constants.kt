package com.example.weathertracker

object Constants {
    const val PREFERENCE_NAME = "my_prefs"
    const val Lat_KEY = "latitude"
    const val Lon_Key = "longitude"

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


}