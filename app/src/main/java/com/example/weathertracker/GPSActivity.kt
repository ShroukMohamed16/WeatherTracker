package com.example.weathertracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.*

private const val TAG = "GPSActivity"
class GPSActivity : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var sharedPreferences: SharedPreferences
    lateinit var  editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpsactivity)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPreferences =getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        getLastLocation()
    }




    private fun getLastLocation() {
        Log.i(TAG, "getLastLocation: ")
        if(checkPermissions())
        {
            Log.i(TAG, "getLastLocation: permissions on")
            if(isLocationEnabled()){
                Log.i(TAG, "getLastLocation: provider  on")

                requestNewLocation()
            }else{
                Log.i(TAG, "getLastLocation: provider off")

                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        }else{
            Log.i(TAG, "getLastLocation: permissions off")
            requestPermission()
        }
    }

    private val locationCallback:LocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            Log.i(TAG, "onLocationResult: ")
            val lastLocation = locationResult!!.lastLocation
            editor.putString(Constants.Lat_KEY,lastLocation.latitude.toString()).apply()
            editor.putString(Constants.Lon_Key,lastLocation.longitude.toString()).apply()
            val geo= Geocoder(this@GPSActivity, Locale.getDefault())
            val address = geo.getFromLocation(lastLocation.latitude,lastLocation.longitude,1)
            editor.putString(Constants.LOCATION_NAME,address!![0].getAddressLine(0).toString()).apply()

        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {
        Log.i(TAG, "requestNewLocation: ")
        val request = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this) //not understand this
        fusedLocationProviderClient.requestLocationUpdates(request,
            locationCallback,Looper.myLooper()
        )

    }

    private fun isLocationEnabled(): Boolean {
        Log.i(TAG, "isLocationEnabled: ")
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        Log.i(TAG, "requestPermission: ")
        ActivityCompat.requestPermissions(this ,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION ,
                android.Manifest.permission.ACCESS_COARSE_LOCATION),
            Constants.LOCATION_PERMISSION_REQUEST_CODE_GPS)
    }

    private fun checkPermissions(): Boolean {
        Log.i(TAG, "checkPermissions: ")
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED

    }
     /*   fun getLastLocation() {
            if(checkPermissions()){
                Log.i(TAG, "getLastLocation: permission off")
                if(isLocationEnabled()){
                    Log.i(TAG, "getLastLocation: provider enabled")
                    requestNewLocation()
                }else{
                    Log.i(TAG, "getLastLocation: permission on")
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            }else{
                requestPermission()
            }
        }

        val locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationRequest: LocationResult?) {
                Log.i(TAG, "onLocationResult: callback")
                val lastLocation = locationRequest!!.lastLocation
                Log.i(TAG, "onLocationResult: ${lastLocation.latitude} & ${lastLocation.longitude}")
                editor.putString(Constants.Lat_KEY,lastLocation.latitude.toString())
                editor.putString(Constants.Lon_Key,lastLocation.longitude.toString())
                editor.commit()

            }
        }

        @SuppressLint("MissingPermission")
        fun requestNewLocation() {
            Log.i(TAG, "requestNewLocation: ")
            val request: LocationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority  = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(request,locationCallback, Looper.myLooper()).addOnSuccessListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Log.i(TAG, "requestNewLocation: Faileeeeeeeeed")
            }
        }

        fun isLocationEnabled(): Boolean {
            Log.i(TAG, "isLocationEnabled: ")
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        }
        fun checkPermissions(): Boolean {
            Log.i(TAG, "checkPermissions: ")
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        }

        fun requestPermission() {
            ActivityCompat.requestPermissions(this,arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION), Constants.LOCATION_PERMISSION_REQUEST_CODE_GPS)
            Log.i(TAG, "requestPermission: ")
        }
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE_GPS) {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    || (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    editor.putBoolean(Constants.PERMISSIONS_IS_ENABLED,true)
                    editor.commit()
                    Log.i(TAG, "onRequestPermissionsResult: ")
                } else {
                    editor.putBoolean(Constants.PERMISSIONS_IS_ENABLED,false)
                    editor.commit()
                }
            }
        }*/

    }
