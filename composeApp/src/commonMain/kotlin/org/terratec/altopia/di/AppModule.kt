package org.terratec.altopia.di

import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val appModule = module {
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
    
    single<org.terratec.altopia.domain.repository.UserRepository> {
        org.terratec.altopia.data.repository.UserRepositoryImpl(get())
    }
    
    factory { org.terratec.altopia.domain.usecase.GetUserUseCase(get()) }
    factory { org.terratec.altopia.domain.usecase.GetVideosUseCase(get()) }
    
    single { org.terratec.altopia.presentation.viewmodel.UserViewModel(get(), get()) }
}
