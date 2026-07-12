package com.aneez.brandshift.core.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Low-level utility to manage state of `<activity-alias>` components.
 */
@Singleton
class AliasManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val packageManager: PackageManager = context.packageManager
    private val packageName: String = context.packageName

    /**
     * Set the enable state of an Activity Alias.
     * Use [PackageManager.DONT_KILL_APP] for [flags] if we don't want Android to kill the app immediately.
     */
    fun setComponentEnabled(aliasClassName: String, enabled: Boolean, killApp: Boolean = false) {
        val componentName = ComponentName(packageName, aliasClassName)
        val state = if (enabled) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        val flags = if (killApp) 0 else PackageManager.DONT_KILL_APP

        packageManager.setComponentEnabledSetting(
            componentName,
            state,
            flags
        )
    }

    /**
     * Check if an Activity Alias component is currently enabled.
     */
    fun isComponentEnabled(aliasClassName: String, defaultAliasClassName: String): Boolean {
        val componentName = ComponentName(packageName, aliasClassName)
        return try {
            val setting = packageManager.getComponentEnabledSetting(componentName)
            when (setting) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED -> false
                else -> aliasClassName == defaultAliasClassName
            }
        } catch (e: Exception) {
            Logger.e("AliasManager", "Error checking component status for $aliasClassName", e)
            aliasClassName == defaultAliasClassName
        }
    }
}
