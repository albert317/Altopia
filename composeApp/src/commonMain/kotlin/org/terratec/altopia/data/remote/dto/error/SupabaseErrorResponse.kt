package org.terratec.altopia.data.remote.dto.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseErrorResponse(
    @SerialName("code") val code: Int? = null,
    @SerialName("error_code") val errorCode: String? = null,
    @SerialName("msg") val msg: String? = null,
    @SerialName("error") val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null
)
