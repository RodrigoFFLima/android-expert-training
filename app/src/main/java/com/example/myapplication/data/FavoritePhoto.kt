package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_photos")
data class FavoritePhoto(
    @PrimaryKey
    val photoId: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val description: String,
    val userName: String,
    val userUsername: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Convert to UnsplashPhoto (for UI consistency)
    fun toUnsplashPhoto(): UnsplashPhoto {
        return UnsplashPhoto(
            id = photoId,
            width = 0, // Not used in UI
            height = 0, // Not used in UI
            description = description,
            alt_description = null,
            urls = UnsplashUrls(
                raw = imageUrl,
                full = imageUrl,
                regular = imageUrl,
                small = thumbnailUrl,
                thumb = thumbnailUrl
            ),
            user = UnsplashUser(
                firstName = userName,
                lastName = null,
                username = userUsername
            )
        )
    }
}