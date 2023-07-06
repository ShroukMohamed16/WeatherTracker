package com.example.weathertracker.dialogs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.weathertracker.*
import com.example.weathertracker.R
import com.example.weathertracker.databinding.FragmentInitialDialogBinding
import com.google.android.gms.location.*

private const val TAG = "InitialDialogFragment"

class InitialDialogFragment : DialogFragment() {
    lateinit var notificationManager: NotificationManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var sharedPreferences:SharedPreferences
    lateinit var  editor:SharedPreferences.Editor
    lateinit var binding: FragmentInitialDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager =  requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        sharedPreferences =requireActivity()
            .getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_initial_dialog, container, false)
        return binding.root
    }


     fun getLastLocation() {
        if(checkPermissions()){
            Log.i(TAG, "getLastLocation: permission off")
            if(isLocationEnabled()){
                Log.i(TAG, "getLastLocation: provider enabled")
                requestNewLocation()
            }else{
                Log.i(TAG, "getLastLocation: permission on")
                val intent =Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
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
        val request:LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority  = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(request,locationCallback,Looper.myLooper()).addOnSuccessListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }.addOnFailureListener {
            Log.i(TAG, "requestNewLocation: Faileeeeeeeeed")
        }
    }

     fun isLocationEnabled(): Boolean {
        Log.i(TAG, "isLocationEnabled: ")
        val locationManager = requireContext()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    }
    fun checkPermissions(): Boolean {
        Log.i(TAG, "checkPermissions: ")
        return ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION
            ,Manifest.permission.ACCESS_COARSE_LOCATION), Constants.LOCATION_PERMISSION_REQUEST_CODE_GPS)
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.okBtnInitialDialog.setOnClickListener {
            when {
                binding.mapInitialDialogRadioButton.isChecked -> {
                    editor.putString(Constants.MAP_DESTINATION,"initial")
                    val intent = Intent(requireContext(), MapActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    dismiss()
                }

                binding.gpsInitialDialogRadioButton.isChecked -> {
                   // getLastLocation()
                    val intent = Intent(requireActivity(),GPSActivity::class.java)
                    startActivity(intent)
                    dismiss()
                }
            }
            when {
                binding.enableNotificationDialogRadioButton.isChecked -> {
                    enableNotifications()
                    Toast.makeText(requireContext(),getString(R.string.notification_enabled), Toast.LENGTH_LONG)
                }

                binding.disableNotificationInitialDialogRadioButton.isChecked -> {
                    notificationManager.cancelAll()
                    Toast.makeText(requireContext(),getString(R.string.notification_disable),Toast.LENGTH_LONG)
                    dismiss()
                }
            }
            editor.putBoolean(isInitializedTag,true)
            editor.commit()
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
    }
}