package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class UnsplashSearchResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val id: String,
    val width: Int,
    val height: Int,
    val description: String?,
    val alt_description: String?,
    val urls: UnsplashUrls,
    val user: UnsplashUser
) {
    // Get the best available description
    val bestDescription: String
        get() = description ?: alt_description ?: "Baked goods image"
}

data class UnsplashUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

data class UnsplashUser(
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    val username: String
) {
    val fullName: String
        get() = if (lastName.isNullOrEmpty()) {
            firstName ?: username
        } else {
            "${firstName ?: ""} ${lastName ?: ""}".trim()
        }
}