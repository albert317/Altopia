package org.terratec.altopia.data.remote.api

import org.terratec.altopia.data.remote.dto.BlockDto
import org.terratec.altopia.data.remote.dto.CreateUnitRequest
import org.terratec.altopia.data.remote.dto.UnitDto

interface UnitApiService {
    suspend fun getUnits(): List<UnitDto>
    suspend fun getBlocks(): List<BlockDto>
    suspend fun createUnit(request: CreateUnitRequest)
    suspend fun updateUnit(id: String, request: CreateUnitRequest)
    suspend fun deleteUnit(id: String)
}
