package com.aneez.brandshift.feature.identities

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aneez.brandshift.core.designsystem.components.AppToolbar
import com.aneez.brandshift.core.designsystem.components.ConfirmationDialog
import com.aneez.brandshift.core.designsystem.components.EmptyState
import com.aneez.brandshift.core.designsystem.components.IdentityCard
import com.aneez.brandshift.core.designsystem.components.LoadingView
import com.aneez.brandshift.core.designsystem.components.PrimaryButton
import kotlinx.coroutines.flow.collectLatest

/**
 * Screen displaying the adaptive grid of available launcher profiles.
 */
@Composable
fun IdentitiesScreen(
    viewModel: IdentitiesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPreview: (identityId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val confirmingIdentity by viewModel.confirmingIdentity.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is IdentitiesUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is IdentitiesUiEffect.NavigateToPreview -> onNavigateToPreview(effect.identityId)
                IdentitiesUiEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    if (confirmingIdentity != null) {
        val identity = confirmingIdentity!!
        ConfirmationDialog(
            title = "Change App Identity?",
            message = "Apply '${identity.name}' branding. Note: Android will close the app to reset the launcher caches. You can re-open it from your home screen.",
            confirmText = "Apply",
            dismissText = "Cancel",
            onConfirm = { viewModel.handleEvent(IdentitiesUiEvent.ConfirmApplyIdentity(identity)) },
            onDismiss = { viewModel.handleEvent(IdentitiesUiEvent.ShowApplyConfirmation(null, false)) }
        )
    }

    Scaffold(
        topBar = {
            AppToolbar(
                title = "Branding Profiles",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = onNavigateBack
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                IdentitiesUiState.Loading -> {
                    LoadingView(modifier = Modifier.fillMaxSize())
                }
                is IdentitiesUiState.Error -> {
                    EmptyState(
                        title = "Error Loading Profiles",
                        description = state.message,
                        icon = Icons.Default.Warning,
                        actionButton = {
                            PrimaryButton(
                                text = "Retry",
                                onClick = { viewModel.handleEvent(IdentitiesUiEvent.LoadIdentities) }
                            )
                        }
                    )
                }
                is IdentitiesUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.identities,
                            key = { it.id }
                        ) { identity ->
                            val isSelected = identity.id == state.currentIdentity.id
                            IdentityCard(
                                identity = identity,
                                isSelected = isSelected,
                                onApplyClick = {
                                    viewModel.handleEvent(IdentitiesUiEvent.RequestApplyIdentity(identity))
                                },
                                onPreviewClick = {
                                    onNavigateToPreview(identity.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
