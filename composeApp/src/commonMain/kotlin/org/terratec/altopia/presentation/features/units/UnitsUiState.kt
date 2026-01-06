package org.terratec.altopia.presentation.features.units

import org.terratec.altopia.domain.model.Block
import org.terratec.altopia.domain.model.PropertyUnit

data class UnitsUiState(
    val isLoading: Boolean = false,
    val units: List<PropertyUnit> = emptyList(),
    val blocks: List<Block> = emptyList(),
    val error: String? = null,
    
    // Dialog State
    val isFormVisible: Boolean = false,
    val isEditing: Boolean = false,
    val selectedUnitId: String? = null,
    
    // Form Fields
    val selectedBlockId: String = "",
    val code: String = "",
    val floor: String = "", // String for input, convert to Int
    val areaCoefficient: String = "", // String for input, convert to Double
    val usageType: String = "VIVIENDA", // Default
    
    // Form Validation
    val isFormValid: Boolean = false
)
