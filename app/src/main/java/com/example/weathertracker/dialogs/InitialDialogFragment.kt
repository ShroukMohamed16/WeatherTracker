package com.example.weathertracker.dialogs

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.weathertracker.*
import com.example.weathertracker.R
import com.example.weathertracker.databinding.FragmentInitialDialogBinding
import com.example.weathertracker.map.view.MapActivity
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*

private const val TAG = "InitialDialogFragment"
class InitialDialogFragment : DialogFragment() {
    private lateinit var myContext: Context
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    lateinit var binding: FragmentInitialDialogBinding
    lateinit var  locationCallback:LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        sharedPreferences = requireActivity()
            .getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.myContext = context
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_initial_dialog, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.okBtnInitialDialog.setOnClickListener {
            when {
                binding.mapInitialDialogRadioButton.isChecked -> {
                    editor.putString(Constants.MAP_DESTINATION, "initial")
                    if(isNetworkConnected()) {
                        editor.putString(Constants.LOCATION_SOURCE,"map").apply()
                        val intent = Intent(myContext, MapActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                        dismiss()
                    }else{
                        Constants.showNoNetworkDialog(myContext)
                    }
                }
                binding.gpsInitialDialogRadioButton.isChecked -> {
                    if(isNetworkConnected()) {
                        editor.putString(Constants.LOCATION_SOURCE,"gps").apply()
                        getLastLocation()
                        checkEnableNotification()
                    }else{
                        Constants.showNoNetworkDialog(myContext)
                    }
                }
            }
        }
    }


    private fun checkEnableNotification(){
        when {
            binding.enableNotificationDialogRadioButton.isChecked -> {
                editor.putBoolean(Constants.NOTIFICATIONS_IS_ENABLED, true).apply()
                Toast.makeText(
                    myContext,
                    getString(R.string.notification_enabled),
                    Toast.LENGTH_LONG
                ).show()
            }
            binding.disableNotificationInitialDialogRadioButton.isChecked -> {
                editor.putBoolean(Constants.NOTIFICATIONS_IS_ENABLED, false).apply()
                Toast.makeText(
                    myContext,
                    getString(R.string.notification_disable),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
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
            override fun onLocationResult(locationResult: LocationResult?) {
                //Log.i(TAG, "onLocationResult: ")
                val lastLocation = locationResult!!.lastLocation
                editor.putString(Constants.Lat_KEY,lastLocation.latitude.toString()).apply()
                editor.putString(Constants.Lon_Key,lastLocation.longitude.toString()).apply()
                try {
                    val geo= Geocoder(myContext, Locale.getDefault())
                    val address = geo.getFromLocation(lastLocation.latitude,lastLocation.longitude,1)
                    Log.i(TAG, "onLocationResult: ${address!![0].subAdminArea}")
                    editor.putString(Constants.LOCATION_NAME,address[0].subAdminArea).apply()

                }catch (e:IOException){
                    e.printStackTrace()
                    println("Poor Internet Connection")
                }
               editor.putBoolean(Constants.PERMISSIONS_IS_ENABLED,true).apply()
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
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
                    // context.finish()
                }
            }
        }
    }
    private fun isNetworkConnected():Boolean{
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}