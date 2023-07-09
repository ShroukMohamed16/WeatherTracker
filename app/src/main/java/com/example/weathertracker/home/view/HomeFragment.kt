package com.example.weathertracker.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weathertracker.ApiState
import com.example.weathertracker.Constants
import com.example.weathertracker.MainActivity
import com.example.weathertracker.R
import com.example.weathertracker.databinding.FragmentHomeBinding
import com.example.weathertracker.databinding.FragmentInitialDialogBinding
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.favorite.HomeRoomState
import com.example.weathertracker.home.viewmodel.HomeViewModel
import com.example.weathertracker.home.viewmodel.HomeViewModelFactory
import com.example.weathertracker.model.Repository
import com.example.weathertracker.model.WeatherEntity
import com.example.weathertracker.network.ApiClient
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var binding:FragmentHomeBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var hourAdapter: HourAdapter
    lateinit var dayAdapter: DayAdapter
    var homeDestination:String? = "initial"
    lateinit var snackbar: Snackbar
    lateinit var weatherEntity:WeatherEntity
    private lateinit var myContext: Context
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var editor: SharedPreferences.Editor
    lateinit var  locationCallback:LocationCallback
    var lat:String?= ""
    var lon:String? = ""
    var lang:String? = ""
    var units:String?= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVariables()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(sharedPreferences.getString(Constants.LOCATION_SOURCE,"map") == "gps"){
            checkGPSPermission()
        }else {
           determineSourceOfDisplayData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.edit().putString(Constants.HOME_DESTINATIO,"initial").commit()
    }
     fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayDataFromApi(){
        homeViewModel.getWeatherFromRetrofit(lat!!.toDouble(), lon!!.toDouble(),units.toString(),lang.toString(),Constants.API_KEY)
        lifecycleScope.launch {
            homeViewModel.weather.collectLatest { result ->
                when (result) {
                    is ApiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.constraint.visibility = View.VISIBLE
                        binding.noPermissionConstraint.visibility = View.GONE
                        binding.homeTemperatureUnit.text = sharedPreferences.getString("unitCharacter","C")
                        binding.homeDay.text = Constants.getDayWithSpecificFormat()
                        hourAdapter = HourAdapter(result.data.timezone)
                        dayAdapter = DayAdapter(lang!!)
                        binding.hourAdapter = hourAdapter
                        hourAdapter.submitList(result.data.hourly)
                        binding.dayAdapter = dayAdapter
                        dayAdapter.submitList(result.data.daily)
                        binding.homeState.text = result.data.current.weather.get(0).description
                        binding.homeTemperature.text = result.data.current.temp.toString()
                        binding.windValue.text = result.data.current.windSpeed.toString()
                        binding.cloudValue.text =result.data.current.clouds.toString()+'%'
                        binding.pressureValue.text = result.data.current.pressure.toString()+" hpa"
                        binding.humidityValue.text = result.data.current.humidity.toString()+'%'
                        binding.ultraVioletValue.text = result.data.current.uvi.toString()
                        binding.visibilityValue.text = result.data.current.visibility.toString()+'m'
                        binding.imageView.setImageResource(
                            Constants.setIcon(
                                result.data.current.weather.get(
                                    0
                                ).icon
                            )
                        )

                        //To Convert Wind Units
                        if(sharedPreferences.getString("wind_units","mile")=="mile"
                            && (sharedPreferences.getString("units","metric")=="metric" ||
                                    sharedPreferences.getString("units","metric")=="standard"))
                        {
                            binding.windValue.text=(convertMpsToMph(result.data.current.windSpeed).toString())
                            binding.windUnit.text = getString(R.string.mile)
                        }else if(sharedPreferences.getString("wind_units","mile")=="meter"
                            && sharedPreferences.getString("units","metric")=="imperial")
                        {
                            binding.windValue.text=(convertMPhToMps(result.data.current.windSpeed).toString())
                            binding.windUnit.text = getString(R.string.m_s)
                        }
                        setLocationNameInShared()
                         weatherEntity = WeatherEntity(result.data.lat,result.data.lon,result.data.timezone
                         ,result.data.timezoneOffset,result.data.current,result.data.hourly,result.data.daily,
                             result.data?.alerts)
                         homeViewModel.insertWeatherDataToRoom(weatherEntity)
                    }
                    is ApiState.Loading -> {
                        binding.constraint.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "Error $result", Toast.LENGTH_LONG)
                    }
                }

            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayDataFromRoom(){
        homeViewModel.getWeatherDataFromRoom()
        lifecycleScope.launch {
            homeViewModel.weatherFromRoom.collect{ result ->
                when (result) {
                    is HomeRoomState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.constraint.visibility = View.VISIBLE
                        binding.noPermissionConstraint.visibility = View.GONE
                        binding.homeTemperatureUnit.text = sharedPreferences.getString("unitCharacter","C")
                        binding.homeDay.text = Constants.getDayWithSpecificFormat()
                        hourAdapter = HourAdapter(result.data.timezone)
                        dayAdapter = DayAdapter(lang!!)
                        binding.hourAdapter = hourAdapter
                        hourAdapter.submitList(result.data.hourly)
                        binding.dayAdapter = dayAdapter
                        dayAdapter.submitList(result.data.daily)
                        binding.homeState.text = result.data.current.weather.get(0).description
                        binding.homeTemperature.text = result.data.current.temp.toString()
                        binding.windValue.text = result.data.current.windSpeed.toString()
                        binding.cloudValue.text =result.data.current.clouds.toString()+'%'
                        binding.pressureValue.text = result.data.current.pressure.toString()+" hpa"
                        binding.humidityValue.text = result.data.current.humidity.toString()+'%'
                        binding.ultraVioletValue.text = result.data.current.uvi.toString()
                        binding.visibilityValue.text = result.data.current.visibility.toString()+'m'
                        binding.imageView.setImageResource(
                            Constants.setIcon(
                                result.data.current.weather.get(
                                    0
                                ).icon
                            )
                        )

                        //To Convert Wind Units
                        if(sharedPreferences.getString("wind_units","mile")=="mile"
                            && (sharedPreferences.getString("units","metric")=="metric" ||
                                    sharedPreferences.getString("units","metric")=="standard"))
                        {
                            binding.windValue.text=(convertMpsToMph(result.data.current.windSpeed).toString())
                            binding.windUnit.text = getString(R.string.mile)
                        }else if(sharedPreferences.getString("wind_units","mile")=="meter"
                            && sharedPreferences.getString("units","metric")=="imperial")
                        {
                            binding.windValue.text=(convertMPhToMps(result.data.current.windSpeed).toString())
                            binding.windUnit.text = getString(R.string.m_s)
                        }
                        setLocationNameInShared()

                    }
                    is HomeRoomState.Loading -> {
                        binding.constraint.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "Error $result", Toast.LENGTH_LONG)
                    }
                }

            }

        }

    }


    private fun setLocationNameInShared(){
        if (homeDestination == "initial") {
            binding.homeLocation.text = sharedPreferences.getString(Constants.LOCATION_NAME, "UnKnown")
        }
        else if (homeDestination == "favorite") {
            binding.homeLocation.text = sharedPreferences.getString(Constants.FAV_LOCATION_NAME, "UnKnown")
        }
    }
    private fun convertMpsToMph(mps: Double): Double {
        return mps * 2.23694
    }
    private fun convertMPhToMps(mph: Double): Double {
        return mph / 2.23694
    }
    private fun initVariables(){
        homeViewModelFactory = HomeViewModelFactory(Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(requireContext())))
        homeViewModel = ViewModelProvider(this,homeViewModelFactory).get(HomeViewModel::class.java)
        sharedPreferences= requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        homeDestination = sharedPreferences.getString(Constants.HOME_DESTINATIO,"initial")
        editor = sharedPreferences.edit()
        Log.i(TAG, "onCreate: $homeDestination")
    }
    private fun determineSourceToHome(){

        if(homeDestination== "initial") {
            lat = sharedPreferences.getString(Constants.Lat_KEY, "33.44")
            lon = sharedPreferences.getString(Constants.Lon_Key, "94.04")
            lang= sharedPreferences.getString("lang","en")
            units= sharedPreferences.getString("units","metric")
        }else{
            lat = sharedPreferences.getString(Constants.FAV_Lat_KEY, "33.44")
            lon = sharedPreferences.getString(Constants.FAV_Lon_Key, "94.04")
            lang= sharedPreferences.getString("lang","en")
            units= sharedPreferences.getString("units","metric")
            requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.MAP_DESTINATION,"initial")
                .commit()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkGPSPermission(){
        if(!sharedPreferences.getBoolean(Constants.PERMISSIONS_IS_ENABLED,false)){
            binding.noPermissionConstraint.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            binding.allowPermissionPoint.setOnClickListener {
                if(isNetworkAvailable())
                {
                    getLastLocation()
                }else{
                    Constants.showNoNetworkDialog(myContext)
                }
            }
        }else{
          determineSourceOfDisplayData()
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
                determineSourceToHome()
                displayDataFromApi()
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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun determineSourceOfDisplayData(){
        if (isNetworkAvailable()) {
            determineSourceToHome()
            displayDataFromApi()
        } else {
            displayDataFromRoom()
        }
    }


}