package com.aneez.brandshift.feature.home

import com.aneez.brandshift.domain.model.AppPreferences
import com.aneez.brandshift.domain.model.LauncherIdentity

/**
 * Dashboard UI States.
 */
sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val currentIdentity: LauncherIdentity,
        val appPreferences: AppPreferences
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

/**
 * Dashboard UI Actions.
 */
sealed interface HomeUiEvent {
    object LoadHomeData : HomeUiEvent
    object ResetToDefault : HomeUiEvent
    data class ShowResetConfirmation(val show: Boolean) : HomeUiEvent
}

/**
 * Dashboard Navigation and notification side effects.
 */
sealed interface HomeUiEffect {
    data class ShowToast(val message: String) : HomeUiEffect
    object NavigateToIdentities : HomeUiEffect
    object NavigateToSettings : HomeUiEffect
    object NavigateToAbout : HomeUiEffect
}
