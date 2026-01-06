package org.terratec.altopia.domain.usecase.unit

import org.terratec.altopia.domain.repository.UnitRepository

class CreateUnitUseCase(
    private val repository: UnitRepository
) {
    suspend operator fun invoke(
        bloqueId: String,
        codigo: String,
        piso: Int,
        coeficienteArea: Double,
        tipoUso: String
    ): Result<Unit> {
        return repository.createUnit(bloqueId, codigo, piso, coeficienteArea, tipoUso)
    }
}
