package com.aneez.brandshift.data.repository

import com.aneez.brandshift.core.utils.LauncherManager
import com.aneez.brandshift.data.datasource.PreferencesDataSource
import com.aneez.brandshift.domain.model.AppPreferences
import com.aneez.brandshift.domain.model.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [IdentityRepositoryImpl].
 */
class IdentityRepositoryImplTest {

    private lateinit var preferencesDataSource: PreferencesDataSource
    private lateinit var launcherManager: LauncherManager
    private lateinit var repository: IdentityRepositoryImpl

    @Before
    fun setUp() {
        preferencesDataSource = mock()
        launcherManager = mock()
        repository = IdentityRepositoryImpl(preferencesDataSource, launcherManager)
    }

    @Test
    fun getIdentities_emitsAllEightProfiles() = runTest {
        val list = repository.getIdentities().first()
        assertEquals(8, list.size)
        assertEquals("brandshift", list[0].id)
        assertEquals("devx", list[1].id)
    }

    @Test
    fun getCurrentIdentity_resolvesCurrentIdentityMatchingDataStore() = runTest {
        val preferences = AppPreferences(ThemeMode.SYSTEM, true, "devx")
        whenever(preferencesDataSource.appPreferences).thenReturn(flowOf(preferences))
        whenever(launcherManager.getCurrentEnabledAlias()).thenReturn("com.aneez.brandshift.MainActivityAliasDevX")

        val current = repository.getCurrentIdentity().first()

        assertEquals("devx", current.id)
    }

    @Test
    fun getCurrentIdentity_recoversFromOutOfSyncSystemStatus() = runTest {
        // DataStore indicates brandshift but system launcher runs devx alias
        val preferences = AppPreferences(ThemeMode.SYSTEM, true, "brandshift")
        whenever(preferencesDataSource.appPreferences).thenReturn(flowOf(preferences))
        whenever(launcherManager.getCurrentEnabledAlias()).thenReturn("com.aneez.brandshift.MainActivityAliasDevX")

        val current = repository.getCurrentIdentity().first()

        // It must recover the active devx identity and align storage keys
        assertEquals("devx", current.id)
        verify(preferencesDataSource).saveCurrentIdentityId("devx")
    }

    @Test
    fun applyIdentity_triggersSystemUpdateAndSavesProfile() = runTest {
        val targetAlias = "com.aneez.brandshift.MainActivityAliasDevX"
        whenever(launcherManager.applyIdentity(targetAlias, false)).thenReturn(Result.success(Unit))

        val result = repository.applyIdentity(targetAlias, killApp = false)

        assertTrue(result.isSuccess)
        verify(launcherManager).applyIdentity(targetAlias, false)
        verify(preferencesDataSource).saveCurrentIdentityId("devx")
    }
}
