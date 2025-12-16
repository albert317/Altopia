package org.terratec.altopia.presentation.features.home

import org.terratec.altopia.data.remote.api.ExpenseApiService
import org.terratec.altopia.data.remote.api.ReceiptApiService
import org.terratec.altopia.domain.usecase.GetUserRolesUseCase
import org.terratec.altopia.domain.usecase.auth.GetAuthSessionLocalUseCase
import org.terratec.altopia.domain.usecase.user.GetPersonUseCase
import org.terratec.altopia.domain.usecase.user.GetUserUseCase
import org.terratec.altopia.presentation.util.executeTask
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

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.RefreshData -> loadData()
            HomeIntent.PayReceipt -> handlePayReceipt()
            is HomeIntent.ViewExpenseDetails -> handleViewExpenseDetails(intent.categoryId)
        }
    }

    private fun loadData() {
        setUiState { copy(isLoading = true, error = null) }

        executeTask(
            onSuccess = { result ->


            },
            onFailure = { e ->
                setUiState {
                    copy(
                        isLoading = false,
                        error = "Error al cargar datos: ${e.message}"
                    )
                }
            }
        ) {
            // Fetch in parallel ideally, but sequential for simplicity
            val debtSummary = receiptService.getDebtSummary()
            val lastReceipt = receiptService.getLastReceipt()
            val expenses = expenseService.getBuildingExpenses()
            val userId = getAuthSessionLocalUseCase().getOrNull()?.user?.id ?: ""
            val person = getPersonUseCase(userId).getOrNull() ?: throw Exception("No person found")
            val user = getUserUseCase(person.id).getOrNull() ?: throw Exception("No user found")
            val roles = getUserRolesUseCase(user.id.toString()).getOrThrow()

            // Return validation data tuple
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
