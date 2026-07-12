package com.aneez.brandshift.feature.identities

import com.aneez.brandshift.domain.model.LauncherIdentity

/**
 * Identities Screen UI states.
 */
sealed interface IdentitiesUiState {
    object Loading : IdentitiesUiState
    data class Success(
        val identities: List<LauncherIdentity>,
        val currentIdentity: LauncherIdentity
    ) : IdentitiesUiState
    data class Error(val message: String) : IdentitiesUiState
}

/**
 * Identities Screen User events.
 */
sealed interface IdentitiesUiEvent {
    object LoadIdentities : IdentitiesUiEvent
    data class RequestApplyIdentity(val identity: LauncherIdentity) : IdentitiesUiEvent
    data class ConfirmApplyIdentity(val identity: LauncherIdentity) : IdentitiesUiEvent
    data class ShowApplyConfirmation(val identity: LauncherIdentity?, val show: Boolean) : IdentitiesUiEvent
}

/**
 * Identities Screen Side effects.
 */
sealed interface IdentitiesUiEffect {
    data class ShowToast(val message: String) : IdentitiesUiEffect
    data class NavigateToPreview(val identityId: String) : IdentitiesUiEffect
    object NavigateBack : IdentitiesUiEffect
}
