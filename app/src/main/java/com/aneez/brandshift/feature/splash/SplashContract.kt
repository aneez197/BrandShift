package com.aneez.brandshift.feature.splash

/**
 * Splash Screen MVI Contract parameters.
 */
sealed interface SplashUiState {
    object Loading : SplashUiState
}

sealed interface SplashUiEvent {
    object LoadConfiguration : SplashUiEvent
}

sealed interface SplashUiEffect {
    object NavigateToHome : SplashUiEffect
}
