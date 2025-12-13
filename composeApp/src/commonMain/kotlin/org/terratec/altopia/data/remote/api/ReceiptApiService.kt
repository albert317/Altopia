package org.terratec.altopia.data.remote.api

import kotlinx.serialization.Serializable

interface ReceiptApiService {
    suspend fun getLastReceipt(): ReceiptResponse?
    suspend fun getDebtSummary(): DebtSummaryResponse
}

@Serializable
data class ReceiptResponse(
    val id: String,
    val period: String,
    val dueDate: String,
    val status: String,
    val subtotalMaintenance: Double,
    val subtotalServices: Double,
    val total: Double
)

@Serializable
data class DebtSummaryResponse(
    val totalDebt: Double,
    val isOverdue: Boolean
)
