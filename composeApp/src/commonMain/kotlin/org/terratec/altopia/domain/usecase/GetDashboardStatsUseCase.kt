package org.terratec.altopia.domain.usecase

import org.terratec.altopia.domain.model.DashboardStats
import org.terratec.altopia.domain.repository.DashboardRepository

class GetDashboardStatsUseCase(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(): Result<DashboardStats> {
        return repository.getDashboardStats()
    }
}
