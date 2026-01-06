package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.api.UnitApiService
import org.terratec.altopia.data.remote.dto.BlockDto
import org.terratec.altopia.data.remote.dto.CreateUnitRequest
import org.terratec.altopia.data.remote.dto.UnitDto

class UnitRemoteDataSourceImpl(
    private val apiService: UnitApiService
) : UnitRemoteDataSource {

    override suspend fun getUnits(): Result<List<UnitDto>> {
        return try {
            val result = apiService.getUnits()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBlocks(): Result<List<BlockDto>> {
        return try {
            val result = apiService.getBlocks()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUnit(request: CreateUnitRequest): Result<Unit> {
        return try {
            apiService.createUnit(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUnit(id: String, request: CreateUnitRequest): Result<Unit> {
        return try {
            apiService.updateUnit(id, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUnit(id: String): Result<Unit> {
        return try {
            apiService.deleteUnit(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
