package com.example.weathertracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.dialogs.ConfirmDialog
import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.Repository
import com.example.weathertracker.network.ApiClient
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "MapActivity"
class MapActivity : AppCompatActivity() {
    lateinit var mapView:MapView
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var  editor :SharedPreferences.Editor
    private var marker:Marker? = null
    var favoriteItem:FavoriteItem? = null
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        var permissionIsEnabled:Boolean =sharedPreferences.getBoolean(Constants.PERMISSIONS_IS_ENABLED,false)
        editor = sharedPreferences.edit()
        repository = Repository.getInstance(
            ApiClient.getInstance(),
            ConcreteLocalSource(this)
        )
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
        editor.putString(Constants.MAP_DESTINATION,"initial").commit()
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
        mapView.getMapAsync { googleMap->
            marker = googleMap.addMarker(MarkerOptions().position(LatLng(0.0,0.0)))
            googleMap.isMyLocationEnabled = true
            if(sharedPreferences.getString(Constants.MAP_DESTINATION,"initial")=="favorite") {
                googleMap.setOnMapClickListener { latLng ->
                    updateMarkerPosition(latLng)
                    val geocoder =
                        Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (addresses!!.isNotEmpty()) {
                        val address = addresses[0]
                        val city = address.adminArea
                        if (!city.isNullOrEmpty()) {
                            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                            builder.setMessage("messageFavMapAlert $city ?")
                            builder.setPositiveButton("Save") { dialog, it ->
                                val lat = latLng.latitude
                                val lng = latLng.longitude
                                lifecycleScope.launch {
                                    repository.insertToFavPlacesFromRoom(
                                        FavoriteItem(
                                            lat.toInt() + lng.toInt(),
                                            city,
                                            lat,
                                            lng
                                        )
                                    )
                                }
                                Toast.makeText(
                                    this,
                                    "Location saved: $city",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            builder.setNegativeButton("No") { dialog, which ->
                            }
                            val dialog = builder.create()
                            dialog.show()
                        } else {
                            Toast.makeText(
                                this,
                                "Choose Specific Place",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }else{
                googleMap.setOnMapClickListener { latLng ->
                    updateMarkerPosition(latLng)
                    val geocoder =
                        Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (addresses!!.isNotEmpty()) {
                        val address = addresses[0]
                        val city = address.adminArea
                        if (!city.isNullOrEmpty()) {
                            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                            builder.setMessage("messageInitialMapAlert $city ?")
                            builder.setPositiveButton("Save") { dialog, it ->
                                val lat = latLng.latitude
                                val lng = latLng.longitude
                                editor.putString(Constants.Lat_KEY, lat.toString())
                                editor.putString(Constants.Lon_Key, lng.toString())
                                editor.putString(Constants.LOCATION_NAME,city)
                                editor.commit()
                                Toast.makeText(
                                    this,
                                    "Location saved: $city",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            builder.setNegativeButton("No") { dialog, which ->
                                dialog.dismiss()
                            }
                            val dialog = builder.create()
                            dialog.show()
                        } else {
                            Toast.makeText(
                                this,
                                "Choose Specific Place",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        }

        /*mapView.getMapAsync { googleMap ->
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
        }*/

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