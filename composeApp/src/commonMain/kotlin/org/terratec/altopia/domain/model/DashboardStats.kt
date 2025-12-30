package org.terratec.altopia.domain.model

data class DashboardStats(
    val collectionAmount: Double,
    val collectionTarget: Double,
    val pendingValidations: Int,
    val overdueAmount: Double,
    val overdueCount: Int,
    val currentPeriodName: String,
    val periodStatus: String
)
