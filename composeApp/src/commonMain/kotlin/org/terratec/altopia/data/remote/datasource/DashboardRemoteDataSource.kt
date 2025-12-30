package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.dto.DashboardStatsDto

interface DashboardRemoteDataSource {
    suspend fun getDashboardStats(): Result<DashboardStatsDto>
}
