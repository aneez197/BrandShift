package com.aneez.brandshift.domain.usecase

import com.aneez.brandshift.domain.repository.IdentityRepository
import javax.inject.Inject

/**
 * Use case to reset the active identity to Default/BrandShift.
 */
class ResetIdentityUseCase @Inject constructor(
    private val repository: IdentityRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.resetToDefault()
    }
}
