package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Introducing the coroutine functions for fetching the picture of the day & asteroids
interface NasaApiService {

    // Service to fetch the picture of the day
    @GET("planetary/apod")
    suspend fun getPicOfTheDay(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): PictureOfDay

    // Service to fetch the Asteroids - tbd if it needs to be reworked
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): String

}

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit =
    Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi)).baseUrl(BASE_URL).client(okHttpClient)
        .build()

object NasaApi {
    val retrofitService: NasaApiService by lazy { retrofit.create(NasaApiService::class.java) }
}

