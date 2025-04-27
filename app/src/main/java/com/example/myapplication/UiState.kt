package com.example.myapplication

import com.example.myapplication.data.UnsplashPhoto

/**
 * Top-level UI state for the home screen
 */
sealed interface HomeUiState {
    /**
     * Initial loading state for photos
     */
    object Loading : HomeUiState
    
    /**
     * Error state for the home screen
     */
    data class Error(val errorMessage: String) : HomeUiState
    
    /**
     * Success state containing photos and prompt information
     */
    data class Success(
        val photos: List<UnsplashPhoto>,
        val favoritePhotos: List<UnsplashPhoto> = emptyList(),
        val isPromptLoading: Boolean = false,
        val outputText: String? = null,
        val promptError: String? = null,
        val showFavoritesOnly: Boolean = false
    ) : HomeUiState
}

/**
 * Top-level UI state for the detail screen
 */
sealed interface DetailUiState {
    // Common properties that should be available in all states
    val imageResourceId: Int
    val imageUrl: String?
    val photographerName: String?
    val photoId: String?
    val altDescription: String?
    
    /**
     * Initial state
     */
    data class Initial(
        override val imageResourceId: Int = -1,
        override val imageUrl: String? = null,
        override val photographerName: String? = null,
        override val photoId: String? = null,
        override val altDescription: String? = null
    ) : DetailUiState
    
    /**
     * Loading state
     */
    data class Loading(
        override val imageResourceId: Int = -1,
        override val imageUrl: String? = null,
        override val photographerName: String? = null,
        override val photoId: String? = null,
        override val altDescription: String? = null
    ) : DetailUiState
    
    /**
     * Success state with the generated description
     */
    data class Success(
        val outputText: String,
        val isFavorite: Boolean = false,
        override val imageResourceId: Int = -1,
        override val imageUrl: String? = null,
        override val photographerName: String? = null,
        override val photoId: String? = null,
        override val altDescription: String? = null
    ) : DetailUiState
    
    /**
     * Error state
     */
    data class Error(
        val errorMessage: String,
        override val imageResourceId: Int = -1,
        override val imageUrl: String? = null,
        override val photographerName: String? = null,
        override val photoId: String? = null,
        override val altDescription: String? = null
    ) : DetailUiState
}