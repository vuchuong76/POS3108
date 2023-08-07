package com.example.test.Utilites

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilites {
    private var retrofit: Retrofit?=null
    var BASE_URL = "http://api.weatherapi.com/v1/"
    fun getApiInterface() : ApiInterFace? {
        if(retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        }

        return  retrofit?.create(ApiInterFace::class.java)
    }
}