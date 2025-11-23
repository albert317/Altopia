package org.terratec.altopia.di

import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.terratec.altopia.data.local.datastore.createDataStore
import org.terratec.altopia.data.local.session.SessionManager
import org.terratec.altopia.data.local.session.SessionManagerImpl
import org.terratec.altopia.data.mapper.AuthMapper
import org.terratec.altopia.data.remote.api.AuthApiService
import org.terratec.altopia.data.remote.api.AuthApiServiceImpl
import org.terratec.altopia.data.repository.AuthRepositoryImpl
import org.terratec.altopia.domain.repository.AuthRepository
import org.terratec.altopia.domain.usecase.auth.GetCurrentUserUseCase
import org.terratec.altopia.domain.usecase.auth.IsAuthenticatedUseCase
import org.terratec.altopia.domain.usecase.auth.LoginUseCase
import org.terratec.altopia.domain.usecase.auth.LogoutUseCase

/**
 * Koin module for authentication dependencies.
 * Provides all auth-related components: DataStore, SessionManager, API service,
 * Repository, Mapper, and Use Cases.
 */
val authModule = module {
    
    // DataStore
    single { createDataStore() }
    
    // Session Manager
    single<SessionManager> { SessionManagerImpl(get()) }
    
    // Mapper
    single { AuthMapper() }
    
    // API Service
    single<AuthApiService> {
        AuthApiServiceImpl(
            httpClient = get<HttpClient>(named("supabaseAuth")),
            sessionManager = get()
        )
    }
    
    // Repository
    single<AuthRepository> {
        AuthRepositoryImpl(
            authApiService = get(),
            sessionManager = get(),
            mapper = get()
        )
    }
    
    // Use Cases
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { IsAuthenticatedUseCase(get()) }
}
