package org.terratec.altopia.domain.model

data class UserProfile(
    val profileType: String,
    
    // Unit Data
    val unitId: String?,
    val unitCode: String?,
    val unitFloor: Int?,
    val unitAreaCoefficient: Double?,
    val unitUsageType: String?,
    
    // Block Data
    val blockId: String?,
    val blockName: String?,
    val blockDescription: String?,
    val condoId: String?
)
