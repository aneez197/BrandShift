package com.aneez.brandshift.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneez.brandshift.core.dispatcher.DispatcherProvider
import com.aneez.brandshift.domain.usecase.GetCurrentIdentityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing the Splash screen lifecycle and bootstrapping configurations.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<SplashUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<SplashUiEffect> = _uiEffect.receiveAsFlow()

    init {
        handleEvent(SplashUiEvent.LoadConfiguration)
    }

    fun handleEvent(event: SplashUiEvent) {
        when (event) {
            SplashUiEvent.LoadConfiguration -> loadConfig()
        }
    }

    private fun loadConfig() {
        viewModelScope.launch(dispatcherProvider.io) {
            // Introduce aesthetic delay to display branding elements
            delay(1500)
            // Prefetch active identity setup
            getCurrentIdentityUseCase().first()
            _uiEffect.send(SplashUiEffect.NavigateToHome)
        }
    }
}
