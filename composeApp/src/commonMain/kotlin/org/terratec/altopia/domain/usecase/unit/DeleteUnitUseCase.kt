package org.terratec.altopia.domain.usecase.unit

import org.terratec.altopia.domain.repository.UnitRepository

class DeleteUnitUseCase(
    private val repository: UnitRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteUnit(id)
    }
}
