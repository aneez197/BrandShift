package com.aneez.brandshift.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [LauncherManager].
 */
class LauncherManagerTest {

    private lateinit var aliasManager: AliasManager
    private lateinit var launcherManager: LauncherManager

    @Before
    fun setUp() {
        aliasManager = mock()
        launcherManager = LauncherManager(aliasManager)
    }

    @Test
    fun applyIdentity_withValidAlias_returnsSuccessAndEnablesIt() {
        val targetAlias = "com.aneez.brandshift.MainActivityAliasDevX"

        val result = launcherManager.applyIdentity(targetAlias, killApp = false)

        assertTrue(result.isSuccess)
        verify(aliasManager).setComponentEnabled(targetAlias, enabled = true, killApp = false)
        for (alias in LauncherManager.ALL_ALIASES) {
            if (alias != targetAlias) {
                verify(aliasManager).setComponentEnabled(alias, enabled = false, killApp = false)
            }
        }
    }

    @Test
    fun applyIdentity_withInvalidAlias_returnsFailure() {
        val targetAlias = "com.aneez.brandshift.MainActivityAliasUnknown"

        val result = launcherManager.applyIdentity(targetAlias, killApp = false)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun getCurrentEnabledAlias_returnsDefault_ifNoAliasIsEnabled() {
        whenever(aliasManager.isComponentEnabled(any(), any())).thenReturn(false)

        val enabled = launcherManager.getCurrentEnabledAlias()

        assertEquals(LauncherManager.DEFAULT_ALIAS, enabled)
    }

    @Test
    fun getCurrentEnabledAlias_returnsEnabledAlias_ifOneIsEnabled() {
        val targetAlias = "com.aneez.brandshift.MainActivityAliasDevX"
        whenever(aliasManager.isComponentEnabled(targetAlias, LauncherManager.DEFAULT_ALIAS)).thenReturn(true)

        val enabled = launcherManager.getCurrentEnabledAlias()

        assertEquals(targetAlias, enabled)
    }
}
