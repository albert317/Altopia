package org.terratec.altopia.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.terratec.altopia.data.remote.dto.DashboardStatsDto
import org.terratec.altopia.data.remote.util.safeApiCall

class DashboardApiServiceImpl(
    private val httpClient: HttpClient
) : DashboardApiService {
    
    override suspend fun getDashboardStats(): DashboardStatsDto {
        return safeApiCall<List<DashboardStatsDto>> {
            // "rpc/get_admin_dashboard_stats"
            httpClient.post("rpc/get_admin_dashboard_stats") {
                contentType(ContentType.Application.Json)
            }
        }.first()
    }
}
