package org.terratec.altopia.presentation.features.admindashboard

sealed interface AdminDashboardEvent {
    data object NavigateToUnits : AdminDashboardEvent
    data object NavigateToUsers : AdminDashboardEvent
    data object NavigateToDistribution : AdminDashboardEvent
    data object NavigateToExpenses : AdminDashboardEvent
    data object NavigateToTransactions : AdminDashboardEvent
}
