package org.terratec.altopia.domain.usecase.unit

import org.terratec.altopia.domain.repository.UnitRepository

class UpdateUnitUseCase(
    private val repository: UnitRepository
) {
    suspend operator fun invoke(
        id: String,
        bloqueId: String,
        codigo: String,
        piso: Int,
        coeficienteArea: Double,
        tipoUso: String
    ): Result<Unit> {
        return repository.updateUnit(id, bloqueId, codigo, piso, coeficienteArea, tipoUso)
    }
}
