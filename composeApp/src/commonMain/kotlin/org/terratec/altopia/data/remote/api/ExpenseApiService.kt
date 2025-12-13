package org.terratec.altopia.data.remote.api

import kotlinx.serialization.Serializable

interface ExpenseApiService {
    suspend fun getBuildingExpenses(): List<ExpenseResponse>
}

@Serializable
data class ExpenseResponse(
    val id: String,
    val category: String,
    val amount: Double
)
