# Baking with Gemini

An Android application that uses Google's Gemini AI to generate recipes and information about baked goods from images.

## Overview

This Android application allows users to:
- Select from a collection of baking images (cupcakes, cookies, cakes)
- Enter prompts related to the selected image
- Receive AI-generated responses from Google's Gemini model about recipes and baking information

## Features

- Image selection interface with visual feedback for the selected item
- Text input for customized prompts
- Integration with Google's Gemini AI model (gemini-1.5-flash)
- Real-time status updates with loading indicators
- Error handling with visual feedback

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **AI Integration**: Google Generative AI SDK
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)

## Dependencies

- Androidx Core KTX: 1.15.0
- Jetpack Compose BOM: 2024.09.00
- Lifecycle Components: 2.8.7
- Google Generative AI SDK: 0.9.0
- Material Design 3

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11
- Google Gemini API Key

### API Key Setup

1. Obtain a Gemini API key from [Google AI Studio](https://aistudio.google.com/)
2. Add your API key to `local.properties`:
   ```
   apiKey=YOUR_GEMINI_API_KEY
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
  - `UiState.kt` - Sealed interface for managing UI states

## How It Works

1. The app displays a selection of baked goods images
2. User selects an image and enters a prompt (e.g., "Provide a recipe for the baked goods in the image")
3. The app sends the image and prompt to the Gemini API
4. Results are displayed in the text area below the inputs

## License

This project uses the standard Android application structure and is meant for educational and demonstration purposes.

## Contributions

This is a sample project. Feel free to fork and modify for your own purposes.