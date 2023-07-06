package com.example.weathertracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weathertracker.dialogs.ConfirmDialog
import com.example.weathertracker.model.FavoriteItem
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

private const val TAG = "MapActivity"
class MapActivity : AppCompatActivity() {
    lateinit var mapView:MapView
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var  editor :SharedPreferences.Editor
    private var marker:Marker? = null
    var favoriteItem:FavoriteItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        var permissionIsEnabled:Boolean =sharedPreferences.getBoolean(Constants.PERMISSIONS_IS_ENABLED,false)
        editor = sharedPreferences.edit()
        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)

        checkPermissions()

    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ||  grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                editor.putBoolean(Constants.PERMISSIONS_IS_ENABLED,true)
                editor.commit()
                initMap()
            } else {
                Log.i(TAG, "onRequestPermissionsResult: Cannot select Location")
                Toast.makeText(this,"Cannot select Location",Toast.LENGTH_LONG)
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun initMap() {

        mapView.getMapAsync { googleMap ->
            marker = googleMap.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)))
            googleMap.isMyLocationEnabled = true
                googleMap.setOnMapClickListener { latLng ->
                    updateMarkerPosition(latLng)
                    val latitude = latLng.latitude
                    val longitude = latLng.longitude
                    if(sharedPreferences.getString(Constants.MAP_DESTINATION, "initial") == "initial"){
                    editor.putString(Constants.Lat_KEY, latitude.toString())
                    editor.putString(Constants.Lon_Key, longitude.toString())
                    editor.commit()
                    }
                    else{
                        editor.putString(Constants.FAV_Lat_KEY, latitude.toString())
                        editor.putString(Constants.FAV_Lon_Key, longitude.toString())
                        editor.commit()
                        favoriteItem?.lat = latitude
                        favoriteItem?.lon = longitude
                    }
                    showConfirmDialog()
                }
        }

    }


    private fun checkPermissions(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION),
                Constants.LOCATION_PERMISSION_REQUEST_CODE)
        }else{
            initMap()
        }

    }
    private fun showConfirmDialog(){
        supportFragmentManager
            .beginTransaction()
            .add(ConfirmDialog(),"ConfirmDialog")
            .commit()
    }
    private fun updateMarkerPosition(latLng: LatLng) {
        marker?.position = latLng
    }
}