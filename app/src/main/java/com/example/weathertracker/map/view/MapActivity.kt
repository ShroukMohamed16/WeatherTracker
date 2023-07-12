package com.example.weathertracker.map.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weathertracker.Constants
import com.example.weathertracker.MainActivity
import com.example.weathertracker.R
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.favorite.viewmodel.FavoriteViewModel
import com.example.weathertracker.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weathertracker.map.viewmodel.MapViewModel
import com.example.weathertracker.map.viewmodel.MapViewModelFactory
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

    lateinit var mapView: MapView
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var marker: Marker? = null
    var favoriteItem: FavoriteItem? = null
    lateinit var mapViewModel: MapViewModel
    lateinit var mapViewModelFactory: MapViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        mapViewModelFactory = MapViewModelFactory(Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(this)))
        mapViewModel = ViewModelProvider(this,mapViewModelFactory).get(MapViewModel::class.java)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)

        initMap()
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
        editor.putString(Constants.MAP_DESTINATION, "initial").commit()
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    @SuppressLint("MissingPermission")
    private fun initMap() {
        mapView.getMapAsync { googleMap ->
            marker = googleMap.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)))
            if (sharedPreferences.getString(Constants.MAP_DESTINATION, "initial") == "favorite") {
                googleMap.setOnMapClickListener { latLng ->
                    updateMarkerPosition(latLng)
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (addresses!!.isNotEmpty()) {
                        val address = addresses[0]
                        val city = address.adminArea
                        if (!city.isNullOrEmpty()) {
                            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                            builder.setMessage("$city ?")
                            builder.setPositiveButton(getString(R.string.Save)) { dialog, it ->
                                val lat = latLng.latitude
                                val lng = latLng.longitude
                                mapViewModel.insert(FavoriteItem(lat + lng, city, lat, lng))
                                Toast.makeText(this, "Location saved: $city", Toast.LENGTH_SHORT).show()
                            }
                            builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                            }
                            val dialog = builder.create()
                            dialog.show()
                        } else {
                            Toast.makeText(this, "Choose Specific Place", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                googleMap.setOnMapClickListener { latLng ->
                    updateMarkerPosition(latLng)
                    val geocoder =
                        Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10000)
                    if (addresses!!.isNotEmpty()) {
                        val address = addresses[0]
                        val city = address.adminArea
                        if (!city.isNullOrEmpty()) {
                            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                            builder.setMessage("$city ?")
                            builder.setPositiveButton(getString(R.string.Save)) { dialog, it ->
                                val lat = latLng.latitude
                                val lng = latLng.longitude
                                editor.putString(Constants.Lat_KEY, lat.toString())
                                editor.putString(Constants.Lon_Key, lng.toString())
                                editor.putString(Constants.LOCATION_NAME, city)
                                editor.putBoolean(Constants.isInitializedTag, true)
                                editor.commit()
                                Toast.makeText(this, "Location saved: $city", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                                dialog.dismiss()
                            }
                            val dialog = builder.create()
                            dialog.show()
                        }
                        else {
                            Toast.makeText(this, "Choose Specific Place", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun updateMarkerPosition(latLng: LatLng) {
        marker?.position = latLng
    }

}

