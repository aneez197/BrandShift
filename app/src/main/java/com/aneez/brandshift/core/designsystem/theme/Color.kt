package com.aneez.brandshift.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// Primary Indigo colors
val PrimaryLight = Color(0xFF4F46E5)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE0E7FF)
val OnPrimaryContainerLight = Color(0xFF1E1B4B)

// Secondary/Accent Cyan colors
val SecondaryLight = Color(0xFF06B6D4)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFCFFAFE)
val OnSecondaryContainerLight = Color(0xFF083344)

// Neutral colors
val BackgroundLight = Color(0xFFF8FAFC)
val OnBackgroundLight = Color(0xFF0F172A)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF0F172A)
val SurfaceVariantLight = Color(0xFFF1F5F9)
val OnSurfaceVariantLight = Color(0xFF475569)
val OutlineLight = Color(0xFFCBD5E1)

// Dark Theme Colors
val PrimaryDark = Color(0xFF818CF8)
val OnPrimaryDark = Color(0xFF1E1B4B)
val PrimaryContainerDark = Color(0xFF312E81)
val OnPrimaryContainerDark = Color(0xFFE0E7FF)

val SecondaryDark = Color(0xFF22D3EE)
val OnSecondaryDark = Color(0xFF083344)
val SecondaryContainerDark = Color(0xFF155E75)
val OnSecondaryContainerDark = Color(0xFFCFFAFE)

val BackgroundDark = Color(0xFF0F172A)
val OnBackgroundDark = Color(0xFFF8FAFC)
val SurfaceDark = Color(0xFF1E293B)
val OnSurfaceDark = Color(0xFFF8FAFC)
val SurfaceVariantDark = Color(0xFF334155)
val OnSurfaceVariantDark = Color(0xFF94A3B8)
val OutlineDark = Color(0xFF475569)

// Identity Specific Gradients (Pairs of Start and End colors)
object BrandGradients {
    val BrandShift = listOf(Color(0xFF4F46E5), Color(0xFF06B6D4))
    val DevX = listOf(Color(0xFF1E293B), Color(0xFF64748B))
    val GameBox = listOf(Color(0xFF0F172A), Color(0xFF10B981))
    val OfficeMate = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))
    val QuickNotes = listOf(Color(0xFF78350F), Color(0xFFF59E0B))
    val Calculator = listOf(Color(0xFF171717), Color(0xFF525252))
    val MusicPlayer = listOf(Color(0xFF581C87), Color(0xFFEC4899))
    val Camera = listOf(Color(0xFF064E3B), Color(0xFF14B8A6))

    fun getGradientForId(id: String): List<Color> {
        return when (id) {
            "brandshift" -> BrandShift
            "devx" -> DevX
            "gamebox" -> GameBox
            "officemate" -> OfficeMate
            "quicknotes" -> QuickNotes
            "calculator" -> Calculator
            "musicplayer" -> MusicPlayer
            "camera" -> Camera
            else -> BrandShift
        }
    }
}
