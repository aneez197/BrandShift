package com.aneez.brandshift.feature.preview

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneez.brandshift.core.designsystem.components.AppToolbar
import com.aneez.brandshift.core.designsystem.components.ConfirmationDialog
import com.aneez.brandshift.core.designsystem.components.EmptyState
import com.aneez.brandshift.core.designsystem.components.LoadingView
import com.aneez.brandshift.core.designsystem.components.PrimaryButton
import com.aneez.brandshift.domain.model.LauncherIdentity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

/**
 * High-fidelity preview layout displaying comparative branding updates and interactive launcher demo.
 */
@Composable
fun PreviewScreen(
    identityId: String,
    viewModel: PreviewViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val showApplyDialog by viewModel.showApplyDialog.collectAsState()

    // Trigger loading data
    LaunchedEffect(key1 = identityId) {
        viewModel.handleEvent(PreviewUiEvent.LoadPreview(identityId))
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is PreviewUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                PreviewUiEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    if (showApplyDialog) {
        val state = uiState
        if (state is PreviewUiState.Success) {
            ConfirmationDialog(
                title = "Apply New Identity?",
                message = "Confirm changing the launcher profile to '${state.targetIdentity.name}'. Note: The app process will close to register branding cache changes.",
                confirmText = "Apply",
                dismissText = "Cancel",
                onConfirm = { viewModel.handleEvent(PreviewUiEvent.ApplyTargetIdentity) },
                onDismiss = { viewModel.handleEvent(PreviewUiEvent.ShowApplyConfirmation(false)) }
            )
        }
    }

    Scaffold(
        topBar = {
            AppToolbar(
                title = "Launcher Preview",
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
                PreviewUiState.Loading -> {
                    LoadingView(modifier = Modifier.fillMaxSize())
                }
                is PreviewUiState.Error -> {
                    EmptyState(
                        title = "Error Loading Preview",
                        description = state.message,
                        icon = Icons.Default.Warning,
                        actionButton = {
                            PrimaryButton(
                                text = "Retry",
                                onClick = { viewModel.handleEvent(PreviewUiEvent.LoadPreview(identityId)) }
                            )
                        }
                    )
                }
                is PreviewUiState.Success -> {
                    val current = state.currentIdentity
                    val target = state.targetIdentity

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Header Comparison Flow
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Current Profile
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = current.iconResId),
                                            contentDescription = null,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = current.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Current",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Target Profile
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = target.iconResId),
                                            contentDescription = null,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = target.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Proposed",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Mock Android Launcher Phone Frame
                        Text(
                            text = "Interactive Homescreen Preview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        var showTarget by remember { mutableStateOf(false) }

                        // Loop animation toggling display between current and target branding every 2 seconds
                        LaunchedEffect(key1 = true) {
                            while (true) {
                                delay(2200)
                                showTarget = !showTarget
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(28.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp)
                            ) {
                                // Status area info
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "9:41 AM",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "LTE  100%",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Date Widget mockup
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Sunday, July 12",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Text(
                                        text = "BrandShift Launcher",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(36.dp))

                                // Desktop Apps Grid Mockup (3x4)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    MockAppCell(icon = Icons.Default.Language, label = "Chrome", iconColor = Color(0xFFFBBF24))
                                    MockAppCell(icon = Icons.Default.Place, label = "Maps", iconColor = Color(0xFF10B981))
                                    MockAppCell(icon = Icons.Default.Image, label = "Photos", iconColor = Color(0xFF3B82F6))

                                    // Dynamic BrandShift icon card
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(64.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFF0F172A).copy(alpha = 0.8f))
                                                .padding(6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Crossfade(
                                                targetState = showTarget,
                                                label = "LauncherIconPreviewTransition"
                                            ) { showNew ->
                                                val iconRes = if (showNew) target.iconResId else current.iconResId
                                                Image(
                                                    painter = painterResource(id = iconRes),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Crossfade(
                                            targetState = showTarget,
                                            label = "LauncherLabelPreviewTransition"
                                        ) { showNew ->
                                            val appLabel = if (showNew) target.name else current.name
                                            Text(
                                                text = appLabel,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    MockAppCell(icon = Icons.Default.Call, label = "Phone", iconColor = Color(0xFF22C55E))
                                    MockAppCell(icon = Icons.Default.Email, label = "Messages", iconColor = Color(0xFF3B82F6))
                                    MockAppCell(icon = Icons.Default.PlayArrow, label = "YouTube", iconColor = Color(0xFFEF4444))
                                    Spacer(modifier = Modifier.width(64.dp)) // empty cell matching grid
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val isAlreadyApplied = current.id == target.id

                        PrimaryButton(
                            text = if (isAlreadyApplied) "Currently Applied" else "Apply '${target.name}' Profile",
                            enabled = !isAlreadyApplied,
                            onClick = { viewModel.handleEvent(PreviewUiEvent.ShowApplyConfirmation(true)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MockAppCell(
    icon: ImageVector,
    label: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(64.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
