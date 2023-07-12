package com.example.weathertracker.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
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
import java.util.*

private const val TAG = "SettingsFragment"
class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor:SharedPreferences.Editor
    lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    lateinit var  locationCallback:LocationCallback
    lateinit var myContext:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
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

        getSettings()
        
        binding.arabicRadioBtn.setOnClickListener{
            binding.arabicRadioBtn.isChecked = true
            editor.putString(Constants.LOCAL_LANGUAGE, "ar").apply()
            setLanguage(requireContext(), "ar")
            requireActivity().recreate()
        }
        binding.englishRadioBtn.setOnClickListener {
            binding.englishRadioBtn.isChecked = true
            editor.putString(Constants.LOCAL_LANGUAGE, "en").apply()
            setLanguage(requireContext(), "en")
            requireActivity().recreate()
        }

        binding.enableRadioBtn.setOnClickListener {
            binding.enableRadioBtn.isChecked = true
            editor.putBoolean(Constants.NOTIFICATIONS_IS_ENABLED,true).apply()
            Toast.makeText(requireContext(),getString(R.string.notification_enabled),Toast.LENGTH_LONG).show()
        }
        binding.disableRadioBtn.setOnClickListener{
            binding.disableRadioBtn.isChecked = true
            editor.putBoolean(Constants.NOTIFICATIONS_IS_ENABLED,false).apply()
            Toast.makeText(requireContext(),getString(R.string.notification_disable),Toast.LENGTH_LONG).show()

        }

        binding.gpsRadioBtn.setOnClickListener {
            binding.gpsRadioBtn.isChecked = true
            if(isNetworkConnected()) {
                editor.putString(Constants.LOCATION_SOURCE,"gps").apply()
                getLastLocation()
            }else{
                Constants.showNoNetworkDialog(myContext)
            }
        }
        binding.mapRadioBtn.setOnClickListener {
            binding.mapRadioBtn.isChecked = true
            if(isNetworkConnected()) {
                editor.putString(Constants.LOCATION_SOURCE,"map").apply()
                val intent = Intent(myContext, MapActivity::class.java)
                startActivity(intent)
            }else{
                Constants.showNoNetworkDialog(myContext)
            }
        }

       binding.celsiusRadioBtn.setOnClickListener{
            binding.celsiusRadioBtn.isChecked = true
            editor.putString(Constants.UNITS, "metric").apply()
            editor.putString(Constants.UNITS_CHARACTER,getString(R.string.c)).apply()
        }
       binding.fahrenRadioBtn.setOnClickListener{
            binding.fahrenRadioBtn.isChecked = true
            editor.putString(Constants.UNITS, "imperial").apply()
            editor.putString(Constants.UNITS_CHARACTER,getString(R.string.f)).apply()
        }
       binding.kelvinRadioBtn.setOnClickListener{
           binding.kelvinRadioBtn.isChecked = true
           editor.putString(Constants.UNITS, "standard").apply()
           editor.putString(Constants.UNITS_CHARACTER,getString(R.string.k)).apply()
       }
        binding.meterRadioBtn.setOnClickListener {
            binding.meterRadioBtn.isChecked = true
            editor.putString(Constants.WIND_SPEED_UNIT, "meter").apply()
        }
        binding.mileRadioBtn.setOnClickListener{
            binding.mileRadioBtn.isChecked = true
            editor.putString(Constants.WIND_SPEED_UNIT, "mile").apply()

        }

}



    private fun  setLanguage(context: Context, language:String) {
        val locale = Locale(language)
        val config = context.resources.configuration
        config.locale= Locale(language)
        Locale.setDefault(locale)
        config.setLayoutDirection(Locale(language))
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        context.createConfigurationContext(config)
    }


    private fun getLastLocation() {
        if(checkPermissions())
        {
            editor.putBoolean(Constants.PERMISSIONS_IS_ENABLED,true).apply()
            if(isLocationEnabled()){
                Log.i(TAG, "getLastLocation: provider  on")
                requestNewLocation()
            }else{
                Log.i(TAG, "getLastLocation: provider off")

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        }else{
            Log.i(TAG, "getLastLocation: permissions off")
            requestPermission()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            myContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    myContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED

    }

    private fun isLocationEnabled(): Boolean {
        Log.i(TAG, "isLocationEnabled: ")
        val locationManager: LocationManager =requireActivity()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {
        Log.i(TAG, "requestNewLocation: ")
        val request = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback(){
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.i(TAG, "onLocationResult: ")
                val lastLocation = locationResult!!.lastLocation
                editor.putString(Constants.Lat_KEY,lastLocation.latitude.toString()).apply()
                editor.putString(Constants.Lon_Key,lastLocation.longitude.toString()).apply()
                val geo= Geocoder(myContext, Locale.getDefault())
                val address = geo.getFromLocation(lastLocation.latitude,lastLocation.longitude,1)
                Log.i(TAG, "onLocationResult: ${address!![0].subAdminArea}")
                editor.putString(Constants.LOCATION_NAME,address[0].subAdminArea).apply()
                editor.putBoolean(Constants.PERMISSIONS_IS_ENABLED,true).apply()

                fusedLocationProviderClient.removeLocationUpdates(locationCallback) // to stop callback loop
            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(request,
            locationCallback,null)
    }

    private fun requestPermission() {
        Log.i(TAG, "requestPermissions: ")
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), Constants.LOCATION_PERMISSION_REQUEST_CODE_GPS)

    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionsResult: ")
        if (isAdded) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE_GPS) {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLastLocation()
                } else {
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    fun getSettings() {
        if (sharedPreferences.getString(Constants.LOCATION_SOURCE, "map") == "map")
            binding.mapRadioBtn.isChecked = true
        else
            binding.gpsRadioBtn.isChecked = true

        if (sharedPreferences.getBoolean(Constants.NOTIFICATIONS_IS_ENABLED, false))
            binding.enableRadioBtn.isChecked = true
        else
            binding.disableRadioBtn.isChecked = true

        if (sharedPreferences.getString(Constants.LOCAL_LANGUAGE,"en") == "ar")
            binding.arabicRadioBtn.isChecked = true
        else
            binding.englishRadioBtn.isChecked = true

        if(sharedPreferences.getString(Constants.UNITS,"metric")=="metric")
            binding.celsiusRadioBtn.isChecked = true
        else if(sharedPreferences.getString(Constants.UNITS,"metric") == "imperial")
            binding.fahrenRadioBtn.isChecked = true
        else
            binding.kelvinRadioBtn.isChecked = true

        if(sharedPreferences.getString(Constants.WIND_SPEED_UNIT,"meter")=="meter")
            binding.meterRadioBtn.isChecked = true
        else
            binding.mileRadioBtn.isChecked = true


    }

}