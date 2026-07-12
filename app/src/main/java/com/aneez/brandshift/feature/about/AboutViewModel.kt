package com.aneez.brandshift.feature.about

import androidx.lifecycle.ViewModel
import com.aneez.brandshift.core.utils.PackageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel aggregating application compilation metadata and package details.
 */
@HiltViewModel
class AboutViewModel @Inject constructor(
    packageUtils: PackageUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AboutUiState(
            versionName = packageUtils.getVersionName(),
            activeLauncherPackage = packageUtils.getLauncherPackageName() ?: "System Default Launcher"
        )
    )
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()
}
