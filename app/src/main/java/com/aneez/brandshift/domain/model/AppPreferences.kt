package com.aneez.brandshift.domain.model

/**
 * Domain representation of application settings and active profile status.
 */
data class AppPreferences(
    val themeMode: ThemeMode,
    val dynamicColors: Boolean,
    val currentIdentityId: String
)

/**
 * Supported display themes in the application.
 */
enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}
