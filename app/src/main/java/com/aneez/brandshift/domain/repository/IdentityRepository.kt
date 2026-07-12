package com.aneez.brandshift.domain.repository

import com.aneez.brandshift.domain.model.AppPreferences
import com.aneez.brandshift.domain.model.LauncherIdentity
import com.aneez.brandshift.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract defining launcher identities management and preference manipulation.
 */
interface IdentityRepository {
    /**
     * Emits the complete list of predefined launcher identities.
     */
    fun getIdentities(): Flow<List<LauncherIdentity>>

    /**
     * Emits the current selected launcher identity model.
     */
    fun getCurrentIdentity(): Flow<LauncherIdentity>

    /**
     * Emits application preferences status flow.
     */
    fun getAppPreferences(): Flow<AppPreferences>

    /**
     * Persists the currently selected identity ID.
     */
    suspend fun saveCurrentIdentity(identityId: String)

    /**
     * Activates the specific launcher component alias and disables all others.
     */
    suspend fun applyIdentity(aliasClassName: String, killApp: Boolean): Result<Unit>

    /**
     * Updates the application theme setting.
     */
    suspend fun updateThemeMode(themeMode: ThemeMode)

    /**
     * Updates the dynamic colors status.
     */
    suspend fun updateDynamicColors(enabled: Boolean)

    /**
     * Resets the active launcher identity and setting back to the default profile.
     */
    suspend fun resetToDefault(): Result<Unit>
}
