package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDatabaseDao {

    @Query("SELECT * FROM asteroid_table ORDER BY closeApproachDate DESC")
    fun getAsteroids(): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: Asteroid)

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate =:day ORDER BY closeApproachDate DESC")
    fun getAsteroidsForDay(day: String): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate DESC")
    fun getAsteroidsFromDateToDate(startDate: String, endDate: String): LiveData<List<Asteroid>>

}