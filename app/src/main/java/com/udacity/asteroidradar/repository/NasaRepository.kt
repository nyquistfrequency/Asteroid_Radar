package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.asDomainModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NasaRepository(private val database: AsteroidDatabase) {

    suspend fun getPicOfDay(): PictureOfDay {
        lateinit var pictureOfDay: PictureOfDay
        withContext(Dispatchers.IO) {
            pictureOfDay = NasaApi.retrofitService.getPicOfTheDay()
        }
        return pictureOfDay
    }

    // Define the relevant dates
    val currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
    val nextWeekDate = LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_DATE)


    // mapping the required filters to "asDomainModel"
    val listOfAsteroidsAll: LiveData<List<Asteroid>> =
        database.asteroidDatabaseDao.getAsteroids().map {
            it.asDomainModel()
        }

    val listOfAsteroidsToday: LiveData<List<Asteroid>> =
        database.asteroidDatabaseDao.getAsteroidsForDay(currentDate).map {
            it.asDomainModel()
        }

    val listOfAsteroidsNextWeek: LiveData<List<Asteroid>> =
        database.asteroidDatabaseDao.getAsteroidsFromDateToDate(currentDate, nextWeekDate).map {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroids = NasaApi.retrofitService.getAsteroids()
                Log.i("succesfully imported", asteroids)
                val parsedAsteroids = parseAsteroidsJsonResult(JSONObject(asteroids))
                Log.i("succesfully parsed", asteroids)
                database.asteroidDatabaseDao.insertAll(*parsedAsteroids.asDatabaseModel())
            } catch (err: Exception) {
                Log.e("Failed refreshing Asteroids", err.message.toString())
            }
        }
    }
}