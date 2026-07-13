package com.aneez.brandshift.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneez.brandshift.core.dispatcher.DispatcherProvider
import com.aneez.brandshift.domain.model.ThemeMode
import com.aneez.brandshift.domain.repository.IdentityRepository
import com.aneez.brandshift.domain.usecase.ResetIdentityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing display configurations and application references links.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: IdentityRepository,
    private val resetIdentityUseCase: ResetIdentityUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<SettingsUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<SettingsUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadSettings()
    }

    fun handleEvent(event: SettingsUiEvent) {
        when (event) {
            SettingsUiEvent.LoadSettings -> loadSettings()
            is SettingsUiEvent.UpdateThemeMode -> updateThemeMode(event.themeMode)
            is SettingsUiEvent.UpdateDynamicColors -> updateDynamicColors(event.enabled)
            SettingsUiEvent.ResetBranding -> resetBranding()
            SettingsUiEvent.TriggerShareApp -> shareApp()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch(dispatcherProvider.io) {
            repository.getAppPreferences()
                .catch { e ->
                    _uiState.value = SettingsUiState.Error(e.message ?: "Failed loading settings.")
                }
                .collect { prefs ->
                    _uiState.value = SettingsUiState.Success(prefs)
                }
        }
    }

    private fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch(dispatcherProvider.io) {
            repository.updateThemeMode(themeMode)
        }
    }

    private fun updateDynamicColors(enabled: Boolean) {
        viewModelScope.launch(dispatcherProvider.io) {
            repository.updateDynamicColors(enabled)
        }
    }

    private fun resetBranding() {
        viewModelScope.launch(dispatcherProvider.io) {
            val result = resetIdentityUseCase()
            if (result.isSuccess) {
                _uiEffect.send(SettingsUiEffect.ShowToast("Identity configuration reset to default."))
            } else {
                _uiEffect.send(SettingsUiEffect.ShowToast("Failed resetting identities configurations."))
            }
        }
    }

    private fun shareApp() {
        viewModelScope.launch {
            _uiEffect.send(
                SettingsUiEffect.ShareAppContent(
                    "Check out BrandShift! An Android showcase application demonstrating dynamic launcher identity changes: https://github.com/aneez197/BrandShift"
                )
            )
        }
    }
}
