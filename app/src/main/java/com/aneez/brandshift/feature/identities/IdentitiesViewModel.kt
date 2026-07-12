package com.aneez.brandshift.feature.identities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneez.brandshift.core.dispatcher.DispatcherProvider
import com.aneez.brandshift.domain.model.LauncherIdentity
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
 * ViewModel managing the collection of launcher profiles and executing identity switches.
 */
@HiltViewModel
class IdentitiesViewModel @Inject constructor(
    private val getIdentitiesUseCase: GetIdentitiesUseCase,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val applyIdentityUseCase: ApplyIdentityUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<IdentitiesUiState>(IdentitiesUiState.Loading)
    val uiState: StateFlow<IdentitiesUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<IdentitiesUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<IdentitiesUiEffect> = _uiEffect.receiveAsFlow()

    private val _confirmingIdentity = MutableStateFlow<LauncherIdentity?>(null)
    val confirmingIdentity: StateFlow<LauncherIdentity?> = _confirmingIdentity.asStateFlow()

    init {
        loadIdentities()
    }

    fun handleEvent(event: IdentitiesUiEvent) {
        when (event) {
            IdentitiesUiEvent.LoadIdentities -> loadIdentities()
            is IdentitiesUiEvent.RequestApplyIdentity -> {
                _confirmingIdentity.value = event.identity
            }
            is IdentitiesUiEvent.ConfirmApplyIdentity -> {
                applyIdentity(event.identity)
            }
            is IdentitiesUiEvent.ShowApplyConfirmation -> {
                _confirmingIdentity.value = if (event.show) event.identity else null
            }
        }
    }

    private fun loadIdentities() {
        viewModelScope.launch(dispatcherProvider.io) {
            combine(
                getIdentitiesUseCase(),
                getCurrentIdentityUseCase()
            ) { list, active ->
                IdentitiesUiState.Success(list, active)
            }
            .catch { e ->
                _uiState.value = IdentitiesUiState.Error(e.message ?: "Failed loading profiles.")
            }
            .collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun applyIdentity(identity: LauncherIdentity) {
        _confirmingIdentity.value = null
        viewModelScope.launch(dispatcherProvider.io) {
            _uiEffect.send(IdentitiesUiEffect.ShowToast("Applying ${identity.name}... Applying branding will close the app."))
            val result = applyIdentityUseCase(identity.aliasClassName, killApp = true)
            if (result.isFailure) {
                _uiEffect.send(IdentitiesUiEffect.ShowToast("Failed to switch component settings."))
            }
        }
    }
}
