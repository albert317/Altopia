package org.terratec.altopia.presentation.features.admindashboard

sealed interface AdminDashboardIntent {
    data object NavigateToUnits : AdminDashboardIntent
    data object NavigateToUsers : AdminDashboardIntent
    data object NavigateToDistribution : AdminDashboardIntent
    data object NavigateToExpenses : AdminDashboardIntent
    data object NavigateToTransactions : AdminDashboardIntent
    data object RefreshDashboard : AdminDashboardIntent
}
