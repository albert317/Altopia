package org.terratec.altopia.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    @SerialName("perfil_tipo") val profileType: String,
    
    // Unit Data
    @SerialName("unidad_id") val unitId: String? = null,
    @SerialName("unidad_codigo") val unitCode: String? = null,
    @SerialName("unidad_piso") val unitFloor: Int? = null,
    @SerialName("unidad_coeficiente_area") val unitAreaCoefficient: Double? = null,
    @SerialName("unidad_tipo_uso") val unitUsageType: String? = null,
    
    // Block Data
    @SerialName("bloque_id") val blockId: String? = null,
    @SerialName("bloque_nombre") val blockName: String? = null,
    @SerialName("bloque_descripcion") val blockDescription: String? = null,
    @SerialName("bloque_condominio_id") val condoId: String? = null
)
