package org.terratec.altopia.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardStatsDto(
    @SerialName("collection_amount") val collectionAmount: Double,
    @SerialName("collection_target") val collectionTarget: Double,
    @SerialName("pending_validations") val pendingValidations: Int,
    @SerialName("overdue_amount") val overdueAmount: Double,
    @SerialName("overdue_count") val overdueCount: Int,
    @SerialName("current_period_name") val currentPeriodName: String?,
    @SerialName("period_status") val periodStatus: String?
)
