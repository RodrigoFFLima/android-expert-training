package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.FavoriteRepository
import com.example.myapplication.data.UnsplashPhoto
import com.example.myapplication.data.UnsplashRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val unsplashRepository = UnsplashRepository()
    private val favoriteRepository = FavoriteRepository(application)
    
    private var cachedPhotos: List<UnsplashPhoto> = emptyList()

    init {
        fetchPhotos()
        collectFavorites()
    }

    private fun fetchPhotos() {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedPhotos = unsplashRepository.getPhotos(BuildConfig.unsplashApiKey)
                cachedPhotos = fetchedPhotos
                
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(photos = fetchedPhotos)
                } else {
                    _uiState.value = HomeUiState.Success(photos = fetchedPhotos)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    errorMessage = e.localizedMessage ?: "Failed to load photos"
                )
            }
        }
    }
    
    private fun collectFavorites() {
        viewModelScope.launch {
            favoriteRepository.getFavoritePhotos().collectLatest { favoritePhotos ->
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(favoritePhotos = favoritePhotos)
                }
            }
        }
    }
    
    // Toggle favorites filter
    fun toggleFavoritesFilter() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val newShowFavoritesOnly = !currentState.showFavoritesOnly
            _uiState.value = currentState.copy(showFavoritesOnly = newShowFavoritesOnly)
        }
    }
    
    // Toggle favorite status for a photo
    fun toggleFavorite(photo: UnsplashPhoto) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(photo)
        }
    }
    
    // Check if a photo is favorite
    suspend fun isPhotoFavorite(photoId: String): Boolean {
        return favoriteRepository.isPhotoFavorite(photoId)
    }
}