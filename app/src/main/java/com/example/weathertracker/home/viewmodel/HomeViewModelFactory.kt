package com.example.weathertracker.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathertracker.model.RepositoryInterface

class HomeViewModelFactory(private val repositoryInterface: RepositoryInterface):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(repositoryInterface) as T
        }else{
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}