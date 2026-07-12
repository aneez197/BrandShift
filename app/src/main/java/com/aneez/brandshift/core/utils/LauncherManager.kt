package com.aneez.brandshift.core.utils

import android.os.Handler
import android.os.Looper
import android.os.Process
import javax.inject.Inject
import javax.inject.Singleton

/**
 * High-level manager orchestrating launcher identity switching by coordinating
 * activity alias states and process recycling.
 */
@Singleton
class LauncherManager @Inject constructor(
    private val aliasManager: AliasManager
) {
    companion object {
        const val DEFAULT_ALIAS = "com.aneez.brandshift.MainActivityAliasBrandShift"
        
        val ALL_ALIASES = listOf(
            "com.aneez.brandshift.MainActivityAliasBrandShift",
            "com.aneez.brandshift.MainActivityAliasDevX",
            "com.aneez.brandshift.MainActivityAliasGameBox",
            "com.aneez.brandshift.MainActivityAliasOfficeMate",
            "com.aneez.brandshift.MainActivityAliasQuickNotes",
            "com.aneez.brandshift.MainActivityAliasCalculator",
            "com.aneez.brandshift.MainActivityAliasMusicPlayer",
            "com.aneez.brandshift.MainActivityAliasCamera"
        )
    }

    /**
     * Applies a new activity alias identity.
     * Enforces single-active-component constraint by enabling the target alias and
     * disabling all other registered aliases.
     *
     * @param newAliasClassName Absolute class name of target alias.
     * @param killApp Instantly kills the process to force-refresh launcher caching.
     */
    fun applyIdentity(newAliasClassName: String, killApp: Boolean = true): Result<Unit> {
        return try {
            if (newAliasClassName !in ALL_ALIASES) {
                return Result.failure(IllegalArgumentException("Invalid alias className: $newAliasClassName"))
            }

            Logger.d("LauncherManager", "Switching identity to $newAliasClassName")

            // Enable selected alias
            aliasManager.setComponentEnabled(newAliasClassName, enabled = true, killApp = false)

            // Disable all other aliases
            for (alias in ALL_ALIASES) {
                if (alias != newAliasClassName) {
                    aliasManager.setComponentEnabled(alias, enabled = false, killApp = false)
                }
            }

            if (killApp) {
                Logger.d("LauncherManager", "Restarting app process to commit launcher changes.")
                Handler(Looper.getMainLooper()).postDelayed({
                    Process.killProcess(Process.myPid())
                }, 500)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Logger.e("LauncherManager", "Failed to apply identity $newAliasClassName", e)
            Result.failure(e)
        }
    }

    /**
     * Returns the currently active launcher alias.
     */
    fun getCurrentEnabledAlias(): String {
        for (alias in ALL_ALIASES) {
            if (aliasManager.isComponentEnabled(alias, DEFAULT_ALIAS)) {
                return alias
            }
        }
        return DEFAULT_ALIAS
    }
}
