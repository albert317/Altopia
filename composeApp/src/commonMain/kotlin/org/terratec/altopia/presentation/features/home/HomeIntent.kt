package org.terratec.altopia.presentation.features.home

sealed interface HomeIntent {
    data object RefreshData : HomeIntent
    data object PayReceipt : HomeIntent
    data class ViewExpenseDetails(val categoryId: String) : HomeIntent
}
