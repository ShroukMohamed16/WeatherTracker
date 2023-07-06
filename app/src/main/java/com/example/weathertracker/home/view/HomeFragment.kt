package com.example.weathertracker.home.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var binding:FragmentHomeBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var hourAdapter: HourAdapter
    lateinit var dayAdapter: DayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModelFactory = HomeViewModelFactory(Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(requireContext())))
        homeViewModel = ViewModelProvider(this,homeViewModelFactory).get(HomeViewModel::class.java)
        sharedPreferences= requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val lat = sharedPreferences.getString(Constants.Lat_KEY,"33.44")
        val lon = sharedPreferences.getString(Constants.Lon_Key,"94.04")
        homeViewModel.getWeatherFromRetrofit(lat!!.toDouble(), lon!!.toDouble(),"metric","eaa7758b00a4ea8b138646fe349149d1")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        binding.lifecycleOwner = this
        hourAdapter = HourAdapter()
        dayAdapter = DayAdapter()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch{
            homeViewModel.weather.collectLatest{ result ->
                when(result) {
                    is ApiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.constraint.visibility = View.VISIBLE
                        binding.homeDay.text = Constants.getDayWithSpecificFormat(result.data.current.dt)
                        binding.homeLocation.text = sharedPreferences.getString(Constants.LOCATION_NAME,"UnKnown")
                        binding.myResponse = result.data
                        binding.imageView.setImageResource(Constants.setIcon(result.data.current.weather.get(0).icon))
                        binding.hourAdapter = hourAdapter
                        hourAdapter.submitList(result.data.hourly)
                        binding.dayAdapter = dayAdapter
                        dayAdapter.submitList(result.data.daily)

                    }
                    is ApiState.Loading -> {
                        binding.constraint.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    else ->{
                        binding.progressBar.visibility = View.VISIBLE
                        Toast.makeText(requireContext(),"Error $result", Toast.LENGTH_LONG)
                    }
                }

            }

        }

    }

}