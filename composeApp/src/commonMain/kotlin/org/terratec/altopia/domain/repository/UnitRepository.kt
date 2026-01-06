package org.terratec.altopia.domain.repository

import org.terratec.altopia.domain.model.Block
import org.terratec.altopia.domain.model.PropertyUnit

interface UnitRepository {
    suspend fun getUnits(): Result<List<PropertyUnit>>
    suspend fun getBlocks(): Result<List<Block>>
    suspend fun createUnit(bloqueId: String, codigo: String, piso: Int, coeficienteArea: Double, tipoUso: String): Result<Unit>
    suspend fun updateUnit(id: String, bloqueId: String, codigo: String, piso: Int, coeficienteArea: Double, tipoUso: String): Result<Unit>
    suspend fun deleteUnit(id: String): Result<Unit>
}
