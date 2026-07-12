package com.aneez.brandshift.domain.model

import androidx.annotation.DrawableRes

/**
 * Domain model representing a unique launcher identity.
 */
data class LauncherIdentity(
    val id: String,
    val name: String,
    @DrawableRes val iconResId: Int,
    val description: String,
    val aliasClassName: String
)
