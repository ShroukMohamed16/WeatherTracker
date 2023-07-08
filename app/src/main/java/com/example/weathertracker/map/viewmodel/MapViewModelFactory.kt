package com.example.weathertracker.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathertracker.model.RepositoryInterface

class MapViewModelFactory(private val repositoryInterface: RepositoryInterface):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            MapViewModel(repositoryInterface) as T
        }else{
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}