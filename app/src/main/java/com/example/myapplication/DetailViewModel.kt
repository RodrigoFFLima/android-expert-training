package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class DetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Initial)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    fun describeImageFromUrl(imageUrl: String, photographerName: String) {
        _uiState.value = DetailUiState.Loading

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
                    _uiState.value = DetailUiState.Success(fallbackMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Failed to process image: ${e.localizedMessage}"
                _uiState.value = DetailUiState.Error(errorMessage)
            }
        }
    }

    fun describeImage(resourceId: Int) {
        _uiState.value = DetailUiState.Loading

        val image = loadBitmapFromResource(context, resourceId)
        if (image != null) {
            processImageForDescription(
                image,
                "Describe this baked good or dessert in detail. What is it called? What ingredients might be in it? What flavors would it have?"
            )
        } else {
            val errorMessage = "Failed to load image from resources"
            _uiState.value = DetailUiState.Error(errorMessage)
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
                    _uiState.value = DetailUiState.Success(outputContent)
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "Error processing image"
                _uiState.value = DetailUiState.Error(errorMessage)
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
