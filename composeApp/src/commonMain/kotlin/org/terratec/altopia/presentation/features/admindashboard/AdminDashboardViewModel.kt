package org.terratec.altopia.presentation.features.admindashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.terratec.altopia.domain.usecase.GetDashboardStatsUseCase

class AdminDashboardViewModel(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    private val _event = Channel<AdminDashboardEvent>()
    val event = _event.receiveAsFlow()

    init {
        loadDashboardData()
    }

    fun setIntent(intent: AdminDashboardIntent) {
        when (intent) {
            AdminDashboardIntent.NavigateToUnits -> sendEvent(AdminDashboardEvent.NavigateToUnits)
            AdminDashboardIntent.NavigateToUsers -> sendEvent(AdminDashboardEvent.NavigateToUsers)
            AdminDashboardIntent.NavigateToDistribution -> sendEvent(AdminDashboardEvent.NavigateToDistribution)
            AdminDashboardIntent.NavigateToExpenses -> sendEvent(AdminDashboardEvent.NavigateToExpenses)
            AdminDashboardIntent.NavigateToTransactions -> sendEvent(AdminDashboardEvent.NavigateToTransactions)
            AdminDashboardIntent.RefreshDashboard -> loadDashboardData()
        }
    }

    private fun sendEvent(event: AdminDashboardEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getDashboardStatsUseCase()
                .onSuccess { stats ->
                     _uiState.update {
                        it.copy(
                            isLoading = false,
                            collectionAmount = stats.collectionAmount,
                            collectionTarget = stats.collectionTarget,
                            pendingValidations = stats.pendingValidations,
                            overdueAmount = stats.overdueAmount,
                            overdueCount = stats.overdueCount,
                            currentPeriod = stats.currentPeriodName,
                            periodStatus = stats.periodStatus
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = error.message ?: "Error al cargar datos"
                        ) 
                    }
                }
        }
    }
}
