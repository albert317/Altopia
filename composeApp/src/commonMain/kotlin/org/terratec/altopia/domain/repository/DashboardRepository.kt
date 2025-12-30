package org.terratec.altopia.domain.repository

import org.terratec.altopia.domain.model.DashboardStats

interface DashboardRepository {
    suspend fun getDashboardStats(): Result<DashboardStats>
}
