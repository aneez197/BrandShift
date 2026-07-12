package com.aneez.brandshift.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aneez.brandshift.core.utils.Logger
import com.aneez.brandshift.domain.model.AppPreferences
import com.aneez.brandshift.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source interacting with Jetpack DataStore Preferences.
 */
@Singleton
class PreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val CURRENT_IDENTITY_ID = stringPreferencesKey("current_identity_id")
    }

    val appPreferences: Flow<AppPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Logger.e("PreferencesDataSource", "Failed reading datastore preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeStr = preferences[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeStr)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }
            val dynamicColors = preferences[Keys.DYNAMIC_COLORS] ?: true
            val currentIdentityId = preferences[Keys.CURRENT_IDENTITY_ID] ?: "brandshift"

            AppPreferences(themeMode, dynamicColors, currentIdentityId)
        }

    suspend fun saveCurrentIdentityId(identityId: String) {
        dataStore.edit { preferences ->
            preferences[Keys.CURRENT_IDENTITY_ID] = identityId
        }
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun updateDynamicColors(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.DYNAMIC_COLORS] = enabled
        }
    }
}
