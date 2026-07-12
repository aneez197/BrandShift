package com.aneez.brandshift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.aneez.brandshift.core.designsystem.theme.BrandShiftTheme
import com.aneez.brandshift.domain.model.ThemeMode
import com.aneez.brandshift.domain.repository.IdentityRepository
import com.aneez.brandshift.feature.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main application activity hosting Compose navigation and collecting root theme parameters.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: IdentityRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs by repository.getAppPreferences().collectAsState(initial = null)

            val darkTheme = when (prefs?.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM, null -> isSystemInDarkTheme()
            }
            val dynamicColors = prefs?.dynamicColors ?: true

            BrandShiftTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColors
            ) {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}