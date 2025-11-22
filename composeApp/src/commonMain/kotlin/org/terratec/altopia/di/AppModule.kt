package org.terratec.altopia.di

import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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

val appModule = module {
    // ===== Network Layer =====
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }
    
    // ===== API Services =====
    single<UserApiService> { UserApiServiceImpl(get()) }
    single<VideoApiService> { VideoApiServiceImpl(get()) }
    
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
}
