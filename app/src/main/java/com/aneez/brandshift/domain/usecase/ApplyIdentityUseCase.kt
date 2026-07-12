package com.aneez.brandshift.domain.usecase

import com.aneez.brandshift.domain.repository.IdentityRepository
import javax.inject.Inject

/**
 * Use case to apply a target launcher activity alias.
 */
class ApplyIdentityUseCase @Inject constructor(
    private val repository: IdentityRepository
) {
    suspend operator fun invoke(aliasClassName: String, killApp: Boolean = true): Result<Unit> {
        return repository.applyIdentity(aliasClassName, killApp)
    }
}
