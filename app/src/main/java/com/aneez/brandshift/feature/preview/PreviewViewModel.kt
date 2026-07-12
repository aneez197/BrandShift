package com.aneez.brandshift.feature.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneez.brandshift.core.dispatcher.DispatcherProvider
import com.aneez.brandshift.domain.usecase.ApplyIdentityUseCase
import com.aneez.brandshift.domain.usecase.GetCurrentIdentityUseCase
import com.aneez.brandshift.domain.usecase.GetIdentitiesUseCase
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
 * ViewModel managing the mock launcher preview state and applying launcher identity updates.
 */
@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val getIdentitiesUseCase: GetIdentitiesUseCase,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val applyIdentityUseCase: ApplyIdentityUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<PreviewUiState>(PreviewUiState.Loading)
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<PreviewUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<PreviewUiEffect> = _uiEffect.receiveAsFlow()

    private val _showApplyDialog = MutableStateFlow(false)
    val showApplyDialog: StateFlow<Boolean> = _showApplyDialog.asStateFlow()

    fun handleEvent(event: PreviewUiEvent) {
        when (event) {
            is PreviewUiEvent.LoadPreview -> loadPreview(event.targetIdentityId)
            PreviewUiEvent.ApplyTargetIdentity -> applyTargetIdentity()
            is PreviewUiEvent.ShowApplyConfirmation -> {
                _showApplyDialog.value = event.show
            }
        }
    }

    private fun loadPreview(targetIdentityId: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            combine(
                getCurrentIdentityUseCase(),
                getIdentitiesUseCase()
            ) { current, allIdentities ->
                val target = allIdentities.firstOrNull { it.id == targetIdentityId } ?: current
                PreviewUiState.Success(current, target)
            }
            .catch { e ->
                _uiState.value = PreviewUiState.Error(e.message ?: "Failed loading preview info.")
            }
            .collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun applyTargetIdentity() {
        _showApplyDialog.value = false
        val state = uiState.value
        if (state is PreviewUiState.Success) {
            viewModelScope.launch(dispatcherProvider.io) {
                _uiEffect.send(PreviewUiEffect.ShowToast("Applying ${state.targetIdentity.name}... Closing app."))
                val result = applyIdentityUseCase(state.targetIdentity.aliasClassName, killApp = true)
                if (result.isFailure) {
                    _uiEffect.send(PreviewUiEffect.ShowToast("Failed to switch launcher component settings."))
                }
            }
        }
    }
}
