package com.aneez.brandshift.feature.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aneez.brandshift.core.designsystem.components.AnimatedIcon
import com.aneez.brandshift.core.designsystem.components.AnimationType
import com.aneez.brandshift.core.designsystem.components.BrandCard
import com.aneez.brandshift.core.designsystem.components.ConfirmationDialog
import com.aneez.brandshift.core.designsystem.components.EmptyState
import com.aneez.brandshift.core.designsystem.components.GradientHeader
import com.aneez.brandshift.core.designsystem.components.LoadingView
import com.aneez.brandshift.core.designsystem.components.PrimaryButton
import com.aneez.brandshift.core.designsystem.components.SecondaryButton
import com.aneez.brandshift.core.designsystem.theme.BrandGradients
import kotlinx.coroutines.flow.collectLatest

/**
 * Main dashboard layout detailing active icon graphics and general application navigation.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToIdentities: () -> Unit,
    onNavigateToPreview: (identityId: String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val showResetDialog by viewModel.showResetDialog.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is HomeUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                HomeUiEffect.NavigateToIdentities -> onNavigateToIdentities()
                HomeUiEffect.NavigateToSettings -> onNavigateToSettings()
                HomeUiEffect.NavigateToAbout -> onNavigateToAbout()
            }
        }
    }

    if (showResetDialog) {
        ConfirmationDialog(
            title = "Reset Branding?",
            message = "This will restore the app theme to System default and re-apply the default BrandShift (Rocket) identity.",
            confirmText = "Reset",
            dismissText = "Cancel",
            onConfirm = { viewModel.handleEvent(HomeUiEvent.ResetToDefault) },
            onDismiss = { viewModel.handleEvent(HomeUiEvent.ShowResetConfirmation(false)) }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                HomeUiState.Loading -> {
                    LoadingView(modifier = Modifier.fillMaxSize())
                }
                is HomeUiState.Error -> {
                    EmptyState(
                        title = "Error Loading Dashboard",
                        description = state.message,
                        icon = Icons.Default.Warning,
                        actionButton = {
                            PrimaryButton(
                                text = "Retry",
                                onClick = { viewModel.handleEvent(HomeUiEvent.LoadHomeData) }
                            )
                        }
                    )
                }
                is HomeUiState.Success -> {
                    val activeIdentity = state.currentIdentity
                    val gradientColors = BrandGradients.getGradientForId(activeIdentity.id)

                    GradientHeader(
                        title = "BrandShift",
                        subtitle = "Change Your App\'s Identity.",
                        gradientColors = gradientColors
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = "Active Identity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        BrandCard(
                            gradientColors = gradientColors
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AnimatedIcon(
                                    iconResId = activeIdentity.iconResId,
                                    animationType = AnimationType.FLOAT,
                                    modifier = Modifier.size(96.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = activeIdentity.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeIdentity.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Text(
                            text = "Quick Actions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Brush,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Branding Center",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Browse profiles and switch icons dynamically.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SecondaryButton(
                                        text = "Preview",
                                        onClick = { onNavigateToPreview(activeIdentity.id) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    PrimaryButton(
                                        text = "Change Identity",
                                        onClick = onNavigateToIdentities,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                onClick = onNavigateToSettings,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Settings",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Configure Theme",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                onClick = onNavigateToAbout,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "About",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Architecture & Stack",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            onClick = { viewModel.handleEvent(HomeUiEvent.ShowResetConfirmation(true)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Reset Device Branding",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Revert back to original launcher icon and metadata.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
