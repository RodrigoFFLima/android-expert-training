package com.example.myapplication

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

@SuppressLint("StaticFieldLeak")
class BakingViewModel(
    private val context: Context
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-flash",
        apiKey = BuildConfig.apiKey
    )

    private fun loadData() {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Then fetch photos from unsplash
                val fetchedPhotos = unsplashRepository.getPhotos(BuildConfig.unsplashApiKey)
                cachedPhotos = fetchedPhotos
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value =
                        currentState.copy(photos = fetchedPhotos)
                } else {
                    _uiState.value =
                        HomeUiState.Success(photos = fetchedPhotos)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    errorMessage = e.localizedMessage ?: "Failed to load photos"
                )
                    .text
            }
                .takeIf { it.isSuccess }
                ?.let { response ->
                    _uiState.value = UiState.Success(response.getOrDefault("")!!)
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
}