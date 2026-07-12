package com.aneez.brandshift.feature.home

import com.aneez.brandshift.core.dispatcher.DispatcherProvider
import com.aneez.brandshift.domain.model.AppPreferences
import com.aneez.brandshift.domain.model.LauncherIdentity
import com.aneez.brandshift.domain.model.ThemeMode
import com.aneez.brandshift.domain.repository.IdentityRepository
import com.aneez.brandshift.domain.usecase.GetCurrentIdentityUseCase
import com.aneez.brandshift.domain.usecase.ResetIdentityUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [HomeViewModel] testing initialization states and actions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    private lateinit var resetIdentityUseCase: ResetIdentityUseCase
    private lateinit var repository: IdentityRepository
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockIdentity = LauncherIdentity(
        id = "brandshift",
        name = "BrandShift",
        iconResId = 0,
        description = "Default test description",
        aliasClassName = "com.aneez.brandshift.MainActivityAliasBrandShift"
    )

    private val mockPreferences = AppPreferences(
        themeMode = ThemeMode.SYSTEM,
        dynamicColors = true,
        currentIdentityId = "brandshift"
    )

    @Before
    fun setUp() {
        getCurrentIdentityUseCase = mock()
        resetIdentityUseCase = mock()
        repository = mock()

        dispatcherProvider = object : DispatcherProvider {
            override val main: CoroutineDispatcher = testDispatcher
            override val io: CoroutineDispatcher = testDispatcher
            override val default: CoroutineDispatcher = testDispatcher
            override val unconfined: CoroutineDispatcher = testDispatcher
        }

        whenever(getCurrentIdentityUseCase()).thenReturn(flowOf(mockIdentity))
        whenever(repository.getAppPreferences()).thenReturn(flowOf(mockPreferences))
    }

    @Test
    fun init_loadsDataAndEmitsSuccessState() = runTest {
        viewModel = HomeViewModel(
            getCurrentIdentityUseCase,
            resetIdentityUseCase,
            repository,
            dispatcherProvider
        )

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        val success = state as HomeUiState.Success
        assertEquals(mockIdentity, success.currentIdentity)
        assertEquals(mockPreferences, success.appPreferences)
    }

    @Test
    fun resetToDefault_triggersResetUseCase_andEmitsToastEffect() = runTest {
        whenever(resetIdentityUseCase()).thenReturn(Result.success(Unit))
        viewModel = HomeViewModel(
            getCurrentIdentityUseCase,
            resetIdentityUseCase,
            repository,
            dispatcherProvider
        )

        viewModel.handleEvent(HomeUiEvent.ResetToDefault)

        verify(resetIdentityUseCase).invoke()
        val effect = viewModel.uiEffect.first()
        assertTrue(effect is HomeUiEffect.ShowToast)
    }
}
