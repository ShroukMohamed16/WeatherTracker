package com.example.weathertracker.map.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathertracker.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class MapViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var mapViewModel: MapViewModel
    lateinit var repository: FakeRepository

    val favoriteItem1 = FavoriteItem(1.1,"Cairo",1.1,1.1)
    val favoriteItem2 = FavoriteItem(2.2,"Alex",2.2,2.2)
    val favoriteItem3 = FavoriteItem(3.3,"Sa7el",3.3,3.3)
    val favList = mutableListOf<FavoriteItem>(favoriteItem1,favoriteItem2,favoriteItem3)

    val alarm1= Alarm(11111,11111,111111,1111111)
    val alarm2= Alarm(22222,22222,222222,2222222)
    val alarm3= Alarm(33333,33333,333333,3333333)
    val alarmList = mutableListOf<Alarm>(alarm1,alarm2,alarm3)

    val weatherEntity = WeatherEntity( 13.4,14.5,"",0, CurrentWeather(1111,22222,3333
        ,12.4, FeelsLike(12.4,12.1,45.5,78.9),0,1,2.2,4.3,1,7,7.1
        ,5, emptyList()), emptyList(), emptyList(), emptyList()
    )
    val weatherList = mutableListOf<WeatherEntity>(weatherEntity)

    val myResponse = MyResponse(1.1,2.2,"cairo",1, CurrentWeather(1111,22222,3333
        ,12.4, FeelsLike(12.4,12.1,45.5,78.9),0,1,2.2,4.3,1,7,7.1
        ,5, emptyList()), emptyList(), emptyList(), emptyList(), emptyList())




    @Before
    fun setUp() {
        repository = FakeRepository(favList,alarmList,weatherList,myResponse)
        mapViewModel = MapViewModel(repository)
    }

    @Test
    fun insert_insertNewFavoritePlace() = runBlockingTest {
        val favoriteItem4 = FavoriteItem(1.8, "qena", 2.7, 9.7)
        mapViewModel.insert(favoriteItem4)
        repository.getFavPlacesFromRoom().collect {
            assertTrue(it.contains(favoriteItem4))
        }
    }

}