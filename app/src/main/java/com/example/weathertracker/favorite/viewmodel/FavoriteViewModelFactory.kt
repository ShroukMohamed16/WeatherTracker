package com.example.weathertracker.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathertracker.home.viewmodel.HomeViewModel
import com.example.weathertracker.model.RepositoryInterface

class FavoriteViewModelFactory(private val repositoryInterface: RepositoryInterface):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            FavoriteViewModel(repositoryInterface) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}