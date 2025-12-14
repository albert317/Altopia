package org.terratec.altopia.presentation.features.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.terratec.altopia.data.remote.api.ExpenseApiService
import org.terratec.altopia.data.remote.api.ReceiptApiService
import org.terratec.altopia.domain.usecase.GetUserRolesUseCase
import org.terratec.altopia.domain.usecase.auth.GetAuthSessionLocalUseCase
import org.terratec.altopia.domain.usecase.user.GetPersonUseCase
import org.terratec.altopia.domain.usecase.user.GetUserUseCase
import org.terratec.altopia.presentation.viewmodel.BaseViewModel

class HomeViewModel(
    private val receiptService: ReceiptApiService,
    private val expenseService: ExpenseApiService,
    private val getUserRolesUseCase: GetUserRolesUseCase,
    private val getAuthSessionLocalUseCase: GetAuthSessionLocalUseCase,
    private val getPersonUseCase: GetPersonUseCase,
    private val getUserUseCase: GetUserUseCase
) : BaseViewModel<HomeUiState, HomeIntent, HomeEvent>() {

    override fun createInitialState(): HomeUiState = HomeUiState()

    init {
        loadData()
    }

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.RefreshData -> loadData()
            HomeIntent.PayReceipt -> handlePayReceipt()
            is HomeIntent.ViewExpenseDetails -> handleViewExpenseDetails(intent.categoryId)
        }
    }

    private fun loadData() {
        setUiState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                // Fetch in parallel ideally, but sequential for simplicity
                val debtSummary = receiptService.getDebtSummary()
                val lastReceipt = receiptService.getLastReceipt()
                val expenses = expenseService.getBuildingExpenses()
                val userId = getAuthSessionLocalUseCase().getOrNull()?.user?.id ?: ""
                val person=getPersonUseCase(userId).getOrNull() ?: throw Exception("No person found")
                val user= getUserUseCase(person.id).getOrNull() ?: throw Exception("No user found")
                val roles = getUserRolesUseCase(user.id.toString())

                val receiptSummary = lastReceipt?.let {
                    ReceiptSummary(
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
                    ExpenseCategory(
                        id = it.id,
                        name = it.category,
                        amount = it.amount,
                        trend = Trend.STABLE // Mock trend
                    )
                }

                setUiState {
                    copy(
                        isLoading = false,
                        userName = "Albert Montes ${roles.toString()}", // Mock name, ideally from UserSession
                        unitCode = "A-302", // Mock unit
                        totalDebt = debtSummary.totalDebt,
                        isDebtOverdue = debtSummary.isOverdue,
                        lastReceipt = receiptSummary,
                        buildingExpenses = expenseCategories.take(3) // Show top 3
                    )
                }

            } catch (e: Exception) {
                setUiState {
                    copy(
                        isLoading = false,
                        error = "Error al cargar datos: ${e.message}"
                    )
                }
            }
        }
    }

    private fun handlePayReceipt() {
        setEvent(HomeEvent.ShowSnack("Funcionalidad de pago pronto disponible"))
        // logic to navigate to payments...
        setEvent(HomeEvent.NavigateToPayments)
    }

    private fun handleViewExpenseDetails(categoryId: String) {
        setEvent(HomeEvent.NavigateToExpenseDetail(categoryId))
    }
}
