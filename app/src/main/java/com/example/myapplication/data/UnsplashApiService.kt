package com.example.myapplication.data

import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApiService {
    @GET("search/photos")
    suspend fun getPhotos(
        @Query("client_id") clientId: String,
        @Query("query") query: String = "baking pastries food desserts",
        @Query("per_page") perPage: Int = 10,
        @Query("orientation") orientation: String = "landscape"
    ): UnsplashSearchResponse
}