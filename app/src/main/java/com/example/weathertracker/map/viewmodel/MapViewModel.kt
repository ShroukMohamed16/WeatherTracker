package com.example.weathertracker.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"
class MapViewModel(private val repositoryInterface: RepositoryInterface):ViewModel() {

    fun insert(favoriteItem: FavoriteItem){
        viewModelScope.launch(Dispatchers.IO){
            repositoryInterface.insertToFavPlacesFromRoom(favoriteItem)
        }
    }

}