# Baking with Gemini

An Android application that uses Google's Gemini AI to generate recipes and information about baked goods from images, with photos from Unsplash API.

## Overview

This Android application allows users to:
- Browse high-quality bakery images from Unsplash API
- Select from a collection of baking images
- Enter prompts related to the selected image
- Receive AI-generated responses from Google's Gemini model about recipes and baking information

## Features

- Dynamic image gallery with photos from Unsplash API
- Image selection interface with visual feedback for the selected item
- Image detail view with photographer attribution
- Text input for customized prompts
- Integration with Google's Gemini AI model (gemini-1.5-flash)
- Real-time status updates with loading indicators
- Error handling with visual feedback
- Fallback to local images when API is unavailable

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **AI Integration**: Google Generative AI SDK
- **Networking**: Retrofit, OkHttp
- **Image Loading**: Coil
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)

## Dependencies

- Androidx Core KTX: 1.15.0
- Jetpack Compose BOM: 2024.09.00
- Lifecycle Components: 2.8.7
- Google Generative AI SDK: 0.9.0
- Material Design 3
- Retrofit: 2.9.0
- OkHttp: 4.11.0
- Coil: 2.4.0

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11
- Google Gemini API Key

### API Key Setup

1. Obtain a Gemini API key from [Google AI Studio](https://aistudio.google.com/)
2. Obtain an Unsplash API key from [Unsplash Developer Portal](https://unsplash.com/developers)
3. Add your API keys to `local.properties`:
   ```
   apiKey=YOUR_GEMINI_API_KEY
   unsplashApiKey=YOUR_UNSPLASH_API_KEY
   ```

### Build and Run

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run on an emulator or physical device

## Project Structure

- `app/src/main/java/com/example/myapplication/`
  - `MainActivity.kt` - Entry point that hosts the main Compose UI
  - `BakingScreen.kt` - Main Compose UI for baking interface
  - `BakingViewModel.kt` - Handles business logic and Gemini API interaction
  - `DetailActivity.kt` - Activity for displaying image details
  - `UiState.kt` - Sealed interfaces for managing UI states
  - `data/` - Data layer
    - `UnsplashApiService.kt` - Retrofit API interface for Unsplash
    - `UnsplashPhoto.kt` - Data models for Unsplash API
    - `UnsplashRepository.kt` - Repository for handling Unsplash API requests

## How It Works

1. The app fetches baking-related images from Unsplash API
2. Images are displayed in a horizontally scrollable gallery
3. User selects an image and can view details by tapping on it
4. In the detail view, users can see the full image and photographer attribution
5. User can enter a prompt (e.g., "Describe the image.")
6. For local images, the app sends the image and prompt to the Gemini API
7. For Unsplash images, the app provides information about the photographer
8. Results are displayed in the text area below the inputs

## License

This project uses the standard Android application structure and is meant for educational and demonstration purposes.

## Contributions

This is a sample project. Feel free to fork and modify for your own purposes.