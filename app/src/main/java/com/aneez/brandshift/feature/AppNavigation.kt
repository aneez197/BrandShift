package com.aneez.brandshift.feature

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aneez.brandshift.feature.about.AboutScreen
import com.aneez.brandshift.feature.about.AboutViewModel
import com.aneez.brandshift.feature.home.HomeScreen
import com.aneez.brandshift.feature.home.HomeViewModel
import com.aneez.brandshift.feature.identities.IdentitiesScreen
import com.aneez.brandshift.feature.identities.IdentitiesViewModel
import com.aneez.brandshift.feature.preview.PreviewScreen
import com.aneez.brandshift.feature.preview.PreviewViewModel
import com.aneez.brandshift.feature.settings.SettingsScreen
import com.aneez.brandshift.feature.settings.SettingsViewModel
import com.aneez.brandshift.feature.splash.SplashScreen
import com.aneez.brandshift.feature.splash.SplashViewModel

/**
 * Supported screen routes.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Identities : Screen("identities")
    object Preview : Screen("preview/{identityId}") {
        fun createRoute(identityId: String) = "preview/$identityId"
    }
    object Settings : Screen("settings")
    object About : Screen("about")
}
/**
 * Configuration options for application navigation flow.
 */
object NavigationConfig {
    /**
     * Whether splash screen and home screen flow is enabled.
     * Set to false to launch directly into the Identities screen.
     */
    const val ENABLE_SPLASH = false
}

/**
 * Application Navigation Controller composing screens logic and ViewModel scopes.
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val startDestination = if (NavigationConfig.ENABLE_SPLASH) {
        Screen.Splash.route
    } else {
        Screen.Identities.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Reserved for future onboarding flow
        // SplashScreen and HomeScreen are temporarily disabled.
        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToIdentities = {
                    navController.navigate(Screen.Identities.route)
                },
                onNavigateToPreview = { identityId ->
                    navController.navigate(Screen.Preview.createRoute(identityId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                }
            )
        }
        
        composable(Screen.Identities.route) {
            val viewModel: IdentitiesViewModel = hiltViewModel()
            IdentitiesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPreview = { identityId ->
                    navController.navigate(Screen.Preview.createRoute(identityId))
                }
            )
        }
        
        composable(
            route = Screen.Preview.route,
            arguments = listOf(navArgument("identityId") { type = NavType.StringType })
        ) { backStackEntry ->
            val identityId = backStackEntry.arguments?.getString("identityId") ?: ""
            val viewModel: PreviewViewModel = hiltViewModel()
            PreviewScreen(
                identityId = identityId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }
        
        composable(Screen.About.route) {
            val viewModel: AboutViewModel = hiltViewModel()
            AboutScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
