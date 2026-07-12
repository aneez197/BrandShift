package com.aneez.brandshift.data.repository

import com.aneez.brandshift.R
import com.aneez.brandshift.core.utils.LauncherManager
import com.aneez.brandshift.data.datasource.PreferencesDataSource
import com.aneez.brandshift.domain.model.AppPreferences
import com.aneez.brandshift.domain.model.LauncherIdentity
import com.aneez.brandshift.domain.model.ThemeMode
import com.aneez.brandshift.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation exposing application themes and launcher identities data flows.
 */
@Singleton
class IdentityRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val launcherManager: LauncherManager
) : IdentityRepository {

    private val identities = listOf(
        LauncherIdentity(
            id = "brandshift",
            name = "BrandShift",
            iconResId = R.drawable.ic_launcher_brandshift_foreground,
            description = "Default launcher identity featuring a rocket theme.",
            aliasClassName = "com.aneez.brandshift.MainActivityAliasBrandShift"
        ),
        LauncherIdentity(
            id = "devx",
            name = "DevX",
            iconResId = R.drawable.ic_launcher_devx_foreground,
            description = "A sleek developer dashboard layout.",
            aliasClassName = "com.aneez.brandshift.MainActivityAliasDevX"
        ),
        LauncherIdentity(
            id = "gamebox",
            name = "GameBox",
            iconResId = R.drawable.ic_launcher_gamebox_foreground,
            description = "Unleash gaming performance styles.",
            aliasClassName = "com.aneez.brandshift.MainActivityAliasGameBox"
        ),
        LauncherIdentity(
            id = "officemate",
            name = "OfficeMate",
            iconResId = R.drawable.ic_launcher_officemate_foreground,
            description = "Professional productivity layout.",
            aliasClassName = "com.aneez.brandshift.MainActivityAliasOfficeMate"
        ),
        LauncherIdentity(
            id = "quicknotes",
            name = "Quick Notes",
            iconResId = R.drawable.ic_launcher_quicknotes_foreground,
            description = "Quick access notebook profile.",
            aliasClassName = "com.aneez.brandshift.MainActivityAliasQuickNotes"
        ),
        LauncherIdentity(
            id = "calculator",
            name = "Calculator",
            iconResId = R.drawable.ic_launcher_calculator_foreground,
            description = "Disguise application in stealth calculator style.",
            aliasClassName = "com.aneez.brandshift.MainActivityAliasCalculator"
        ),
        LauncherIdentity(
            id = "musicplayer",
            name = "Music Player",
            iconResId = R.drawable.ic_launcher_music_foreground,
            description = "Sleek music visual style.",
            aliasClassName = "com.aneez.brandshift.MainActivityAliasMusicPlayer"
        ),
        LauncherIdentity(
            id = "camera",
            name = "Camera",
            iconResId = R.drawable.ic_launcher_camera_foreground,
            description = "Photography camera lens design.",
            aliasClassName = "com.aneez.brandshift.MainActivityAliasCamera"
        )
    )

    override fun getIdentities(): Flow<List<LauncherIdentity>> = flowOf(identities)

    override fun getCurrentIdentity(): Flow<LauncherIdentity> {
        return preferencesDataSource.appPreferences.map { prefs ->
            val actualEnabledAlias = launcherManager.getCurrentEnabledAlias()
            val identityByAlias = identities.firstOrNull { it.aliasClassName == actualEnabledAlias }
            if (identityByAlias != null && identityByAlias.id != prefs.currentIdentityId) {
                // Align datastore if system status differs (e.g. after clearing app cache)
                saveCurrentIdentity(identityByAlias.id)
                identityByAlias
            } else {
                identities.firstOrNull { it.id == prefs.currentIdentityId } ?: identities.first()
            }
        }
    }

    override fun getAppPreferences(): Flow<AppPreferences> = preferencesDataSource.appPreferences

    override suspend fun saveCurrentIdentity(identityId: String) {
        preferencesDataSource.saveCurrentIdentityId(identityId)
    }

    override suspend fun applyIdentity(aliasClassName: String, killApp: Boolean): Result<Unit> {
        val result = launcherManager.applyIdentity(aliasClassName, killApp)
        if (result.isSuccess) {
            val identity = identities.firstOrNull { it.aliasClassName == aliasClassName }
            if (identity != null) {
                saveCurrentIdentity(identity.id)
            }
        }
        return result
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        preferencesDataSource.updateThemeMode(themeMode)
    }

    override suspend fun updateDynamicColors(enabled: Boolean) {
        preferencesDataSource.updateDynamicColors(enabled)
    }

    override suspend fun resetToDefault(): Result<Unit> {
        val defaultAlias = "com.aneez.brandshift.MainActivityAliasBrandShift"
        val result = applyIdentity(defaultAlias, killApp = false)
        if (result.isSuccess) {
            saveCurrentIdentity("brandshift")
            updateThemeMode(ThemeMode.SYSTEM)
            updateDynamicColors(true)
        }
        return result
    }
}
