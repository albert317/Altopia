package org.terratec.altopia.presentation.features.units

sealed interface UnitsEvent {
    data class ShowToast(val message: String) : UnitsEvent
}
