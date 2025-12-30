package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.api.DashboardApiService
import org.terratec.altopia.data.remote.dto.DashboardStatsDto

class DashboardRemoteDataSourceImpl(
    private val apiService: DashboardApiService
) : DashboardRemoteDataSource {
    override suspend fun getDashboardStats(): Result<DashboardStatsDto> {
        return try {
            val result = apiService.getDashboardStats()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
