package com.example.weathertracker.home.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weathertracker.ApiState
import com.example.weathertracker.Constants
import com.example.weathertracker.R
import com.example.weathertracker.databinding.FragmentHomeBinding
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.home.viewmodel.HomeViewModel
import com.example.weathertracker.home.viewmodel.HomeViewModelFactory
import com.example.weathertracker.model.Repository
import com.example.weathertracker.network.ApiClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModelFactory = HomeViewModelFactory(Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(requireContext())))
        homeViewModel = ViewModelProvider(this,homeViewModelFactory).get(HomeViewModel::class.java)
        sharedPreferences= requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        homeDestination = sharedPreferences.getString(Constants.HOME_DESTINATIO,"initial")
        Log.i(TAG, "onCreate: $homeDestination")

    }

    override fun onResume() {
        super.onResume()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        binding.lifecycleOwner = this
        hourAdapter = HourAdapter()
        dayAdapter = DayAdapter()
        if(homeDestination== "initial") {
            val lat = sharedPreferences.getString(Constants.Lat_KEY, "33.44")
            val lon = sharedPreferences.getString(Constants.Lon_Key, "94.04")
            val lang= sharedPreferences.getString("lang","en")
            val units= sharedPreferences.getString("units","metric")
            homeViewModel.getWeatherFromRetrofit(lat!!.toDouble(), lon!!.toDouble(),units.toString(),lang.toString(),Constants.API_KEY)

        }else{
            val lat = sharedPreferences.getString(Constants.FAV_Lat_KEY, "33.44")
            val lon = sharedPreferences.getString(Constants.FAV_Lon_Key, "94.04")
            val lang= sharedPreferences.getString("lang","en")
            val units= sharedPreferences.getString("units","metric")

            homeViewModel.getWeatherFromRetrofit(lat!!.toDouble(), lon!!.toDouble(),units.toString(),lang.toString(),Constants.API_KEY)
            requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.MAP_DESTINATION,"initial")
                .commit()

        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     //   snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG)

        if(isNetworkAvailable()) {
            lifecycleScope.launch {
                homeViewModel.weather.collectLatest { result ->
                    when (result) {
                        is ApiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.constraint.visibility = View.VISIBLE
                            binding.homeTemperatureUnit.text = sharedPreferences.getString("unitCharacter","C")
                            binding.homeDay.text =
                                Constants.getDayWithSpecificFormat()
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
                            if (homeDestination == "initial") {
                                binding.homeLocation.text =
                                    sharedPreferences.getString(Constants.LOCATION_NAME, "UnKnown")
                            } else if (homeDestination == "favorite") {
                                binding.homeLocation.text =
                                    sharedPreferences.getString(
                                        Constants.FAV_LOCATION_NAME,
                                        "UnKnown"
                                    )
                            }
                            binding.myResponse = result.data
                            binding.imageView.setImageResource(
                                Constants.setIcon(
                                    result.data.current.weather.get(
                                        0
                                    ).icon
                                )
                            )
                            binding.hourAdapter = hourAdapter
                            hourAdapter.submitList(result.data.hourly)
                            binding.dayAdapter = dayAdapter
                            dayAdapter.submitList(result.data.daily)

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
        }/*else{
            snackbar.setAction("Dismiss") {
                snackbar.dismiss()
            }
            snackbar.show()
        }*/

    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.edit().putString(Constants.HOME_DESTINATIO,"initial").commit()
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    fun convertMpsToMph(mps: Double): Double {
        return mps * 2.23694
    }
    fun convertMPhToMps(mph: Double): Double {
        return mph / 2.23694
    }

}