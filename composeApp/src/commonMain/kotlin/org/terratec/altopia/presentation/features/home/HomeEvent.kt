package org.terratec.altopia.presentation.features.home

sealed interface HomeEvent {
    data object NavigateToPayments : HomeEvent
    data class NavigateToExpenseDetail(val categoryId: String) : HomeEvent
    data class ShowSnack(val message: String) : HomeEvent
}
