package org.terratec.altopia.domain.exception

data class BusinessError(
    override val message: String,
    val success: Boolean? = null,
    val httpCode: Int? = null,
    val codeMessage: String,
    val typeMessage: Int? = null,
) : Exception(message)
