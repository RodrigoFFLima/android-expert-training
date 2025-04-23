package com.example.myapplication

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.baking_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            
            // Show favorites filter when we have a Success state
            if (homeUiState is HomeUiState.Success) {
                val showFavoritesOnly = (homeUiState as HomeUiState.Success).showFavoritesOnly
                
                FilterChip(
                    selected = showFavoritesOnly,
                    onClick = { homeViewModel.toggleFavoritesFilter() },
                    label = { Text("Favorites") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (showFavoritesOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Filter favorites",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

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
                // Get the photos to display based on filter
                val photosToDisplay = if (state.showFavoritesOnly) {
                    state.favoritePhotos
                } else {
                    state.photos
                }
                
                if (photosToDisplay.isEmpty() && state.showFavoritesOnly) {
                    // Show message when no favorites exist
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No favorite images yet. Tap the heart icon to add favorites!",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    // Display the images from Unsplash in a grid
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 180.dp),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(photosToDisplay) { photo ->
                            ImageItem(
                                photo = photo,
                                onFavoriteClick = {
                                    homeViewModel.toggleFavorite(photo)
                                },
                                onImageClick = {
                                    // Navigate to detail view on click
                                    val intent = Intent(context, DetailActivity::class.java).apply {
                                        putExtra(DetailActivity.IMAGE_URL_KEY, photo.urls.regular)
                                        putExtra(
                                            DetailActivity.PHOTOGRAPHER_NAME_KEY,
                                            photo.user.fullName
                                        )
                                        putExtra(DetailActivity.PHOTO_ID_KEY, photo.id)
                                        putExtra(DetailActivity.ALT_DESCRIPTION_KEY, photo.alt_description ?: photo.bestDescription)
                                    }
                                    context.startActivity(intent)
                                },
                                isFavorite = state.favoritePhotos.any { it.id == photo.id }
                            )
                        }
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
                        // Card for local images with similar layout to Unsplash images
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
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
                                                    putExtra(
                                                        DetailActivity.ALT_DESCRIPTION_KEY,
                                                        context.getString(imageDescriptions[index])
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
                                                putExtra(
                                                    DetailActivity.ALT_DESCRIPTION_KEY,
                                                    context.getString(imageDescriptions[index])
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
    }
}

@Composable
fun ImageItem(
    photo: UnsplashPhoto,
    onFavoriteClick: () -> Unit,
    onImageClick: () -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onImageClick) // Click on image to go to detail view
    ) {
        Box {
            AsyncImage(
                model = photo.urls.small,
                contentDescription = photo.bestDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Row at the bottom with username and favorite button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                    .padding(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Username text
                    Text(
                        text = photo.user.username,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    // Favorite icon
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun BakingScreenPreview() {
    BakingScreen()
}