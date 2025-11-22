package org.terratec.altopia.data.remote.config

/**
 * Centralized network configuration for the application.
 * Contains base URLs, API keys, and network settings.
 *
 * TODO: Move sensitive values like API keys to build config or secure storage in production.
 */
object NetworkConfig {

    /**
     * Supabase configuration
     */
    object Supabase {
        const val BASE_URL = "https://thvijyqaigfbfbbrmknx.supabase.co/rest/v1/"
        const val API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRodmlqeXFhaWdmYmZiYnJta254Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMxMzM4ODgsImV4cCI6MjA3ODcwOTg4OH0.CNeEfs62T930qQGsgXUzkdfjdRMV57V8aa59j1vVR4c"
    }

    /**
     * JSONPlaceholder configuration (for testing)
     */
    object JsonPlaceholder {
        const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    }

    /**
     * Network timeout settings (in milliseconds)
     */
    object Timeout {
        const val REQUEST_TIMEOUT = 30_000L
        const val CONNECT_TIMEOUT = 30_000L
        const val SOCKET_TIMEOUT = 30_000L
    }

    /**
     * Logging configuration
     */
    object Logging {
        /**
         * Enable/disable HTTP logging
         * Set to false in production builds for performance
         */
        const val ENABLED = true

        /**
         * Log level options:
         * - ALL: Logs everything (headers, body, and info)
         * - HEADERS: Logs request and response headers
         * - BODY: Logs request and response body
         * - INFO: Logs request and response lines (URL, method, status)
         * - NONE: Disables logging
         */
        enum class Level {
            ALL, HEADERS, BODY, INFO, NONE
        }

        /**
         * Current log level
         * Change to Level.NONE or Level.INFO for production
         */
        val LEVEL = Level.ALL

        /**
         * List of header names to sanitize (hide their values in logs)
         * Useful for hiding sensitive information like API keys and tokens
         */
        val SANITIZED_HEADERS = listOf(
            "apikey",
            "Authorization",
            "Bearer",
            "api-key",
            "x-api-key"
        )
    }
}
