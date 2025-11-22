package org.terratec.altopia

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform