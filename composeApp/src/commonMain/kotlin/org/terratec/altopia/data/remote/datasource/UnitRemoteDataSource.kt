package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.dto.BlockDto
import org.terratec.altopia.data.remote.dto.CreateUnitRequest
import org.terratec.altopia.data.remote.dto.UnitDto

interface UnitRemoteDataSource {
    suspend fun getUnits(): Result<List<UnitDto>>
    suspend fun getBlocks(): Result<List<BlockDto>>
    suspend fun createUnit(request: CreateUnitRequest): Result<Unit>
    suspend fun updateUnit(id: String, request: CreateUnitRequest): Result<Unit>
    suspend fun deleteUnit(id: String): Result<Unit>
}
