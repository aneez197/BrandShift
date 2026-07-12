package com.aneez.brandshift.domain.usecase

import com.aneez.brandshift.domain.model.LauncherIdentity
import com.aneez.brandshift.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve the active launcher identity profile.
 */
class GetCurrentIdentityUseCase @Inject constructor(
    private val repository: IdentityRepository
) {
    operator fun invoke(): Flow<LauncherIdentity> {
        return repository.getCurrentIdentity()
    }
}
