package org.terratec.altopia.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.terratec.altopia.data.remote.api.ExpenseApiService
import org.terratec.altopia.data.remote.api.ReceiptApiService

class HomeViewModel(
    private val receiptService: ReceiptApiService,
    private val expenseService: ExpenseApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeContract.UiState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<HomeContract.Event>()
    val event = _event.receiveAsFlow()

    init {
        loadData()
    }

    fun setIntent(intent: HomeContract.Intent) {
        when (intent) {
            HomeContract.Intent.RefreshData -> loadData()
            HomeContract.Intent.PayReceipt -> handlePayReceipt()
            is HomeContract.Intent.ViewExpenseDetails -> handleViewExpenseDetails(intent.categoryId)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Fetch in parallel ideally, but sequential for simplicity
                val debtSummary = receiptService.getDebtSummary()
                val lastReceipt = receiptService.getLastReceipt()
                val expenses = expenseService.getBuildingExpenses()

                val receiptSummary = lastReceipt?.let {
                    HomeContract.ReceiptSummary(
                        id = it.id,
                        periodName = it.period,
                        dueDate = it.dueDate,
                        status = it.status,
                        maintenanceAmount = it.subtotalMaintenance,
                        waterAmount = it.subtotalServices,
                        totalAmount = it.total
                    )
                }

                val expenseCategories = expenses.map {
                    HomeContract.ExpenseCategory(
                        id = it.id,
                        name = it.category,
                        amount = it.amount,
                        trend = HomeContract.Trend.STABLE // Mock trend
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userName = "Albert Montes", // Mock name, ideally from UserSession
                    unitCode = "A-302", // Mock unit
                    totalDebt = debtSummary.totalDebt,
                    isDebtOverdue = debtSummary.isOverdue,
                    lastReceipt = receiptSummary,
                    buildingExpenses = expenseCategories.take(3) // Show top 3
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar datos: ${e.message}"
                )
            }
        }
    }

    private fun handlePayReceipt() {
        viewModelScope.launch {
            _event.send(HomeContract.Event.ShowSnack("Funcionalidad de pago pronto disponible"))
            // logic to navigate to payments...
             _event.send(HomeContract.Event.NavigateToPayments)
        }
    }

    private fun handleViewExpenseDetails(categoryId: String) {
        viewModelScope.launch {
             _event.send(HomeContract.Event.NavigateToExpenseDetail(categoryId))
        }
    }
}
