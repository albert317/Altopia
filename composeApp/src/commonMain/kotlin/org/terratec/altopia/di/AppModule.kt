package org.terratec.altopia.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.terratec.altopia.data.mapper.UserMapper
import org.terratec.altopia.data.remote.api.UserApiService
import org.terratec.altopia.data.remote.api.UserApiServiceImpl
import org.terratec.altopia.data.remote.api.VideoApiService
import org.terratec.altopia.data.remote.api.VideoApiServiceImpl
import org.terratec.altopia.data.remote.api.DashboardApiService
import org.terratec.altopia.data.remote.api.DashboardApiServiceImpl
import org.terratec.altopia.data.remote.datasource.UserRemoteDataSource
import org.terratec.altopia.data.remote.datasource.UserRemoteDataSourceImpl
import org.terratec.altopia.data.remote.datasource.VideoRemoteDataSource
import org.terratec.altopia.data.remote.datasource.VideoRemoteDataSourceImpl
import org.terratec.altopia.data.remote.datasource.DashboardRemoteDataSource
import org.terratec.altopia.data.remote.datasource.DashboardRemoteDataSourceImpl
import org.terratec.altopia.data.repository.UserRepositoryImpl
import org.terratec.altopia.data.repository.DashboardRepositoryImpl
import org.terratec.altopia.domain.repository.UserRepository
import org.terratec.altopia.domain.repository.DashboardRepository
import org.terratec.altopia.presentation.features.forgotpassword.ForgotPasswordViewModel
import org.terratec.altopia.presentation.features.login.LoginViewModel
import org.terratec.altopia.presentation.features.resetpassword.ResetPasswordViewModel
import org.terratec.altopia.presentation.features.home.HomeViewModel
import org.terratec.altopia.presentation.features.splash.SplashViewModel
import org.terratec.altopia.data.remote.api.ReceiptApiService
import org.terratec.altopia.data.remote.api.ReceiptApiServiceImpl
import org.terratec.altopia.data.remote.api.ExpenseApiService
import org.terratec.altopia.data.remote.api.ExpenseApiServiceImpl
import org.terratec.altopia.domain.usecase.GetUserRolesUseCase
import org.terratec.altopia.domain.usecase.GetVideosUseCase
import org.terratec.altopia.domain.usecase.auth.GetAuthSessionLocalUseCase
import org.terratec.altopia.domain.usecase.user.GetPersonUseCase
import org.terratec.altopia.domain.usecase.user.GetUserUseCase
import org.terratec.altopia.domain.usecase.user.GetUserProfilesUseCase
import org.terratec.altopia.domain.usecase.GetDashboardStatsUseCase
import org.terratec.altopia.presentation.features.profile_selection.ProfileSelectionViewModel
import org.terratec.altopia.presentation.navigation.DeepLinkHandler
import org.terratec.altopia.presentation.viewmodel.UserViewModel
import org.terratec.altopia.presentation.features.admindashboard.AdminDashboardViewModel

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
        UserApiServiceImpl(get(named("supabase")))
    }
    single<VideoApiService> {
        VideoApiServiceImpl(get(named("supabase")))
    }
    single<ReceiptApiService> { ReceiptApiServiceImpl() }
    single<ReceiptApiService> { ReceiptApiServiceImpl() }
    single<ExpenseApiService> { ExpenseApiServiceImpl() }
    single<DashboardApiService> { 
        DashboardApiServiceImpl(get(named("supabase"))) 
    }

    // ===== Data Sources =====
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
    single<VideoRemoteDataSource> { VideoRemoteDataSourceImpl(get()) }
    single<DashboardRemoteDataSource> { DashboardRemoteDataSourceImpl(get()) }

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
    single<UserRepository> {
        UserRepositoryImpl(
            userRemoteDataSource = get(),
            videoRemoteDataSource = get(),
            mapper = get()
        )
    }
    
    single<DashboardRepository> {
        DashboardRepositoryImpl(get())
    }

    // ===== Use Cases =====
    factory { GetVideosUseCase(get()) }
    factory { GetUserRolesUseCase(get()) }
    factory { GetAuthSessionLocalUseCase(get()) }
    factory { GetPersonUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { GetUserProfilesUseCase(get()) }
    factory { GetDashboardStatsUseCase(get()) }


    // ===== ViewModels =====
    viewModelOf(::UserViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::DeepLinkHandler)
    
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::ResetPasswordViewModel)
    viewModelOf(::ResetPasswordViewModel)
    viewModelOf(::ProfileSelectionViewModel)
    viewModelOf(::AdminDashboardViewModel)
}
