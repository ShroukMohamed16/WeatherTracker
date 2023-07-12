package com.example.weathertracker.favorite.viewmodel

import android.util.Size
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.weathertracker.favorite.RoomState
import com.example.weathertracker.map.viewmodel.FakeRepository
import com.example.weathertracker.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.*
import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()
    lateinit var repository: FakeRepository
    lateinit var favoriteViewModel: FavoriteViewModel

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
   fun setUp(){
       repository = FakeRepository(favList,alarmList,weatherList,myResponse)
       favoriteViewModel = FavoriteViewModel(repository)
   }
    @Test
    fun getAllFavPlaces_getFavPlacesFromRoom() = runBlockingTest{
        var value = listOf<FavoriteItem>()
        favoriteViewModel.getAllFavPlaces()
        favoriteViewModel.favPlacesList.test {
            this.awaitItem().apply {
                when(this){
                    is RoomState.Success -> { value = this.data}
                    else->{}
                }
                assertEquals(3,value.size)
            }
        }

    }
    @Test
    fun deleteFavPlace() = runBlockingTest{
        favoriteViewModel.delete(favoriteItem3)
        repository.getFavPlacesFromRoom().collect{
            assertFalse(it.contains(favoriteItem3))
        }

    }

}