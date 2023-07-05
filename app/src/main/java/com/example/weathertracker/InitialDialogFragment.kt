package com.example.weathertracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.button.MaterialButton

private const val TAG = "InitialDialogFragment"
const val PERMISSION_ID = 1
class InitialDialogFragment : DialogFragment() {
    lateinit var gpsRadioButton: RadioButton
    lateinit var mapRadioButton: RadioButton
    lateinit var enableNotificationRadioButton: RadioButton
    lateinit var disableNotificationRadioButton: RadioButton
    lateinit var notificationManager: NotificationManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var sharedPreferences:SharedPreferences
    lateinit var  editor:SharedPreferences.Editor
    lateinit var okButton: MaterialButton

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
        val view:View = inflater.inflate(R.layout.fragment_initial_dialog, container, false)
        gpsRadioButton = view.findViewById(R.id.gps_initial_dialog_radioButton)
        mapRadioButton = view.findViewById(R.id.map_initial_dialog_radioButton)
        enableNotificationRadioButton = view.findViewById(R.id.enable_notification_dialog_radioButton)
        disableNotificationRadioButton = view.findViewById(R.id.disable_notification_initial_dialog_radioButton)
        okButton = view.findViewById(R.id.ok_btn_initial_dialog)
        return view
    }


    private fun getLastLocation() {
        if(checkPermissions()){
            if(isLocationEnabled()){
                requestLastLocation()

            }else{
                val intent =Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        }else{
            requestPermission()
        }
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationRequest: LocationResult?) {
            val lastLocation = locationRequest!!.lastLocation
            Log.i(TAG, "onLocationResult: ${lastLocation.latitude} & ${lastLocation.longitude}")
            editor.putString(Constants.Lat_KEY,lastLocation.latitude.toString())
            editor.putString(Constants.Lon_Key,lastLocation.longitude.toString())
            editor.apply()

        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLastLocation() {
        val request:LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority  = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(request,locationCallback,Looper.myLooper()).addOnSuccessListener {
            val intent = Intent(requireActivity(),MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID)

    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        okButton.setOnClickListener {
            when {
                mapRadioButton.isChecked -> {
                    val intent = Intent(requireContext(), MapActivity::class.java)
                    startActivity(intent)
                    dismiss()
                }

                gpsRadioButton.isChecked -> {
                    getLastLocation()

                    dismiss()
                }
            }
            when {
                enableNotificationRadioButton.isChecked -> {

                }

                disableNotificationRadioButton.isChecked -> {
                    notificationManager.cancelAll()
                    Log.i(TAG, "onViewCreated: Notification Disabled")
                    dismiss()
                }
            }
            editor.putBoolean(isInitializedTag,true)
            editor.apply()
        }


    }
}