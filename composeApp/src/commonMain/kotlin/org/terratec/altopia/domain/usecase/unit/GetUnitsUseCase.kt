package org.terratec.altopia.domain.usecase.unit

import org.terratec.altopia.domain.model.PropertyUnit
import org.terratec.altopia.domain.repository.UnitRepository

class GetUnitsUseCase(
    private val repository: UnitRepository
) {
    suspend operator fun invoke(): Result<List<PropertyUnit>> {
        return repository.getUnits()
    }
}
