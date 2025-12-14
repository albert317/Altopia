package org.terratec.altopia.presentation.features.home

import androidx.compose.runtime.Immutable

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val unitCode: String = "",
    val totalDebt: Double = 0.0,
    val isDebtOverdue: Boolean = false,
    val lastReceipt: ReceiptSummary? = null,
    val buildingExpenses: List<ExpenseCategory> = emptyList(),
    val error: String? = null
)

@Immutable
data class ReceiptSummary(
    val id: String,
    val periodName: String,
    val dueDate: String,
    val status: String,
    val maintenanceAmount: Double,
    val waterAmount: Double,
    val totalAmount: Double
)

@Immutable
data class ExpenseCategory(
    val id: String,
    val name: String,
    val amount: Double,
    val trend: Trend = Trend.STABLE
)

enum class Trend {
    UP, DOWN, STABLE
}
