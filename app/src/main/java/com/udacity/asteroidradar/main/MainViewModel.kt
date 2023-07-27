package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.NasaRepository
import kotlinx.coroutines.launch

enum class FilterAsteroids { TODAY, WEEK, ALL }

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AsteroidDatabase.getInstance(application)
    private val repository = NasaRepository(database)

    private val _picOfTheDay = MutableLiveData<PictureOfDay>()
    val picOfTheDay: LiveData<PictureOfDay>
        get() = _picOfTheDay

    private val _navigateToDetailFragment = MutableLiveData<Asteroid?>()
    val navigateToDetailFragment: LiveData<Asteroid?>
        get() = _navigateToDetailFragment

    private val activeFilter = MutableLiveData(FilterAsteroids.ALL)

    // I googled this solution since I was stuck here for quite a while
    val listOfAsteroids = activeFilter.switchMap {
        when (it) {
            FilterAsteroids.WEEK -> repository.listOfAsteroidsNextWeek
            FilterAsteroids.TODAY -> repository.listOfAsteroidsToday
            else -> repository.listOfAsteroidsAll
        }
    }

//    // for Testing the RecyclerView with init values only without utilizing the mapping as learned in the DevBye lesson
//    private val _listOfAsteroids = MutableLiveData<Asteroid>
//    val listOfAsteroids: LiveData<Asteroid>
//        get() = _listOfAsteroids


    init {
        viewModelScope.launch {
            repository.refreshAsteroids()
            getPicOfTheDay()
        }
//       //For Testing Only
//        _listOfAsteroids.value = mutableListOf(
//            Asteroid(
//                id = 1,
//                codename = "a",
//                closeApproachDate = "01.01.2023",
//                absoluteMagnitude = 1.0,
//                estimatedDiameter = 1.0,
//                relativeVelocity = 1.0,
//                distanceFromEarth = 1.0,
//                isPotentiallyHazardous = true
//            ),
//            Asteroid(
//                id = 2,
//                codename = "b",
//                closeApproachDate = "01.02.2023",
//                absoluteMagnitude = 2.0,
//                estimatedDiameter = 2.0,
//                relativeVelocity = 2.0,
//                distanceFromEarth = 2.0,
//                isPotentiallyHazardous = false
//            ),
//            Asteroid(
//                id = 3,
//                codename = "c",
//                closeApproachDate = "01.03.2023",
//                absoluteMagnitude = 3.0,
//                estimatedDiameter = 3.0,
//                relativeVelocity = 3.0,
//                distanceFromEarth = 3.0,
//                isPotentiallyHazardous = true
//            ),
//            Asteroid(
//                id = 4,
//                codename = "d",
//                closeApproachDate = "01.04.2023",
//                absoluteMagnitude = 4.0,
//                estimatedDiameter = 4.0,
//                relativeVelocity = 4.0,
//                distanceFromEarth = 4.0,
//                isPotentiallyHazardous = false
//            ),
//            Asteroid(
//                id = 5,
//                codename = "a",
//                closeApproachDate = "01.01.2023",
//                absoluteMagnitude = 1.0,
//                estimatedDiameter = 1.0,
//                relativeVelocity = 1.0,
//                distanceFromEarth = 1.0,
//                isPotentiallyHazardous = true
//            ),
//            Asteroid(
//                id = 6,
//                codename = "b",
//                closeApproachDate = "01.02.2023",
//                absoluteMagnitude = 2.0,
//                estimatedDiameter = 2.0,
//                relativeVelocity = 2.0,
//                distanceFromEarth = 2.0,
//                isPotentiallyHazardous = false
//            ),
//            Asteroid(
//                id = 7,
//                codename = "c",
//                closeApproachDate = "01.03.2023",
//                absoluteMagnitude = 3.0,
//                estimatedDiameter = 3.0,
//                relativeVelocity = 3.0,
//                distanceFromEarth = 3.0,
//                isPotentiallyHazardous = true
//            ),
//            Asteroid(
//                id = 8,
//                codename = "d",
//                closeApproachDate = "01.04.2023",
//                absoluteMagnitude = 4.0,
//                estimatedDiameter = 4.0,
//                relativeVelocity = 4.0,
//                distanceFromEarth = 4.0,
//                isPotentiallyHazardous = false
//            )
//        )

    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetailFragment.value = asteroid
    }

    fun doneNavigating() {
        _navigateToDetailFragment.value = null
    }

    fun onFilterChangeClicked(filterAsteroids: FilterAsteroids) {
        activeFilter.postValue(filterAsteroids)
    }

    private fun getPicOfTheDay() {
        viewModelScope.launch {
            try {
                _picOfTheDay.value = repository.getPicOfDay()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }
}