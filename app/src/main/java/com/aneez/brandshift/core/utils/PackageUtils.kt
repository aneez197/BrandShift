package com.aneez.brandshift.core.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reusable utility to check package-related details and device details.
 */
@Singleton
class PackageUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Retrieves the application versionName.
     */
    fun getVersionName(): String {
        return try {
            val packageInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            Logger.e("PackageUtils", "Error retrieving version name", e)
            "1.0.0"
        }
    }

    /**
     * Identifies the package name of the active device Launcher.
     * Useful for troubleshooting OEM-specific launcher cache delays.
     */
    fun getLauncherPackageName(): String? {
        return try {
            val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                addCategory(android.content.Intent.CATEGORY_HOME)
            }
            val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.resolveActivity(
                    intent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            }
            resolveInfo?.activityInfo?.packageName
        } catch (e: Exception) {
            Logger.e("PackageUtils", "Error resolving active launcher", e)
            null
        }
    }
}
