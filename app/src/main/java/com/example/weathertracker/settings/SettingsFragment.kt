package com.example.weathertracker.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import com.example.weathertracker.Constants
import com.example.weathertracker.MainActivity
import com.example.weathertracker.R
import com.example.weathertracker.databinding.FragmentSettingsBinding
import com.example.weathertracker.map.view.MapActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor:SharedPreferences.Editor
    lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.langRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.arabicRadioBtn -> {
                        editor.putString(Constants.LOCAL_LANGUAGE, "ar").apply()
                        GlobalScope.launch(Dispatchers.Main) {
                            setLanguage(requireContext(), "ar")
                        }
                }
                R.id.english_radio_btn -> {
                    editor.putString(Constants.LOCAL_LANGUAGE, "en").apply()
                    GlobalScope.launch(Dispatchers.Main) {
                        setLanguage(requireContext(), "en")
                    }
                }
            }
            }
        binding.notfRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.enable_radio_btn -> {
                  //  enableNotifications()
                    editor.putBoolean(Constants.NOTIFICATIONS_IS_ENABLED,true)
                    Toast.makeText(requireContext(),getString(R.string.notification_enabled),Toast.LENGTH_LONG).show()


                }
                R.id.disable_radio_btn -> {
                    /*val notificationManager =  requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancelAll()*/
                    editor.putBoolean(Constants.NOTIFICATIONS_IS_ENABLED,false)
                    Toast.makeText(requireContext(),getString(R.string.notification_disable),Toast.LENGTH_LONG).show()

                }
            }
        }
        binding.locRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.gps_radio_btn -> {
                       getLastLocation()
                }
                R.id.map_radio_btn -> {

                    val intent = Intent(requireActivity(), MapActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.tempRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.celsius_radio_btn -> {
                    editor.putString("units", "metric").apply()
                    editor.putString("unitCharacter",getString(R.string.c)).apply()
                }
                R.id.fahren_radio_btn -> {
                    editor.putString("units", "imperial").apply()
                    editor.putString("unitCharacter",getString(R.string.f)).apply()


                }
                R.id.kelvin_radio_btn ->{
                    editor.putString("units", "standard").apply()
                    editor.putString("unitCharacter",getString(R.string.k)).apply()


                }
            }
        }
        binding.windRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.meter_radio_btn -> {
                    editor.putString("wind_units", "meter").apply()
                }
                R.id.mile_radio_btn -> {
                    editor.putString("wind_units", "mile").apply()

                }

            }
        }

    }

    fun  setLanguage(context: Context, language:String) {
        val locale = Locale(language)
        val config = context.resources.configuration
        config.locale= Locale(language)
        Locale.setDefault(locale)
        config.setLayoutDirection(Locale(language))
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        context.createConfigurationContext(config)

      //  requireActivity().recreate()
    }
    fun getLastLocation() {
        if(checkPermissions()){
            if(isLocationEnabled()){
                requestNewLocation()
            }else{
                val intent =Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            requestPermission()
        }
    }

    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationRequest: LocationResult?) {
            val lastLocation = locationRequest!!.lastLocation
            editor.putString(Constants.Lat_KEY,lastLocation.latitude.toString())
            editor.putString(Constants.Lon_Key,lastLocation.longitude.toString())
            editor.commit()

        }
    }

    @SuppressLint("MissingPermission")
    fun requestNewLocation() {
        val request: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority  = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
         fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
         fusedLocationProviderClient.requestLocationUpdates(request,locationCallback, Looper.myLooper()).addOnSuccessListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }.addOnFailureListener {
        }
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = requireContext()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    }

    fun requestPermission() {

        ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION),
            Constants.LOCATION_PERMISSION_REQUEST_CODE_GPS
        )

    }

    fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE_GPS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                editor.putBoolean(Constants.PERMISSIONS_IS_ENABLED,true)
                editor.commit()
            } else {
                editor.putBoolean(Constants.PERMISSIONS_IS_ENABLED,false)
                editor.commit()
            }
        }
    }

    private fun enableNotifications() {
        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val descriptionText = "My Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            }

            val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        // Show a notification
        val notification = NotificationCompat.Builder(requireContext(), Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.snow_icon)
            .setContentTitle("My Notification")
            .setContentText("This is my notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager =requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
        Toast.makeText(requireContext(),getString(R.string.notification_enabled),Toast.LENGTH_LONG)
    }




}