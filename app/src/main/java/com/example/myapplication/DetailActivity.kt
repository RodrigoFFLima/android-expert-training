package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.MyApplicationTheme

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get the image information from the intent
        val imageResourceId = intent.getIntExtra(IMAGE_RESOURCE_ID_KEY, -1)
        val imageUrl = intent.getStringExtra(IMAGE_URL_KEY)
        val photographerName = intent.getStringExtra(PHOTOGRAPHER_NAME_KEY)
        
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (imageUrl != null) {
                        // Remote image from Unsplash
                        ImageDetailScreenFromUrl(imageUrl, photographerName ?: "Unknown photographer")
                    } else if (imageResourceId != -1) {
                        // Fallback to local resource
                        ImageDetailScreen(imageResourceId)
                    }
                }
            }
        }
    }
    
    companion object {
        const val IMAGE_RESOURCE_ID_KEY = "image_resource_id"
        const val IMAGE_URL_KEY = "image_url" 
        const val PHOTOGRAPHER_NAME_KEY = "photographer_name"
    }
}

@Composable
fun ImageDetailScreen(imageResourceId: Int, viewModel: DetailViewModel = viewModel()) {
    viewModel.init(LocalContext.current)
    val detailUiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the full-size image
        Image(
            painter = painterResource(id = imageResourceId),
            contentDescription = "Detailed image view",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        // Display state
        when (detailUiState) {
            is DetailUiState.Initial -> {
                viewModel.describeImage(imageResourceId)
            }
            is DetailUiState.Loading -> {
                CircularProgressIndicator()
            }
            is DetailUiState.Success -> {
                Text(
                    text = "AI Description:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(text = (detailUiState as DetailUiState.Success).outputText)
            }
            is DetailUiState.Error -> {
                Text(
                    text = "Error: ${(detailUiState as DetailUiState.Error).errorMessage}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ImageDetailScreenFromUrl(
    imageUrl: String,
    photographerName: String,
    viewModel: DetailViewModel = viewModel()
) {
    viewModel.init(LocalContext.current)
    val detailUiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the full-size image from URL
        AsyncImage(
            model = imageUrl,
            contentDescription = "Detailed image view",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        // Display photographer credit
        Text(
            text = "Photo by $photographerName on Unsplash",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Display state
        when (detailUiState) {
            is DetailUiState.Initial -> {
                // Trigger Gemini processing for the URL image
                viewModel.describeImageFromUrl(imageUrl, photographerName)
            }
            is DetailUiState.Loading -> {
                CircularProgressIndicator()
            }
            is DetailUiState.Success -> {
                Text(
                    text = "AI Description:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(text = (detailUiState as DetailUiState.Success).outputText)
            }
            is DetailUiState.Error -> {
                Text(
                    text = "Error: ${(detailUiState as DetailUiState.Error).errorMessage}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}