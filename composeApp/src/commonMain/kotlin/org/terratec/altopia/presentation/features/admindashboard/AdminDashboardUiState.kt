package org.terratec.altopia.presentation.features.admindashboard

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val collectionAmount: Double = 0.0,
    val collectionTarget: Double = 0.0,
    val pendingValidations: Int = 0,
    val overdueAmount: Double = 0.0,
    val overdueCount: Int = 0,
    val currentPeriod: String = "",
    val periodStatus: String = "",
    val error: String? = null
) {
    val collectionPercentage: Int
        get() = if (collectionTarget > 0) ((collectionAmount / collectionTarget) * 100).toInt() else 0
}
