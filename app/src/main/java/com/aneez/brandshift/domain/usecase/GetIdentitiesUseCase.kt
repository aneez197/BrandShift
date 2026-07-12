package com.aneez.brandshift.domain.usecase

import com.aneez.brandshift.domain.model.LauncherIdentity
import com.aneez.brandshift.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all predefined launcher identities.
 */
class GetIdentitiesUseCase @Inject constructor(
    private val repository: IdentityRepository
) {
    operator fun invoke(): Flow<List<LauncherIdentity>> {
        return repository.getIdentities()
    }
}
