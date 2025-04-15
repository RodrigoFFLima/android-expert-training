package com.example.myapplication

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.data.UnsplashPhoto

// Fallback images for when API fails
val fallbackImages = arrayOf(
    R.drawable.baked_goods_1,
    R.drawable.baked_goods_2,
    R.drawable.baked_goods_3,
)
val imageDescriptions = arrayOf(
    R.string.image1_description,
    R.string.image2_description,
    R.string.image3_description,
)

@Composable
fun BakingScreen(
    homeViewModel: HomeViewModel = viewModel()
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.baking_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        // Display images based on home UI state
        when (val state = homeUiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Success -> {
                // Display the images from Unsplash in a grid
                val photos = state.photos
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(photos) { photo ->
                        ImageItem(
                            photo = photo,
                            onClick = {
                                // Navigate to detail view on click
                                val intent = Intent(context, DetailActivity::class.java).apply {
                                    putExtra(DetailActivity.IMAGE_URL_KEY, photo.urls.regular)
                                    putExtra(
                                        DetailActivity.PHOTOGRAPHER_NAME_KEY,
                                        photo.user.fullName
                                    )
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            is HomeUiState.Error -> {
                // Show error message
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Fallback to local images if API fails
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(fallbackImages) { index, image ->
                        // Bug: Variable padding based on index causing grid items to shift
                        val extraPadding = if (index % 2 == 0) 16.dp else 8.dp
                        
                        // Card for local images with similar layout to Unsplash images
                        Card(
                            modifier = Modifier
                                .padding(start = extraPadding, end = 8.dp, top = 8.dp, bottom = 8.dp)
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            Box {
                                Image(
                                    painter = painterResource(image),
                                    contentDescription = stringResource(imageDescriptions[index]),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            val intent =
                                                Intent(context, DetailActivity::class.java).apply {
                                                    putExtra(
                                                        DetailActivity.IMAGE_RESOURCE_ID_KEY,
                                                        fallbackImages[index]
                                                    )
                                                }
                                            context.startActivity(intent)
                                        },
                                    contentScale = ContentScale.Crop
                                )

                                // Details button at top right
                                Button(
                                    onClick = {
                                        val intent =
                                            Intent(context, DetailActivity::class.java).apply {
                                                putExtra(
                                                    DetailActivity.IMAGE_RESOURCE_ID_KEY,
                                                    fallbackImages[index]
                                                )
                                            }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(36.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "↗",
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // No additional content below the grid
    }
}

@Composable
fun ImageItem(
    photo: UnsplashPhoto,
    onClick: () -> Unit,
) {
    // Bug: Variable padding based on username length causing grid items to shift
    val extraPadding = if (photo.user.username.length % 2 == 0) 16.dp else 8.dp
    
    Card(
        modifier = Modifier
            .padding(start = extraPadding, end = 8.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick) // Click on image to go to detail view
    ) {
        Box {
            AsyncImage(
                model = photo.urls.small,
                contentDescription = photo.bestDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Username at bottom left
            Text(
                text = photo.user.username,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                    .padding(4.dp)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun BakingScreenPreview() {
    BakingScreen()
}