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
        val isPromptLoading: Boolean = false,
        val outputText: String? = null,
        val promptError: String? = null
    ) : HomeUiState
}

/**
 * Top-level UI state for the detail screen
 */
sealed interface DetailUiState {
    /**
     * Initial state
     */
    object Initial : DetailUiState
    
    /**
     * Loading state
     */
    object Loading : DetailUiState
    
    /**
     * Success state with the generated description
     */
    data class Success(val outputText: String) : DetailUiState
    
    /**
     * Error state
     */
    data class Error(val errorMessage: String) : DetailUiState
}