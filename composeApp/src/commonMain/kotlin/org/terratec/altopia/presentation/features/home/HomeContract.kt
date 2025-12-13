package org.terratec.altopia.presentation.features.home

import androidx.compose.runtime.Immutable

/**
 * Contract for the Home Feature
 */
object HomeContract {

    @Immutable
    data class UiState(
        val isLoading: Boolean = false,
        val userName: String = "",
        val unitCode: String = "",
        val totalDebt: Double = 0.0,
        val isDebtOverdue: Boolean = false,
        val lastReceipt: ReceiptSummary? = null,
        val buildingExpenses: List<ExpenseCategory> = emptyList(),
        val error: String? = null
    )

    sealed interface Intent {
        data object RefreshData : Intent
        data object PayReceipt : Intent
        data class ViewExpenseDetails(val categoryId: String) : Intent
    }

    sealed interface Event {
        data object NavigateToPayments : Event
        data class NavigateToExpenseDetail(val categoryId: String) : Event
        data class ShowSnack(val message: String) : Event
    }

    // -- UI Specific Models --
    @Immutable
    data class ReceiptSummary(
        val id: String,
        val periodName: String, // e.g., "Noviembre 2025"
        val dueDate: String,
        val status: String, // PENDIENTE, PAGADO
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
}
