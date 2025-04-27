package com.example.myapplication.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UnsplashRepository {
    private val apiService: UnsplashApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(UnsplashApiService::class.java)
    }

    suspend fun getPhotos(
        clientId: String, 
        query: String = "baking pastries food desserts", 
        perPage: Int = 10
    ): List<UnsplashPhoto> {
        val response = apiService.getPhotos(
            clientId = clientId,
            query = query,
            perPage = perPage
        )
        return response.results
    }
}