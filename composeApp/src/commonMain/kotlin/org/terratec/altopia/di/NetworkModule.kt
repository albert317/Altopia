package org.terratec.altopia.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.terratec.altopia.data.remote.config.NetworkConfig
import org.terratec.altopia.data.remote.logger.KtorLogger

/**
 * Koin module for network configuration.
 * Provides pre-configured HttpClient instances for different backends.
 */
val networkModule = module {

    /**
     * Supabase HttpClient - Pre-configured for Supabase REST API
     * Includes authentication headers and base URL
     */
    single(named("supabase")) {
        HttpClient {
            // Base URL configuration
            defaultRequest {
                url(NetworkConfig.Supabase.BASE_URL)
                contentType(ContentType.Application.Json)

                // Supabase authentication headers
                header("apikey", NetworkConfig.Supabase.API_KEY)
                header("Authorization", "Bearer ${NetworkConfig.Supabase.API_KEY}")
            }

            // JSON content negotiation
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            // HTTP Logging
            if (NetworkConfig.Logging.ENABLED) {
                install(Logging) {
                    logger = KtorLogger.supabase()
                    level = when (NetworkConfig.Logging.LEVEL) {
                        NetworkConfig.Logging.Level.ALL -> LogLevel.ALL
                        NetworkConfig.Logging.Level.HEADERS -> LogLevel.HEADERS
                        NetworkConfig.Logging.Level.BODY -> LogLevel.BODY
                        NetworkConfig.Logging.Level.INFO -> LogLevel.INFO
                        NetworkConfig.Logging.Level.NONE -> LogLevel.NONE
                    }
                    // Sanitize sensitive headers
                    NetworkConfig.Logging.SANITIZED_HEADERS.forEach { headerName ->
                        sanitizeHeader { header -> header == headerName }
                    }
                }
            }
        }
    }

    /**
     * Supabase Auth HttpClient - Pre-configured for Supabase Auth API
     * Used for authentication endpoints (login, logout, refresh token)
     * Does NOT include Authorization header by default (added manually per request)
     */
    single(named("supabaseAuth")) {
        HttpClient {
            // Base URL configuration
            defaultRequest {
                url(NetworkConfig.Supabase.AUTH_BASE_URL)
                contentType(ContentType.Application.Json)

                // Only apikey header, Authorization is added per request
                header("apikey", NetworkConfig.Supabase.API_KEY)
            }

            // JSON content negotiation
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            // HTTP Logging
            if (NetworkConfig.Logging.ENABLED) {
                install(Logging) {
                    logger = KtorLogger.supabase()
                    level = when (NetworkConfig.Logging.LEVEL) {
                        NetworkConfig.Logging.Level.ALL -> LogLevel.ALL
                        NetworkConfig.Logging.Level.HEADERS -> LogLevel.HEADERS
                        NetworkConfig.Logging.Level.BODY -> LogLevel.BODY
                        NetworkConfig.Logging.Level.INFO -> LogLevel.INFO
                        NetworkConfig.Logging.Level.NONE -> LogLevel.NONE
                    }
                    // Sanitize sensitive headers
                    NetworkConfig.Logging.SANITIZED_HEADERS.forEach { headerName ->
                        sanitizeHeader { header -> header == headerName }
                    }
                }
            }
        }
    }

    /**
     * JSONPlaceholder HttpClient - Pre-configured for JSONPlaceholder API
     * Used for testing/demo purposes
     */
    single(named("jsonPlaceholder")) {
        HttpClient {
            // Base URL configuration
            defaultRequest {
                url(NetworkConfig.JsonPlaceholder.BASE_URL)
                contentType(ContentType.Application.Json)
            }

            // JSON content negotiation
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            // HTTP Logging
            if (NetworkConfig.Logging.ENABLED) {
                install(Logging) {
                    logger = KtorLogger.jsonPlaceholder()
                    level = when (NetworkConfig.Logging.LEVEL) {
                        NetworkConfig.Logging.Level.ALL -> LogLevel.ALL
                        NetworkConfig.Logging.Level.HEADERS -> LogLevel.HEADERS
                        NetworkConfig.Logging.Level.BODY -> LogLevel.BODY
                        NetworkConfig.Logging.Level.INFO -> LogLevel.INFO
                        NetworkConfig.Logging.Level.NONE -> LogLevel.NONE
                    }
                }
            }
        }
    }
}
