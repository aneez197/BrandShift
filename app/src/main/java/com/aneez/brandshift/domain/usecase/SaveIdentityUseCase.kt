package com.aneez.brandshift.domain.usecase

import com.aneez.brandshift.domain.repository.IdentityRepository
import javax.inject.Inject

/**
 * Use case to persist target identity ID.
 */
class SaveIdentityUseCase @Inject constructor(
    private val repository: IdentityRepository
) {
    suspend operator fun invoke(identityId: String) {
        repository.saveCurrentIdentity(identityId)
    }
}
