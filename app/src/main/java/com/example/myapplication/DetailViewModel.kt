package com.example.myapplication

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.FavoriteRepository
import com.example.myapplication.data.UnsplashPhoto
import com.example.myapplication.data.UnsplashUrls
import com.example.myapplication.data.UnsplashUser
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Initial())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )
    
    private val favoriteRepository = FavoriteRepository(application)
    private var currentPhoto: UnsplashPhoto? = null
    
    // Function to set image data from Activity
    fun setImageData(
        imageResourceId: Int, 
        imageUrl: String?,
        photographerName: String?,
        photoId: String?,
        altDescription: String?
    ) {
        // Create a new state object with the image data
        // Keep the current state type but with updated image data
        val currentState = _uiState.value
        val newState = when (currentState) {
            is DetailUiState.Initial -> DetailUiState.Initial(
                imageResourceId = imageResourceId,
                imageUrl = imageUrl,
                photographerName = photographerName,
                photoId = photoId,
                altDescription = altDescription
            )
            is DetailUiState.Loading -> DetailUiState.Loading(
                imageResourceId = imageResourceId,
                imageUrl = imageUrl,
                photographerName = photographerName,
                photoId = photoId,
                altDescription = altDescription
            )
            is DetailUiState.Success -> DetailUiState.Success(
                outputText = currentState.outputText,
                isFavorite = currentState.isFavorite,
                imageResourceId = imageResourceId,
                imageUrl = imageUrl,
                photographerName = photographerName,
                photoId = photoId,
                altDescription = altDescription
            )
            is DetailUiState.Error -> DetailUiState.Error(
                errorMessage = currentState.errorMessage,
                imageResourceId = imageResourceId,
                imageUrl = imageUrl,
                photographerName = photographerName,
                photoId = photoId,
                altDescription = altDescription
            )
        }
        _uiState.value = newState
        
        // Create a photo object if we have an ID and URL
        if (photoId != null && imageUrl != null) {
            currentPhoto = UnsplashPhoto(
                id = photoId,
                width = 0,
                height = 0,
                description = null,
                alt_description = altDescription,
                urls = UnsplashUrls(
                    raw = imageUrl,
                    full = imageUrl,
                    regular = imageUrl,
                    small = imageUrl,
                    thumb = imageUrl
                ),
                user = UnsplashUser(
                    firstName = photographerName,
                    lastName = null,
                    username = photographerName ?: "Unknown"
                )
            )
        }
    }

    fun describeImageFromUrl(imageUrl: String, photographerName: String, photoId: String?) {
        // Preserve image data when changing state
        val currentState = _uiState.value
        _uiState.value = DetailUiState.Loading(
            imageResourceId = currentState.imageResourceId,
            imageUrl = currentState.imageUrl,
            photographerName = currentState.photographerName,
            photoId = currentState.photoId,
            altDescription = currentState.altDescription
        )
        
        // No need to recreate currentPhoto as it's already set in setImageData

        if (photoId != null) {
            // Check favorite status
            checkFavoriteStatus(photoId)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = downloadImageFromUrl(imageUrl)

                if (bitmap != null) {
                    processImageForDescription(
                        bitmap,
                        "Describe this baked good or dessert in detail. What is it called? What ingredients might be in it? What flavors would it have?"
                    )
                } else {
                    val fallbackMessage = "Photo by $photographerName on Unsplash"
                    val isFavorite = photoId?.let { favoriteRepository.isPhotoFavorite(it) } ?: false
                    val currentImageState = _uiState.value
                    _uiState.value = DetailUiState.Success(
                        outputText = fallbackMessage, 
                        isFavorite = isFavorite,
                        imageResourceId = currentImageState.imageResourceId,
                        imageUrl = currentImageState.imageUrl,
                        photographerName = currentImageState.photographerName,
                        photoId = currentImageState.photoId,
                        altDescription = currentImageState.altDescription
                    )
                }
            } catch (e: Exception) {
                val errorMessage = "Failed to process image: ${e.localizedMessage}"
                val currentImageState = _uiState.value
                _uiState.value = DetailUiState.Error(
                    errorMessage = errorMessage,
                    imageResourceId = currentImageState.imageResourceId,
                    imageUrl = currentImageState.imageUrl,
                    photographerName = currentImageState.photographerName,
                    photoId = currentImageState.photoId,
                    altDescription = currentImageState.altDescription
                )
            }
        }
    }

    fun describeImage(resourceId: Int) {
        // Preserve image data when changing state
        val currentState = _uiState.value
        _uiState.value = DetailUiState.Loading(
            imageResourceId = currentState.imageResourceId,
            imageUrl = currentState.imageUrl,
            photographerName = currentState.photographerName,
            photoId = currentState.photoId, 
            altDescription = currentState.altDescription
        )
        
        // Local images don't have IDs for favorites
        currentPhoto = null

        val image = loadBitmapFromResource(getApplication(), resourceId)
        if (image != null) {
            processImageForDescription(
                image,
                "Describe this baked good or dessert in detail. What is it called? What ingredients might be in it? What flavors would it have?"
            )
        } else {
            val errorMessage = "Failed to load image from resources"
            val currentImageState = _uiState.value
            _uiState.value = DetailUiState.Error(
                errorMessage = errorMessage,
                imageResourceId = currentImageState.imageResourceId,
                imageUrl = currentImageState.imageUrl,
                photographerName = currentImageState.photographerName,
                photoId = currentImageState.photoId,
                altDescription = currentImageState.altDescription
            )
        }
    }
    
    fun toggleFavorite() {
        currentPhoto?.let { photo ->
            viewModelScope.launch {
                val isFavorite = favoriteRepository.toggleFavorite(photo)
                val currentState = _uiState.value
                if (currentState is DetailUiState.Success) {
                    _uiState.value = currentState.copy(isFavorite = isFavorite)
                }
            }
        }
    }
    
    private fun checkFavoriteStatus(photoId: String) {
        viewModelScope.launch {
            val isFavorite = favoriteRepository.isPhotoFavorite(photoId)
            val currentState = _uiState.value
            if (currentState is DetailUiState.Success) {
                _uiState.value = currentState.copy(isFavorite = isFavorite)
            }
        }
    }

    private fun processImageForDescription(bitmap: Bitmap, prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    val currentState = _uiState.value
                    val isFavorite = currentState.photoId?.let { favoriteRepository.isPhotoFavorite(it) } ?: false
                    _uiState.value = DetailUiState.Success(
                        outputText = outputContent, 
                        isFavorite = isFavorite,
                        imageResourceId = currentState.imageResourceId,
                        imageUrl = currentState.imageUrl,
                        photographerName = currentState.photographerName,
                        photoId = currentState.photoId,
                        altDescription = currentState.altDescription
                    )
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "Error processing image"
                val currentState = _uiState.value
                _uiState.value = DetailUiState.Error(
                    errorMessage = errorMessage,
                    imageResourceId = currentState.imageResourceId,
                    imageUrl = currentState.imageUrl,
                    photographerName = currentState.photographerName,
                    photoId = currentState.photoId,
                    altDescription = currentState.altDescription
                )
            }
        }
    }

    private suspend fun downloadImageFromUrl(imageUrl: String): Bitmap? {
        return try {
            withContext(Dispatchers.IO) {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connectTimeout = 10000 // 10 seconds timeout
                connection.readTimeout = 15000 // 15 seconds read timeout
                connection.connect()
                val input = connection.inputStream
                try {
                    BitmapFactory.decodeStream(input)
                } finally {
                    input.close()
                    connection.disconnect()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun loadBitmapFromResource(context: Context, resourceId: Int): Bitmap? {
        return try {
            BitmapFactory.decodeResource(context.resources, resourceId)
        } catch (e: Exception) {
            null
        }
    }
}