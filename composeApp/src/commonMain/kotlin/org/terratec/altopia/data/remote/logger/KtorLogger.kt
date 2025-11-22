package org.terratec.altopia.data.remote.logger

import io.ktor.client.plugins.logging.Logger

/**
 * Custom Ktor Logger for HTTP client.
 * Provides formatted console output for debugging API requests and responses.
 *
 * @param tag - Tag to identify the API source (e.g., "SUPABASE", "JSONPlaceholder")
 */
class KtorLogger(private val tag: String) : Logger {

    override fun log(message: String) {
        // Format the log message with the API tag for easy identification
        println("🌐 [$tag] $message")
    }

    companion object {
        /**
         * Creates a logger for Supabase API
         */
        fun supabase() = KtorLogger("SUPABASE")

        /**
         * Creates a logger for JSONPlaceholder API
         */
        fun jsonPlaceholder() = KtorLogger("JSONPlaceholder")
    }
}
