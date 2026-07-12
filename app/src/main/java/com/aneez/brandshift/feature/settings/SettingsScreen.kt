package com.aneez.brandshift.feature.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import com.aneez.brandshift.core.designsystem.components.AppToolbar
import com.aneez.brandshift.core.designsystem.components.EmptyState
import com.aneez.brandshift.core.designsystem.components.LoadingView
import com.aneez.brandshift.core.designsystem.components.PrimaryButton
import com.aneez.brandshift.domain.model.ThemeMode
import kotlinx.coroutines.flow.collectLatest

/**
 * Screen containing general display configurations, theme overrides, and application reference links.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is SettingsUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is SettingsUiEffect.ShareAppContent -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.text)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share BrandShift"))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppToolbar(
                title = "Settings",
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
                SettingsUiState.Loading -> {
                    LoadingView(modifier = Modifier.fillMaxSize())
                }
                is SettingsUiState.Error -> {
                    EmptyState(
                        title = "Error Loading Settings",
                        description = state.message,
                        icon = Icons.Default.Warning,
                        actionButton = {
                            PrimaryButton(
                                text = "Retry",
                                onClick = { viewModel.handleEvent(SettingsUiEvent.LoadSettings) }
                            )
                        }
                    )
                }
                is SettingsUiState.Success -> {
                    val prefs = state.preferences

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Theme selection card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Theme Mode",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ThemeOptionRow(
                                        text = "System Default",
                                        selected = prefs.themeMode == ThemeMode.SYSTEM,
                                        onClick = { viewModel.handleEvent(SettingsUiEvent.UpdateThemeMode(ThemeMode.SYSTEM)) }
                                    )
                                    ThemeOptionRow(
                                        text = "Light Theme",
                                        selected = prefs.themeMode == ThemeMode.LIGHT,
                                        onClick = { viewModel.handleEvent(SettingsUiEvent.UpdateThemeMode(ThemeMode.LIGHT)) }
                                    )
                                    ThemeOptionRow(
                                        text = "Dark Theme",
                                        selected = prefs.themeMode == ThemeMode.DARK,
                                        onClick = { viewModel.handleEvent(SettingsUiEvent.UpdateThemeMode(ThemeMode.DARK)) }
                                    )
                                }
                            }
                        }

                        // Dynamic colors switch card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Dynamic Colors",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Apply Material You theme colors on Android 12+ devices.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = prefs.dynamicColors,
                                    onCheckedChange = { viewModel.handleEvent(SettingsUiEvent.UpdateDynamicColors(it)) }
                                )
                            }
                        }

                        // Action cards: About, Reset, Share, rate
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                SettingsNavigationItem(
                                    icon = Icons.Default.Info,
                                    title = "About BrandShift",
                                    subtitle = "App specs, architecture, and technology stack",
                                    onClick = onNavigateToAbout
                                )
                                SettingsNavigationItem(
                                    icon = Icons.Default.Share,
                                    title = "Share Application",
                                    subtitle = "Spread the word about dynamic branding",
                                    onClick = { viewModel.handleEvent(SettingsUiEvent.TriggerShareApp) }
                                )
                                SettingsNavigationItem(
                                    icon = Icons.Default.Star,
                                    title = "Rate App",
                                    subtitle = "Leave feedback on Google Play Store placeholder",
                                    onClick = {
                                        Toast.makeText(context, "Rate App clicked (Placeholder)", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                SettingsNavigationItem(
                                    icon = Icons.Default.Code,
                                    title = "GitHub Repository",
                                    subtitle = "View application open-source source files",
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/example/BrandShift"))
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }

                        // Reset launcher configuration card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Reset Icon Branding",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Revert to default app name and launcher settings.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.handleEvent(SettingsUiEvent.ResetBranding) }
                                        .padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset launcher branding components",
                                        tint = MaterialTheme.colorScheme.error
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

@Composable
fun ThemeOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
