package com.petracackov.catapp.data

import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val baseUrl = "https://api.thecatapi.com/"
private const val apiKey = "live_ZPaLkp80ubDZncywd3UWnHC8VvnKyeZqxxH58paRnA4ZsBDoScedjGIaWkeo0eNI"
private val client = OkHttpClient.Builder().addInterceptor(HeaderInterceptor()).build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(baseUrl)
    .client(client)
    .build()

private class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", "$apiKey")
                .build()
        )
    }
}

object CatApi {
    val retrofitService: CatApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }
}

interface CatApiService {
    @GET(SEARCH_ENDPOINT)
    suspend fun getRandomCat(): List<CatModel?>?

    @POST(LIKE_ENDPOINT)
    suspend fun likeCat(@Body raw: JsonObject): LikeResponse

    companion object {
        const val SEARCH_ENDPOINT = "v1/images/search"
        const val LIKE_ENDPOINT = "v1/votes"
    }
}

