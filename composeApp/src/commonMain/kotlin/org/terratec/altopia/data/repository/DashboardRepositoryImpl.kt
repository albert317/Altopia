package org.terratec.altopia.data.repository

import org.terratec.altopia.data.remote.datasource.DashboardRemoteDataSource
import org.terratec.altopia.domain.model.DashboardStats
import org.terratec.altopia.domain.repository.DashboardRepository

class DashboardRepositoryImpl(
    private val remoteDataSource: DashboardRemoteDataSource
) : DashboardRepository {
    override suspend fun getDashboardStats(): Result<DashboardStats> {
        return remoteDataSource.getDashboardStats().map { dto ->
            DashboardStats(
                collectionAmount = dto.collectionAmount,
                collectionTarget = dto.collectionTarget,
                pendingValidations = dto.pendingValidations,
                overdueAmount = dto.overdueAmount,
                overdueCount = dto.overdueCount,
                currentPeriodName = dto.currentPeriodName ?: "N/A",
                periodStatus = dto.periodStatus ?: "N/A"
            )
        }
    }
}
