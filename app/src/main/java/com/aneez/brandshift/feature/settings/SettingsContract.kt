package com.aneez.brandshift.feature.settings

import com.aneez.brandshift.domain.model.AppPreferences
import com.aneez.brandshift.domain.model.ThemeMode

/**
 * Settings UI parameters.
 */
sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val preferences: AppPreferences) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

sealed interface SettingsUiEvent {
    object LoadSettings : SettingsUiEvent
    data class UpdateThemeMode(val themeMode: ThemeMode) : SettingsUiEvent
    data class UpdateDynamicColors(val enabled: Boolean) : SettingsUiEvent
    object ResetBranding : SettingsUiEvent
    object TriggerShareApp : SettingsUiEvent
}

sealed interface SettingsUiEffect {
    data class ShowToast(val message: String) : SettingsUiEffect
    data class ShareAppContent(val text: String) : SettingsUiEffect
}
