package org.terratec.altopia.domain.model

data class Person(
    val id: String,
    val nombre: String,
    val apellidos: String,
    val dniRuc: String,
    val telefono: String?,
    val emailContacto: String?,
    val createdAt: String,
    val authId: String
)
