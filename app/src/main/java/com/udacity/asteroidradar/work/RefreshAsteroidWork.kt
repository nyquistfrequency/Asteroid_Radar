package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase.Companion.getInstance
import com.udacity.asteroidradar.repository.NasaRepository
import retrofit2.HttpException

class RefreshAsteroidWork(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

        companion object {
            const val WORK_NAME = "RefreshAsteroidWork"
        }

    override suspend fun doWork(): Result {
        val database = getInstance(applicationContext)
        val repository = NasaRepository(database)

        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch(e: HttpException) {
            Result.retry()
        }
    }
}