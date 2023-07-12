package com.example.weathertracker.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathertracker.Constants
import com.example.weathertracker.db.FakeLocalDataSource
import com.example.weathertracker.network.FakeRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var fakeLocalDataSource: FakeLocalDataSource
    lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    lateinit var repository: Repository

    val favoriteItem1 = FavoriteItem(1.1,"Cairo",1.1,1.1)
    val favoriteItem2 = FavoriteItem(2.2,"Alex",2.2,2.2)
    val favoriteItem3 = FavoriteItem(3.3,"Sa7el",3.3,3.3)
    val favList = mutableListOf<FavoriteItem>(favoriteItem1,favoriteItem2,favoriteItem3)

    val alarm1=Alarm(11111,11111,111111,1111111)
    val alarm2=Alarm(22222,22222,222222,2222222)
    val alarm3=Alarm(33333,33333,333333,3333333)
    val alarmList = mutableListOf<Alarm>(alarm1,alarm2,alarm3)

    val weatherEntity =WeatherEntity( 13.4,14.5,"",0, CurrentWeather(1111,22222,3333
    ,12.4, FeelsLike(12.4,12.1,45.5,78.9),0,1,2.2,4.3,1,7,7.1
    ,5, emptyList()), emptyList(), emptyList(), emptyList()
    )
    val weatherList = mutableListOf<WeatherEntity>(weatherEntity)

    val myResponse = MyResponse(1.1,2.2,"cairo",1,CurrentWeather(1111,22222,3333
        ,12.4, FeelsLike(12.4,12.1,45.5,78.9),0,1,2.2,4.3,1,7,7.1
        ,5, emptyList()), emptyList(), emptyList(), emptyList(), emptyList())



    @Before
    fun setUp() {
        fakeLocalDataSource = FakeLocalDataSource(favList,alarmList,weatherList)
        fakeRemoteDataSource = FakeRemoteDataSource(myResponse)
        repository = Repository.getInstance(fakeRemoteDataSource,fakeLocalDataSource)
    }

    @Test
    fun getWeather_getWeatherInformationFromRetrofit_returnFlowOfMyResponse(){
        runBlockingTest {
            repository.getWeather(1.1,2.2,"standard","en",Constants.API_KEY)?.collect{
                assertThat(it,IsEqual(myResponse))
            }
        }
    }

    @Test
    fun getFavPlaces_returnFavPlacesListFromRoom(){
        runBlockingTest {
            repository.getFavPlacesFromRoom().collect{
                assertThat(it,IsEqual(favList))
            }
        }
    }
    @Test
    fun getWeatherFromRoom_returnWeatherEntityDetails(){
        runBlockingTest {
            repository.getWeatherFromRoom()?.collect{
                assertThat(it,IsEqual(weatherEntity))
            }
        }
    }
    @Test
    fun getAlarmsFromRoom_returnFlowAlarmsListFromRoom(){
        runBlockingTest {
            repository.getAlarmsFromRoom().collect{
                assertThat(it,IsEqual(alarmList))
            }
        }
    }

    @Test
    fun insertToFavPlacesFromRoom_insertNewFavoriteItemToRoom() = runBlockingTest {
            val favoriteItem4 = FavoriteItem(1.1,"Sohag",2.7,9.7)
            repository.insertToFavPlacesFromRoom(favoriteItem4)
            repository.getFavPlacesFromRoom().collect {
                val fav = it.last()
                assertThat(fav,IsEqual(favoriteItem4))
            }
    }

    @Test
    fun insertAlarmsFromRoom_insertNewAlarmToRoom() = runBlockingTest {
        val alarm4 = Alarm(17887,485645,489754,488456)
        repository.insertAlarmToRoom(alarm4)
        repository.getAlarmsFromRoom().collect {
            val alarms = it.last()
            assertThat(alarms,IsEqual(alarm4))
        }
    }
    @Test
     fun insertWeatherEntityToRoom_insertNewWeatherToRoom()= runBlockingTest {
        val weatherEntity = WeatherEntity(
            13.4, 14.5, "", 0, CurrentWeather(1111, 22222, 3333, 12.4,
                FeelsLike(12.4, 12.1, 45.5, 78.9), 0, 1, 2.2, 4.3, 1, 7, 7.1, 5,
                emptyList()
            ), emptyList(), emptyList(), emptyList()
        )
        repository.insertWeatherToRoom(weatherEntity)
        repository.getWeatherFromRoom()?.collect() {
            assertThat(it, IsEqual(weatherEntity))
        }
    }

    @Test
    fun deleteFavPlaceFromRoom_FavItem()= runBlockingTest {
        val favoriteItem4 = FavoriteItem(1.1,"Sohag",2.7,9.7)
        repository.deleteFromFavPlacesFromRoom(favoriteItem4)
        repository.getFavPlacesFromRoom().collect{
            assertFalse(it.contains(favoriteItem4))
        }
    }

    @Test
    fun deleteAlarmFromRoom_Alarm()= runBlockingTest {
        val alarm4 = Alarm(17887,485645,489754,488456)
        repository.deleteAlarmFromRoom(alarm4.startTime,alarm4.endTime)
        repository.getAlarmsFromRoom().collect {
            assertFalse(it.contains(alarm4))
        }
    }




}