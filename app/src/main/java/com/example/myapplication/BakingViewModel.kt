package com.example.myapplication

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.UnsplashRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val unsplashRepository = UnsplashRepository()

    init {
        fetchPhotos()
    }

    private fun fetchPhotos() {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedPhotos = unsplashRepository.getPhotos(BuildConfig.unsplashApiKey)
                _uiState.value = HomeUiState.Success(photos = fetchedPhotos)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    errorMessage = e.localizedMessage ?: "Failed to load photos"
                )
            }
        }
    }
}
