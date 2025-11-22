# Guía de Configuración: Koin + Ktor en Kotlin Multiplatform

Esta guía documenta la configuración de **Koin** (inyección de dependencias) y **Ktor** (cliente HTTP) en un proyecto Kotlin Multiplatform (KMP) con Compose Multiplatform, basada en la implementación del feature de lista de videos.

## 📋 Tabla de Contenidos

1. [Estructura del Proyecto](#estructura-del-proyecto)
2. [Dependencias](#dependencias)
3. [Configuración de Koin](#configuración-de-koin)
4. [Configuración de Ktor](#configuración-de-ktor)
5. [Implementación Ejemplo: Lista de Videos](#implementación-ejemplo-lista-de-videos)
6. [Configuración Específica por Plataforma](#configuración-específica-por-plataforma)
7. [Problemas Comunes y Soluciones](#problemas-comunes-y-soluciones)
8. [Mejoras Recomendadas](#mejoras-recomendadas)

---

## 📁 Estructura del Proyecto

El proyecto sigue **Clean Architecture** con la siguiente estructura:

```
composeApp/src/
├── commonMain/kotlin/org/terratec/altopia/
│   ├── domain/              # Lógica de negocio
│   │   ├── model/          # Entidades del dominio
│   │   ├── repository/     # Interfaces de repositorios
│   │   └── usecase/        # Casos de uso
│   ├── data/               # Implementación de datos
│   │   ├── remote/         # DTOs y API
│   │   ├── mapper/         # Conversión DTO ↔ Domain
│   │   └── repository/     # Implementación de repositorios
│   ├── presentation/       # Capa de presentación
│   │   ├── viewmodel/      # ViewModels
│   │   └── ui/             # Composables
│   └── di/                 # Inyección de dependencias
├── androidMain/            # Código específico de Android
├── iosMain/                # Código específico de iOS
└── commonTest/             # Tests compartidos
```

---

## 📦 Dependencias

### Versiones ([libs.versions.toml](file:///Users/albertmontesanccasi/androidProject/Altopia/gradle/libs.versions.toml))

```toml
[versions]
kotlin = "2.2.20"
koin = "4.0.0"
koin-compose = "4.0.0"
ktor = "3.0.0"
androidx-lifecycle = "2.9.5"
composeMultiplatform = "1.9.1"
```

### Plugins Necesarios

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)  // ⚠️ Requerido para Ktor
}
```

### Dependencias por Source Set

#### commonMain (Código Compartido)

```kotlin
commonMain.dependencies {
    // Compose
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.components.resources)
    
    // Lifecycle (para ViewModels)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewmodel)
    
    // Koin - Inyección de Dependencias
    implementation(libs.koin.core)              // Core de Koin
    implementation(libs.koin.compose)            // Integración con Compose
    implementation(libs.koin.compose.viewmodel)  // ⚠️ NO usar en iOS (ver nota)

    // Ktor - Cliente HTTP
    implementation(libs.ktor.client.core)                      // Core del cliente
    implementation(libs.ktor.client.content.negotiation)       // Serialización
    implementation(libs.ktor.serialization.kotlinx.json)       // JSON
}
```

**Propósito de cada dependencia:**

| Dependencia | Propósito |
|------------|-----------|
| `koin-core` | Motor principal de inyección de dependencias |
| `koin-compose` | Provee `koinInject()` para usar en Composables |
| `koin-compose-viewmodel` | Provee `koinViewModel()` - **NO recomendado para iOS** |
| `ktor-client-core` | API principal del cliente HTTP |
| `ktor-client-content-negotiation` | Permite serializar/deserializar automáticamente |
| `ktor-serialization-kotlinx-json` | Soporte para JSON usando kotlinx.serialization |

#### androidMain (Específico de Android)

```kotlin
androidMain.dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.ktor.client.okhttp)  // Engine HTTP para Android
    implementation(libs.koin.android)        // Extensiones Android de Koin
}
```

#### iosMain (Específico de iOS)

```kotlin
iosMain.dependencies {
    implementation(libs.ktor.client.darwin)  // Engine HTTP para iOS (NSURLSession)
}
```

---

## 🔧 Configuración de Koin

### 1. Módulo de Dependencias ([AppModule.kt](file:///Users/albertmontesanccasi/androidProject/Altopia/composeApp/src/commonMain/kotlin/org/terratec/altopia/di/AppModule.kt))

```kotlin
package org.terratec.altopia.di

import org.koin.dsl.module
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val appModule = module {
    // HttpClient configurado
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true  // Importante para APIs externas
                })
            }
        }
    }
    
    // Repositorio
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    
    // Use Cases
    factory { GetUserUseCase(get()) }
    factory { GetVideosUseCase(get()) }
    
    // ViewModel (como singleton para evitar reinicios)
    single { UserViewModel(get(), get()) }
}
```

**Notas:**
- `single`: Instancia única (Singleton)
- `factory`: Nueva instancia cada vez
- `get()`: Resuelve dependencias automáticamente

### 2. Función de Inicialización ([InitKoin.kt](file:///Users/albertmontesanccasi/androidProject/Altopia/composeApp/src/commonMain/kotlin/org/terratec/altopia/di/InitKoin.kt))

```kotlin
package org.terratec.altopia.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}
```

### 3. Uso en Compose

#### ❌ Forma NO Recomendada (Problemas en iOS)

```kotlin
@OptIn(KoinExperimentalAPI::class)
@Composable
fun UserScreen() {
    val viewModel = koinViewModel<UserViewModel>()  // ⚠️ Causa errores de linkeo en iOS
    // ...
}
```

#### ✅ Forma Recomendada (Compatible iOS/Android)

```kotlin
@Composable
fun UserScreen(
    viewModel: UserViewModel = koinInject()  // ✅ Funciona en ambas plataformas
) {
    val uiState by viewModel.uiState.collectAsState()
    // ...
}
```

---

## 🌐 Configuración de Ktor

### 1. Modelo de Datos ([VideoDto.kt](file:///Users/albertmontesanccasi/androidProject/Altopia/composeApp/src/commonMain/kotlin/org/terratec/altopia/data/remote/VideoDto.kt))

```kotlin
package org.terratec.altopia.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
    val id: Long,
    @SerialName("created_at")  // Mapea el campo del JSON
    val createdAt: String,
    val videoLink: String,
    val isFinished: Boolean? = null
)
```

### 2. Implementación del Repositorio

```kotlin
package org.terratec.altopia.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers

class UserRepositoryImpl(
    private val httpClient: HttpClient
) : UserRepository {

    override suspend fun getVideos(): Result<List<Video>> {
        return try {
            val dtos = httpClient.get("https://api.example.com/videos") {
                headers {
                    append("apikey", "your-api-key")
                    append("Authorization", "Bearer your-token")
                }
            }.body<List<VideoDto>>()
            
            Result.success(dtos.map { UserMapper.dtoToDomain(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**Características:**
- Manejo de errores con `Result<T>`
- Headers personalizados
- Deserialización automática con `.body<T>()`
- Mapeo de DTO a modelo de dominio

---

## 💡 Implementación Ejemplo: Lista de Videos

### Arquitectura del Feature

```
Video Feature
├── Domain Layer
│   ├── Video.kt (modelo)
│   ├── UserRepository.kt (interfaz)
│   └── GetVideosUseCase.kt
├── Data Layer
│   ├── VideoDto.kt (DTO)
│   ├── UserMapper.kt (mapper)
│   └── UserRepositoryImpl.kt
└── Presentation Layer
    ├── UserViewModel.kt
    └── UserScreen.kt
```

### 1. Domain Layer

#### [Video.kt](file:///Users/albertmontesanccasi/androidProject/Altopia/composeApp/src/commonMain/kotlin/org/terratec/altopia/domain/model/Video.kt)
```kotlin
package org.terratec.altopia.domain.model

data class Video(
    val id: Long,
    val createdAt: String,
    val videoLink: String,
    val isFinished: Boolean?
)
```

#### [UserRepository.kt](file:///Users/albertmontesanccasi/androidProject/Altopia/composeApp/src/commonMain/kotlin/org/terratec/altopia/domain/repository/UserRepository.kt)
```kotlin
package org.terratec.altopia.domain.repository

interface UserRepository {
    suspend fun getVideos(): Result<List<Video>>
}
```

#### [GetVideosUseCase.kt](file:///Users/albertmontesanccasi/androidProject/Altopia/composeApp/src/commonMain/kotlin/org/terratec/altopia/domain/usecase/GetVideosUseCase.kt)
```kotlin
package org.terratec.altopia.domain.usecase

class GetVideosUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<Video>> {
        return userRepository.getVideos()
    }
}
```

### 2. Data Layer

#### Mapper
```kotlin
package org.terratec.altopia.data.mapper

object UserMapper {
    fun dtoToDomain(dto: VideoDto): Video {
        return Video(
            id = dto.id,
            createdAt = dto.createdAt,
            videoLink = dto.videoLink,
            isFinished = dto.isFinished
        )
    }
}
```

### 3. Presentation Layer

#### [UserViewModel.kt](file:///Users/albertmontesanccasi/androidProject/Altopia/composeApp/src/commonMain/kotlin/org/terratec/altopia/presentation/viewmodel/UserViewModel.kt)
```kotlin
package org.terratec.altopia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getVideosUseCase: GetVideosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadVideos()  // Carga automática al iniciar
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getVideosUseCase()
                .onSuccess { videos ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        videos = videos
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error"
                    )
                }
        }
    }
}

data class UserUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val videos: List<Video> = emptyList(),
    val error: String? = null
)
```

#### [UserScreen.kt](file:///Users/albertmontesanccasi/androidProject/Altopia/composeApp/src/commonMain/kotlin/org/terratec/altopia/presentation/ui/UserScreen.kt)
```kotlin
package org.terratec.altopia.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun UserScreen(
    viewModel: UserViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        uiState.error?.let { error ->
            Text("Error: $error", color = Color.Red)
        }

        LazyColumn {
            items(uiState.videos.size) { index ->
                val video = uiState.videos[index]
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Video ID: ${video.id}")
                        Text("Created At: ${video.createdAt}")
                        Text("Link: ${video.videoLink}")
                        Text("Finished: ${video.isFinished ?: "Unknown"}")
                    }
                }
            }
        }
    }
}
```

---

## 🎯 Configuración Específica por Plataforma

### Android

#### 1. AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- ⚠️ IMPORTANTE: Permiso de Internet -->
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".AltopiaApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">
        
        <activity
            android:exported="true"
            android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

#### 2. Application Class
```kotlin
package org.terratec.altopia

import android.app.Application
import org.terratec.altopia.di.initKoin

class AltopiaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            // Configuraciones específicas de Android si es necesario
        }
    }
}
```

### iOS

#### [MainViewController.kt](file:///Users/albertmontesanccasi/androidProject/Altopia/composeApp/src/iosMain/kotlin/org/terratec/altopia/MainViewController.kt)

```kotlin
package org.terratec.altopia

import androidx.compose.ui.window.ComposeUIViewController
import org.terratec.altopia.di.initKoin

private var koinInitialized = false

fun MainViewController() = ComposeUIViewController { 
    // ⚠️ CRÍTICO: Inicializar Koin antes de usar composables
    if (!koinInitialized) {
        initKoin()
        koinInitialized = true
    }
    App() 
}
```

**⚠️ Nota Importante para iOS:**
- Koin DEBE inicializarse en `MainViewController` antes de llamar a `App()`
- Usar un flag `koinInitialized` para evitar múltiples inicializaciones
- **NO exportar** dependencias de lifecycle en el framework de iOS (causa errores de linkeo)

#### build.gradle.kts - Configuración iOS

```kotlin
listOf(
    iosArm64(),
    iosSimulatorArm64()
).forEach { iosTarget ->
    iosTarget.binaries.framework {
        baseName = "ComposeApp"
        isStatic = true
        // ⚠️ NO exportar dependencias aquí
        // Esto causaba errores de linkeo
    }
}
```

---

## ⚠️ Problemas Comunes y Soluciones

### 1. Error: "Permission denied (missing INTERNET permission?)" (Android)

**Problema:** App crashea al hacer requests HTTP en Android.

**Solución:** Agregar permiso de internet en `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 2. Error: "KoinApplication has not been started" (iOS)

**Problema:** Koin no está inicializado antes de usar `koinInject()`.

**Solución:** Inicializar Koin en `MainViewController.kt`:
```kotlin
private var koinInitialized = false

fun MainViewController() = ComposeUIViewController { 
    if (!koinInitialized) {
        initKoin()
        koinInitialized = true
    }
    App() 
}
```

### 3. Error de Linkeo en iOS con `koinViewModel()`

**Problema:**
```
Undefined symbol: _kfun:androidx.lifecycle.viewmodel.compose#androidx_lifecycle_viewmodel_compose_LocalViewModelStoreOwner$stableprop_getter$artificial(){}kotlin.Int
```

**Solución:** Usar `koinInject()` en lugar de `koinViewModel()`:
```kotlin
// ❌ NO usar
val viewModel = koinViewModel<UserViewModel>()

// ✅ Usar
val viewModel: UserViewModel = koinInject()
```

### 4. Error: "Following dependencies exported in the debugFramework binary are not specified as API-dependencies"

**Problema:** Intentar exportar dependencias en el framework de iOS.

**Solución:** NO exportar dependencias en `build.gradle.kts`. Mantener el framework simple:
```kotlin
iosTarget.binaries.framework {
    baseName = "ComposeApp"
    isStatic = true
    // NO agregar export() aquí
}
```

---

## 🚀 Mejoras Recomendadas

### 1. Gestión de Configuraciones

**Problema Actual:** API keys hardcodeadas en el código.

**Mejora:** Usar `BuildConfig` o archivos de configuración:

```kotlin
// commonMain/kotlin/config/ApiConfig.kt
expect object ApiConfig {
    val baseUrl: String
    val apiKey: String
}

// androidMain/kotlin/config/ApiConfig.kt
actual object ApiConfig {
    actual val baseUrl: String = BuildConfig.BASE_URL
    actual val apiKey: String = BuildConfig.API_KEY
}

// iosMain/kotlin/config/ApiConfig.kt
actual object ApiConfig {
    actual val baseUrl: String = "https://..."
    actual val apiKey: String = "..."
}
```

### 2. Logging y Debugging

Agregar plugin de logging a Ktor:

```kotlin
// build.gradle.kts
implementation("io.ktor:ktor-client-logging:3.0.0")

// AppModule.kt
single {
    HttpClient {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(ContentNegotiation) {
            json(...)
        }
    }
}
```

### 3. Timeout Configuration

```kotlin
single {
    HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
        // ...
    }
}
```

### 4. Error Handling Mejorado

Crear sealed class para errores:

```kotlin
sealed class DataError {
    data class Network(val message: String) : DataError()
    data class Server(val code: Int, val message: String) : DataError()
    data class Unknown(val throwable: Throwable) : DataError()
}

// En el repositorio
override suspend fun getVideos(): Result<List<Video>> {
    return try {
        // ...
    } catch (e: ClientRequestException) {
        Result.failure(DataError.Server(e.response.status.value, e.message))
    } catch (e: IOException) {
        Result.failure(DataError.Network(e.message ?: "Network error"))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }
}
```

### 5. Cache y Persistencia

Para mejorar la experiencia offline, considerar:
- **Room** (Android) + **SQLDelight** (compartido) para cache local
- **DataStore** para preferencias
- Implementar Repository Pattern con lógica de cache-first

### 6. Testing

Agregar tests unitarios:

```kotlin
// commonTest
class GetVideosUseCaseTest {
    @Test
    fun `getVideos returns success when repository succeeds`() = runTest {
        val mockRepo = mockk<UserRepository>()
        coEvery { mockRepo.getVideos() } returns Result.success(listOf(...))
        
        val useCase = GetVideosUseCase(mockRepo)
        val result = useCase()
        
        assertTrue(result.isSuccess)
    }
}
```

### 7. Módulos de Koin por Feature

Organizar módulos por feature en lugar de uno monolítico:

```kotlin
val networkModule = module {
    single { /* HttpClient */ }
}

val videoModule = module {
    factory { GetVideosUseCase(get()) }
    single { VideoViewModel(get()) }
}

val userModule = module {
    factory { GetUserUseCase(get()) }
}

fun initKoin() {
    startKoin {
        modules(networkModule, videoModule, userModule)
    }
}
```

---

## 📚 Referencias

- [Koin Documentation](https://insert-koin.io/docs/reference/koin-mp/kmp)
- [Ktor Client Documentation](https://ktor.io/docs/client-create-multiplatform-application.html)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)

---

## 📝 Notas Finales

### Lecciones Aprendidas

1. **iOS requiere cuidado especial** con las dependencias de ViewModel
2. **Koin debe inicializarse explícitamente** en cada plataforma
3. **No exportar dependencias** en el framework de iOS a menos que sea absolutamente necesario
4. **`koinInject()` es más compatible** que `koinViewModel()` en KMP
5. **Permisos de Android** deben configurarse manualmente

### Checklist de Setup

- [ ] Agregar plugins de serialization en `build.gradle.kts`
- [ ] Configurar dependencias por source set
- [ ] Crear módulo de Koin con todas las dependencias
- [ ] Inicializar Koin en Android (`Application`)
- [ ] Inicializar Koin en iOS (`MainViewController`)
- [ ] Agregar permiso de INTERNET en `AndroidManifest.xml`
- [ ] Usar `koinInject()` en lugar de `koinViewModel()`
- [ ] No exportar dependencias en iOS framework

---

*Documento creado: 2025-11-22*  
*Última actualización: 2025-11-22*
