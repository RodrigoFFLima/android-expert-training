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
import com.example.myapplication.ui.theme.MyApplicationTheme

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get the image resource ID from the intent
        val imageResourceId = intent.getIntExtra(IMAGE_RESOURCE_ID_KEY, -1)
        val factory = BakingViewModelFactory(this)

        setContent {
            val viewModel: BakingViewModel = viewModel(
                factory = factory
            )
            MyApplicationTheme {
                Surface(
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
    }
}

@Composable
fun ImageDetailScreen(imageResourceId: Int, viewModel: BakingViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

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
        when (val state = uiState) {
            is UiState.Initial -> {
                viewModel.describeImage(imageResourceId)
            }
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Success -> {
                Text(
                    text = "AI Description:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(text = state.outputText)
            }
            is UiState.Error -> {
                Text(
                    text = "Error: ${state.errorMessage}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}