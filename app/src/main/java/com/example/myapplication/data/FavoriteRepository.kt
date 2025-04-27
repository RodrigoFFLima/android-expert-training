package com.example.myapplication.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepository(context: Context) {
    private val favoritePhotoDao = AppDatabase.getDatabase(context).favoritePhotoDao()
    
    // Get all favorite photos as a Flow of UnsplashPhoto objects for UI consistency
    fun getFavoritePhotos(): Flow<List<UnsplashPhoto>> {
        return favoritePhotoDao.getFavoritePhotos().map { favorites ->
            favorites.map { it.toUnsplashPhoto() }
        }
    }
    
    // Check if a photo is marked as favorite
    suspend fun isPhotoFavorite(photoId: String): Boolean {
        return favoritePhotoDao.isPhotoFavorite(photoId)
    }
    
    // Add a photo to favorites
    suspend fun addToFavorites(photo: UnsplashPhoto) {
        favoritePhotoDao.insertFavoritePhoto(photo.toFavoritePhoto())
    }
    
    // Remove a photo from favorites
    suspend fun removeFromFavorites(photoId: String) {
        favoritePhotoDao.deleteFavoritePhotoById(photoId)
    }
    
    // Toggle favorite status
    suspend fun toggleFavorite(photo: UnsplashPhoto): Boolean {
        val isFavorite = isPhotoFavorite(photo.id)
        if (isFavorite) {
            removeFromFavorites(photo.id)
        } else {
            addToFavorites(photo)
        }
        return !isFavorite
    }
}