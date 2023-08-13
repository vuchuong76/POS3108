package com.example.test.Utilites

import com.example.pos1.weather.CurrentWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterFace {
    @GET("current.json")
    fun getCurrentFuture(
        @Query("key") key:String,
        @Query("q") q:String,
        @Query("days") days:String,
        @Query("dt") dt:String
    ): Call<CurrentWeather>


}