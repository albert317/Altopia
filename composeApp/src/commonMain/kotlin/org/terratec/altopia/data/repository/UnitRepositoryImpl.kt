package org.terratec.altopia.data.repository

import org.terratec.altopia.data.remote.datasource.UnitRemoteDataSource
import org.terratec.altopia.data.remote.dto.CreateUnitRequest
import org.terratec.altopia.domain.model.Block
import org.terratec.altopia.domain.model.PropertyUnit
import org.terratec.altopia.domain.repository.UnitRepository

class UnitRepositoryImpl(
    private val remoteDataSource: UnitRemoteDataSource
) : UnitRepository {

    override suspend fun getUnits(): Result<List<PropertyUnit>> {
        return remoteDataSource.getUnits().map { dtoList ->
            dtoList.map { dto ->
                PropertyUnit(
                    id = dto.id,
                    bloqueId = dto.bloqueId,
                    bloqueNombre = dto.bloqueNombre,
                    codigo = dto.codigo,
                    piso = dto.piso,
                    coeficienteArea = dto.coeficienteArea,
                    tipoUso = dto.tipoUso,
                    createdAt = dto.createdAt
                )
            }
        }
    }

    override suspend fun getBlocks(): Result<List<Block>> {
        return remoteDataSource.getBlocks().map { dtoList ->
            dtoList.map { dto ->
                Block(
                    id = dto.id,
                    nombre = dto.nombre
                )
            }
        }
    }

    override suspend fun createUnit(
        bloqueId: String,
        codigo: String,
        piso: Int,
        coeficienteArea: Double,
        tipoUso: String
    ): Result<Unit> {
        val request = CreateUnitRequest(
            bloqueId = bloqueId,
            codigo = codigo,
            piso = piso,
            coeficienteArea = coeficienteArea,
            tipoUso = tipoUso
        )
        return remoteDataSource.createUnit(request)
    }

    override suspend fun updateUnit(
        id: String,
        bloqueId: String,
        codigo: String,
        piso: Int,
        coeficienteArea: Double,
        tipoUso: String
    ): Result<Unit> {
        val request = CreateUnitRequest(
            bloqueId = bloqueId,
            codigo = codigo,
            piso = piso,
            coeficienteArea = coeficienteArea,
            tipoUso = tipoUso
        )
        return remoteDataSource.updateUnit(id, request)
    }

    override suspend fun deleteUnit(id: String): Result<Unit> {
        return remoteDataSource.deleteUnit(id)
    }
}
