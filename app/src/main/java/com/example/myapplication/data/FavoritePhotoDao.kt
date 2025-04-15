package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePhotoDao {
    @Query("SELECT * FROM favorite_photos ORDER BY timestamp DESC")
    fun getFavoritePhotos(): Flow<List<FavoritePhoto>>
    
    @Query("SELECT * FROM favorite_photos WHERE photoId = :photoId")
    suspend fun getFavoritePhotoById(photoId: String): FavoritePhoto?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritePhoto(favoritePhoto: FavoritePhoto)
    
    @Delete
    suspend fun deleteFavoritePhoto(favoritePhoto: FavoritePhoto)
    
    @Query("DELETE FROM favorite_photos WHERE photoId = :photoId")
    suspend fun deleteFavoritePhotoById(photoId: String)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_photos WHERE photoId = :photoId)")
    suspend fun isPhotoFavorite(photoId: String): Boolean
}