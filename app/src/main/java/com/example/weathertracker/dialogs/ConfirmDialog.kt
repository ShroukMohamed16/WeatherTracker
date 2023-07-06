package com.example.weathertracker.dialogs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.weathertracker.Constants
import com.example.weathertracker.MainActivity
import com.example.weathertracker.MapActivity
import com.example.weathertracker.R
import com.example.weathertracker.databinding.ConfirmDialogBinding
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.Repository
import com.example.weathertracker.network.ApiClient
import kotlinx.coroutines.launch
import java.util.*

class ConfirmDialog :DialogFragment(){

    lateinit var binding: ConfirmDialogBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var repository: Repository
    var favoriteItem:FavoriteItem = FavoriteItem(0,"",0.0,0.0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        repository = Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(requireContext()))


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.confirm_dialog,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(sharedPreferences.getString(Constants.MAP_DESTINATION,"initial")=="initial") {
            val lat = sharedPreferences.getString(Constants.Lat_KEY, "0.0")
            val lon = sharedPreferences.getString(Constants.Lon_Key, "0.0")
            val geo = Geocoder(requireActivity(), Locale.getDefault())
            val address = geo.getFromLocation(lat!!.toDouble(), lon!!.toDouble(), 1)
            val editor = sharedPreferences.edit()
            editor.putString(Constants.LOCATION_NAME, address!![0].featureName)
            editor.commit()
            binding.locationConfirmDialog.text = address!![0].featureName
            binding.addressConfirmDialog.text = address!![0].getAddressLine(0)
        }else{
            val lat = sharedPreferences.getString(Constants.FAV_Lat_KEY, "0.0")
            val lon = sharedPreferences.getString(Constants.FAV_Lon_Key, "0.0")
           val geo = Geocoder(requireActivity(), Locale.getDefault())
            val address = geo.getFromLocation(lat!!.toDouble(), lon!!.toDouble(), 1)
            val editor = sharedPreferences.edit()
            editor.putString(Constants.FAV_LOCATION_NAME, address!![0].featureName)
            editor.commit()
            favoriteItem.id = lat.toInt()+lon.toInt()
            favoriteItem.lat = lat.toDouble()
            favoriteItem.lon = lon.toDouble()
            favoriteItem.cityName=address!![0].featureName
            binding.locationConfirmDialog.text = address!![0].featureName
            binding.addressConfirmDialog.text = address!![0].getAddressLine(0)
        }

        binding.saveBtnConfirmDialog.setOnClickListener{
            if(sharedPreferences.getString(Constants.MAP_DESTINATION,"initial")=="initial") {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                dismiss()
            }else{
                lifecycleScope.launch {
                    repository.localSource.insertToFavPlaces(favoriteItem)
                }
                requireActivity().finish()
                dismiss()
            }
        }
    }

}