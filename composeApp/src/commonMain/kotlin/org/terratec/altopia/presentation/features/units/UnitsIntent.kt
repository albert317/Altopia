package org.terratec.altopia.presentation.features.units

sealed interface UnitsIntent {
    data object LoadData : UnitsIntent
    data object OpenCreateDialog : UnitsIntent
    data class OpenEditDialog(val unitId: String) : UnitsIntent
    data object CloseDialog : UnitsIntent
    
    // Form Changes
    data class BlockSelected(val blockId: String) : UnitsIntent
    data class CodeChanged(val code: String) : UnitsIntent
    data class FloorChanged(val floor: String) : UnitsIntent
    data class CoefficientChanged(val coefficient: String) : UnitsIntent
    data class UsageTypeChanged(val type: String) : UnitsIntent
    
    data object SubmitForm : UnitsIntent
    data class DeleteUnit(val unitId: String) : UnitsIntent
}
