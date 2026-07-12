package com.aneez.brandshift.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneez.brandshift.core.dispatcher.DispatcherProvider
import com.aneez.brandshift.domain.repository.IdentityRepository
import com.aneez.brandshift.domain.usecase.GetCurrentIdentityUseCase
import com.aneez.brandshift.domain.usecase.ResetIdentityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing the dashboard UI state, handling identity resets and preference streaming.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val resetIdentityUseCase: ResetIdentityUseCase,
    private val repository: IdentityRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<HomeUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<HomeUiEffect> = _uiEffect.receiveAsFlow()

    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog: StateFlow<Boolean> = _showResetDialog.asStateFlow()

    init {
        loadHomeData()
    }

    fun handleEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.LoadHomeData -> loadHomeData()
            HomeUiEvent.ResetToDefault -> resetToDefault()
            is HomeUiEvent.ShowResetConfirmation -> {
                _showResetDialog.value = event.show
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch(dispatcherProvider.io) {
            combine(
                getCurrentIdentityUseCase(),
                repository.getAppPreferences()
            ) { identity, prefs ->
                HomeUiState.Success(identity, prefs)
            }
            .catch { e ->
                _uiState.value = HomeUiState.Error(e.message ?: "An unexpected error occurred.")
            }
            .collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun resetToDefault() {
        _showResetDialog.value = false
        viewModelScope.launch(dispatcherProvider.io) {
            val result = resetIdentityUseCase()
            if (result.isSuccess) {
                _uiEffect.send(HomeUiEffect.ShowToast("Branding identity reset to default."))
            } else {
                _uiEffect.send(HomeUiEffect.ShowToast("Failed to reset branding components."))
            }
        }
    }
}
