package com.aneez.brandshift.feature.preview

import com.aneez.brandshift.domain.model.LauncherIdentity

/**
 * Preview Screen MVI state contract parameters.
 */
sealed interface PreviewUiState {
    object Loading : PreviewUiState
    data class Success(
        val currentIdentity: LauncherIdentity,
        val targetIdentity: LauncherIdentity
    ) : PreviewUiState
    data class Error(val message: String) : PreviewUiState
}

sealed interface PreviewUiEvent {
    data class LoadPreview(val targetIdentityId: String) : PreviewUiEvent
    object ApplyTargetIdentity : PreviewUiEvent
    data class ShowApplyConfirmation(val show: Boolean) : PreviewUiEvent
}

sealed interface PreviewUiEffect {
    data class ShowToast(val message: String) : PreviewUiEffect
    object NavigateBack : PreviewUiEffect
}
