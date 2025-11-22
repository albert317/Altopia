package org.terratec.altopia.domain.model

data class Video(
    val id: Long,
    val createdAt: String,
    val videoLink: String,
    val isFinished: Boolean?
)
