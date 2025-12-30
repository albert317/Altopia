package org.terratec.altopia.data.remote.api

import org.terratec.altopia.data.remote.dto.DashboardStatsDto

interface DashboardApiService {
    suspend fun getDashboardStats(): DashboardStatsDto
}
