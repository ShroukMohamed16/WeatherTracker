package com.example.weathertracker.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathertracker.model.RepositoryInterface

class AlarmViewModelFactory(private val repositoryInterface: RepositoryInterface):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlarmViewModel::class.java)){
            AlarmViewModel(repositoryInterface) as T
        }else{
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}