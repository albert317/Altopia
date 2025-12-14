package org.terratec.altopia.domain.model

data class BusinessError(
    override val message: String,
    val success: Boolean = false,
    val codeMessage: String
) : Throwable(message)
