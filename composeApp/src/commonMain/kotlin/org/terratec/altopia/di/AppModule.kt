package org.terratec.altopia.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.terratec.altopia.data.mapper.UserMapper
import org.terratec.altopia.data.remote.api.UserApiService
import org.terratec.altopia.data.remote.api.UserApiServiceImpl
import org.terratec.altopia.data.remote.api.VideoApiService
import org.terratec.altopia.data.remote.api.VideoApiServiceImpl
import org.terratec.altopia.data.remote.datasource.UserRemoteDataSource
import org.terratec.altopia.data.remote.datasource.UserRemoteDataSourceImpl
import org.terratec.altopia.data.remote.datasource.VideoRemoteDataSource
import org.terratec.altopia.data.remote.datasource.VideoRemoteDataSourceImpl
import org.terratec.altopia.data.repository.UserRepositoryImpl
import org.terratec.altopia.domain.repository.UserRepository
import org.terratec.altopia.presentation.viewmodel.HomeViewModel
import org.terratec.altopia.presentation.viewmodel.LoginViewModel
import org.terratec.altopia.presentation.viewmodel.SplashViewModel

/**
 * Koin module for application dependencies.
 * Provides repositories, use cases, and ViewModels.
 */
val appModule = module {
    // ===== Network Layer =====
    // Network configuration is now in NetworkModule

    // ===== API Services =====
    // Inject specific HttpClients for each backend
    single<UserApiService> {
        UserApiServiceImpl(get(named("jsonPlaceholder")))
    }
    single<VideoApiService> {
        VideoApiServiceImpl(get(named("supabase")))
    }

    // ===== Data Sources =====
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
    single<VideoRemoteDataSource> { VideoRemoteDataSourceImpl(get()) }

    // ===== Mappers =====
    single { UserMapper }

    // ===== Repositories =====
    single<UserRepository> {
        UserRepositoryImpl(
            userRemoteDataSource = get(),
            videoRemoteDataSource = get(),
            mapper = get()
        )
    }

    // ===== Use Cases =====
    factory { org.terratec.altopia.domain.usecase.GetUserUseCase(get()) }
    factory { org.terratec.altopia.domain.usecase.GetVideosUseCase(get()) }

    // ===== ViewModels =====
    single { org.terratec.altopia.presentation.viewmodel.UserViewModel(get(), get()) }
    viewModelOf(::SplashViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
}
