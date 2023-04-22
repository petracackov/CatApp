package com.petracackov.catapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

private const val baseUrl = "https://api.thecatapi.com/"
private const val apiKey = "live_ZPaLkp80ubDZncywd3UWnHC8VvnKyeZqxxH58paRnA4ZsBDoScedjGIaWkeo0eNI"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(baseUrl)
    .build()

interface CatApiService {

    @Headers(
        "Content-Type: application/json",
        "x-api-key: $apiKey"
    )

    @GET("v1/images/search")
    suspend fun getRandomCat(): List<CatModel?>?
}

object CatApi {
    val retrofitService: CatApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }
}

