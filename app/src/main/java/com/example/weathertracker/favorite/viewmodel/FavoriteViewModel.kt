package com.example.weathertracker.favorite.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertracker.RoomState
import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel(val repositoryInterface: RepositoryInterface):ViewModel() {

    private val mutableFavPlaces: MutableStateFlow<RoomState> = MutableStateFlow(RoomState.Loading)
    val favPlacesList : StateFlow<RoomState> = mutableFavPlaces
    init{
        getAllFavPlaces()
    }
    fun getAllFavPlaces(){
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.getFavPlacesFromRoom()
                ?.catch { e->
                        mutableFavPlaces.value=RoomState.Failure(e)
                }
                ?.collect{ favPlace->
                        mutableFavPlaces.value = RoomState.Success(favPlace)
                }
        }
    }
    fun delete(favoriteItem: FavoriteItem){
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.deleteFromFavPlacesFromRoom(favoriteItem)
            getAllFavPlaces()
        }
    }
}