package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.MyApplicationTheme

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        // Get the image information from the intent
        val imageResourceId = intent.getIntExtra(IMAGE_RESOURCE_ID_KEY, -1)
        val factory = BakingViewModelFactory(this)

        setContent {
            val viewModel: BakingViewModel = viewModel(
                factory = factory
            )
            MyApplicationTheme {
                // Use Scaffold for proper insets handling
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImageDetailScreen(imageResourceId, viewModel)
                }
            }
        }
    }
    
    companion object {
        const val IMAGE_RESOURCE_ID_KEY = "image_resource_id"
        const val IMAGE_URL_KEY = "image_url" 
        const val PHOTOGRAPHER_NAME_KEY = "photographer_name"
        const val PHOTO_ID_KEY = "photo_id"
        const val ALT_DESCRIPTION_KEY = "alt_description"
    }
}

@Composable
fun ImageDetailScreen(imageResourceId: Int, viewModel: BakingViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the appropriate image with proper accessibility
        if (detailUiState.imageUrl != null) {
            // Remote image from URL
            AsyncImage(
                model = detailUiState.imageUrl,
                contentDescription = "Detailed image view",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
        } else if (detailUiState.imageResourceId != -1) {
            // Local resource image
            Image(
                painter = painterResource(id = detailUiState.imageResourceId),
                contentDescription = "Detailed image view",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Only show photographer credit and favorite button for Unsplash photos
        if (detailUiState.imageUrl != null) {
            // Row for photographer credit and favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display photographer credit
                Text(
                    text = "Photo by ${detailUiState.photographerName ?: "Unknown photographer"} on Unsplash",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                
                // Only show favorite button if we have a photoId
                if (detailUiState.photoId != null) {
                    // Favorite button - only shown when in Success state with isFavorite status
                    if (detailUiState is DetailUiState.Success) {
                        val isFavorite = (detailUiState as DetailUiState.Success).isFavorite
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Display state
        when (val state = uiState) {
            is UiState.Initial,
            is UiState.Loading -> {
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
