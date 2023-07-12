package com.example.weathertracker.alarm.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.weathertracker.alarm.AlarmRoomState
import com.example.weathertracker.favorite.HomeRoomState
import com.example.weathertracker.favorite.RoomState
import com.example.weathertracker.favorite.viewmodel.FavoriteViewModel
import com.example.weathertracker.map.viewmodel.FakeRepository
import com.example.weathertracker.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlarmViewModelTest{
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()
    lateinit var repository: FakeRepository
    lateinit var alarmViewModel: AlarmViewModel

    val favoriteItem1 = FavoriteItem(1.1,"Cairo",1.1,1.1)
    val favoriteItem2 = FavoriteItem(2.2,"Alex",2.2,2.2)
    val favoriteItem3 = FavoriteItem(3.3,"Sa7el",3.3,3.3)
    val favList = mutableListOf<FavoriteItem>(favoriteItem1,favoriteItem2,favoriteItem3)

    val alarm1= Alarm(11111,11111,111111,1111111)
    val alarm2= Alarm(22222,22222,222222,2222222)
    val alarmList = mutableListOf<Alarm>(alarm1,alarm2)

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
        alarmViewModel = AlarmViewModel(repository)
    }

    @Test
    fun getAllAlarms_getAlarmsFromRoom() = runBlockingTest{
        var value = listOf<Alarm>()
        alarmViewModel.getAllAlarms()
        alarmViewModel.alarmsList.test {
            this.awaitItem().apply {
                when(this){
                    is AlarmRoomState.Success -> { value = this.data}
                    else->{}
                }
                assertEquals(2,value.size)
            }
        }

    }
    @Test
    fun deleteAlarm_deleteAlarmFromRoom() = runBlockingTest{
        alarmViewModel.deleteAlarm(alarm2.startTime,alarm2.endTime)
        repository.getAlarmsFromRoom().collect{
            assertFalse(it.contains(alarm2))
        }

    }
    @Test
    fun insertAlarm_insertNewAlarmToRoom() = runBlockingTest {
        val alarm3 = Alarm(445454,787845,78975,54545)
        alarmViewModel.insertAlarm(alarm3)
        repository.getAlarmsFromRoom().collect {
            assertTrue(it.contains(alarm3))
        }
    }

    @Test
    fun getWeatherAlarm_getWeatherDetails() = runBlockingTest{
        var value :WeatherEntity? = null
        alarmViewModel.getWeatherAlarms()
        alarmViewModel.weatherAlarmsList.test {
            this.awaitItem().apply {
                when(this){
                    is HomeRoomState.Success -> { value = this.data}
                    else->{}
                }
                assertThat(value,not(nullValue()))
            }
        }

    }


}
