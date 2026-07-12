package com.aneez.brandshift

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Base Application class that initializes Dagger Hilt dependency injection.
 */
@HiltAndroidApp
class BrandShiftApplication : Application()
