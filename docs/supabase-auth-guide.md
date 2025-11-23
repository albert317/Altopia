# Guía de Autenticación con Supabase

## Índice
1. [Conceptos Clave](#conceptos-clave)
2. [API de Login - Qué Retorna](#api-de-login---qué-retorna)
3. [Gestión de Tokens y Sesiones](#gestión-de-tokens-y-sesiones)
4. [API Key vs Authorization Token](#api-key-vs-authorization-token)
5. [Tipos de Tokens en Supabase](#tipos-de-tokens-en-supabase)
6. [Flujo de Autenticación](#flujo-de-autenticación)
7. [Guía de Implementación](#guía-de-implementación)

---

## Conceptos Clave

### ¿Qué es Supabase Auth?
Supabase Auth es un servicio de autenticación completo que maneja:
- Registro y login de usuarios
- Gestión de sesiones con JWT (JSON Web Tokens)
- Refresh tokens automáticos
- Verificación de email
- Recuperación de contraseña
- Autenticación social (Google, GitHub, etc.)

---

## Endpoints de Autenticación

### 1. Sign Up (Registro de Usuario)

**Endpoint:**
```bash
POST https://thvijyqaigfbfbbrmknx.supabase.co/auth/v1/signup
Headers:
  - apikey: SUPABASE_ANON_KEY
  - Content-Type: application/json
Body:
  {
    "email": "user@email.com",
    "password": "password123"
  }
```

**Respuesta Exitosa (200 OK):**
Misma estructura que el login (ver abajo).

**Notas:**
- Crea una nueva cuenta de usuario
- Por defecto, requiere confirmación de email (configurable)
- Retorna los tokens inmediatamente si la confirmación no es requerida

---

### 2. Login (Inicio de Sesión)

**Endpoint:**
```bash
POST https://thvijyqaigfbfbbrmknx.supabase.co/auth/v1/token?grant_type=password
Headers:
  - apikey: SUPABASE_ANON_KEY
  - Content-Type: application/json
Body:
  {
    "email": "user@email.com",
    "password": "password123"
  }
```

### Respuesta Exitosa (200 OK)
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", 
  "token_type": "bearer",
  "expires_in": 3600,
  "expires_at": 1700000000,
  "refresh_token": "v1_refresh_token_...",
  "user": {
    "id": "uuid-del-usuario",
    "aud": "authenticated",
    "role": "authenticated",
    "email": "user@email.com",
    "email_confirmed_at": "2024-01-15T10:30:00.000Z",
    "phone": "",
    "confirmed_at": "2024-01-15T10:30:00.000Z",
    "last_sign_in_at": "2024-01-20T15:45:00.000Z",
    "app_metadata": {
      "provider": "email",
      "providers": ["email"]
    },
    "user_metadata": {
      // Datos personalizados del usuario
      "name": "John Doe",
      "avatar_url": "https://..."
    },
    "identities": [...],
    "created_at": "2024-01-15T10:30:00.000Z",
    "updated_at": "2024-01-20T15:45:00.000Z"
  }
}
```

### Campos Importantes de la Respuesta

| Campo | Descripción | Uso |
|-------|-------------|-----|
| `access_token` | JWT que identifica al usuario autenticado | Se usa en el header `Authorization: Bearer <token>` para todas las peticiones autenticadas |
| `token_type` | Tipo de token (siempre "bearer") | Indica cómo usar el token en el header |
| `expires_in` | Tiempo de vida del access_token en **segundos** (por defecto 3600s = 1 hora) | Para calcular cuándo refrescar el token |
| `expires_at` | Timestamp Unix de expiración | Momento exacto de expiración del token |
| `refresh_token` | Token para obtener un nuevo access_token | Se guarda de forma segura para renovar la sesión |
| `user` | Objeto con toda la información del usuario | Datos del perfil y metadata |

### Respuesta de Error (400/401)
```json
{
  "error": "invalid_grant",
  "error_description": "Invalid login credentials"
}
```

---

## Gestión de Tokens y Sesiones

### 1. Duración del JWT (Access Token)

> [!IMPORTANT]
> Por defecto, el `access_token` expira en **1 hora (3600 segundos)**.

- **Configurable**: Se puede modificar en el dashboard de Supabase (Project Settings > Auth)
- **Rango permitido**: Entre 5 minutos y 1 semana
- **Recomendación**: Mantener 1 hora por seguridad

### 2. Refresh Token

El `refresh_token` tiene una duración mucho mayor:
- **Duración por defecto**: Permanece válido mientras se use activamente
- **Inactividad**: Expira después de cierto tiempo sin uso (configurable, típicamente 30 días)
- **Rotación**: Supabase puede rotar el refresh token cada vez que se usa (configurable)

### 3. Flujo de Renovación de Token

```mermaid
sequenceDiagram
    participant App
    participant Supabase
    
    App->>Supabase: Login (email/password)
    Supabase-->>App: access_token + refresh_token
    
    Note over App: Usa access_token<br/>durante 1 hora
    
    App->>Supabase: API Request con access_token
    Supabase-->>App: 401 Unauthorized (token expirado)
    
    App->>Supabase: POST /auth/v1/token?grant_type=refresh_token<br/>Body: {refresh_token}
    Supabase-->>App: Nuevo access_token + refresh_token
    
    App->>Supabase: Reintenta API Request con nuevo token
    Supabase-->>App: 200 OK
```

### Endpoint para Refrescar Token
```bash
POST https://thvijyqaigfbfbbrmknx.supabase.co/auth/v1/token?grant_type=refresh_token
Headers:
  - apikey: SUPABASE_ANON_KEY
  - Content-Type: application/json
Body:
  {
    "refresh_token": "v1_refresh_token_..."
  }
```

**Respuesta**: Misma estructura que el login, con nuevos tokens.

---

### 4. Obtener Usuario Actual

**Endpoint:**
```bash
GET https://thvijyqaigfbfbbrmknx.supabase.co/auth/v1/user
Headers:
  - apikey: SUPABASE_ANON_KEY
  - Authorization: Bearer ACCESS_TOKEN
```

**Respuesta (200 OK):**
```json
{
  "id": "uuid-del-usuario",
  "aud": "authenticated",
  "role": "authenticated",
  "email": "user@email.com",
  "email_confirmed_at": "2024-01-15T10:30:00.000Z",
  "phone": "",
  "confirmed_at": "2024-01-15T10:30:00.000Z",
  "last_sign_in_at": "2024-01-20T15:45:00.000Z",
  "app_metadata": {
    "provider": "email",
    "providers": ["email"]
  },
  "user_metadata": {
    "name": "John Doe",
    "avatar_url": "https://..."
  },
  "created_at": "2024-01-15T10:30:00.000Z",
  "updated_at": "2024-01-20T15:45:00.000Z"
}
```

**Uso:**
- Obtener información actualizada del usuario autenticado
- Verificar que el token sigue siendo válido
- Sincronizar datos del usuario

---

### 5. Actualizar Usuario

**Endpoint:**
```bash
PUT https://thvijyqaigfbfbbrmknx.supabase.co/auth/v1/user
Headers:
  - apikey: SUPABASE_ANON_KEY
  - Authorization: Bearer ACCESS_TOKEN
  - Content-Type: application/json
Body:
  {
    "email": "newemail@email.com",     // Opcional
    "password": "newpassword123",      // Opcional
    "data": {                          // Opcional
      "name": "New Name",
      "avatar_url": "https://..."
    }
  }
```

**Respuesta (200 OK):**
Objeto de usuario actualizado (misma estructura que "Obtener Usuario Actual").

**Notas:**
- Todos los campos son opcionales, solo envía los que quieres actualizar
- `data` actualiza el `user_metadata`
- Cambiar el email puede requerir confirmación (configurable)
- Cambiar el password cierra todas las sesiones excepto la actual

---

### 6. Recuperación de Contraseña

**Endpoint:**
```bash
POST https://thvijyqaigfbfbbrmknx.supabase.co/auth/v1/recover
Headers:
  - apikey: SUPABASE_ANON_KEY
  - Content-Type: application/json
Body:
  {
    "email": "user@email.com"
  }
```

**Respuesta (200 OK):**
```json
{}
```

**Flujo:**
1. Usuario solicita recuperación de contraseña
2. Supabase envía un email con un link especial
3. Usuario hace clic en el link (lo redirige a tu app con un token)
4. App usa el token para permitir que el usuario establezca una nueva contraseña
5. App llama a "Actualizar Usuario" con la nueva contraseña

---

### 7. Logout (Cerrar Sesión)

**Endpoint:**
```bash
POST https://thvijyqaigfbfbbrmknx.supabase.co/auth/v1/logout
Headers:
  - apikey: SUPABASE_ANON_KEY
  - Authorization: Bearer ACCESS_TOKEN
  - Content-Type: application/json
```

**Respuesta (204 No Content)**

**Efecto:**
- Invalida el `access_token` y `refresh_token` actual
- El usuario debe hacer login nuevamente
- Importante: Siempre limpia los tokens del almacenamiento local, incluso si la petición falla

---

### 8. Otros Métodos de Autenticación

Tu proyecto de Supabase también soporta:

#### Magic Link (Login sin contraseña)
```bash
POST https://thvijyqaigfbfbbrmknx.supabase.co/auth/v1/magiclink
Body: {"email": "user@email.com"}
```

#### OAuth Providers (Google, GitHub, etc.)
```bash
GET https://thvijyqaigfbfbbrmknx.supabase.co/auth/v1/authorize?provider=github
```

#### SMS OTP (Login con teléfono)
```bash
POST https://thvijyqaigfbfbbrmknx.supabase.co/auth/v1/otp
Body: {"phone": "+13334445555"}
```

Estos métodos los implementaremos en futuras iteraciones según sea necesario.

---

## API Key vs Authorization Token

### API Key (SUPABASE_ANON_KEY)

> [!NOTE]
> La API Key es una **credencial pública del proyecto**, no del usuario.

**Características:**
- ✅ **Público y seguro**: Se puede incluir en aplicaciones cliente
- ✅ **Identificador del proyecto**: Identifica tu proyecto de Supabase
- ✅ **Permisos limitados**: Solo tiene permisos de "usuario anónimo"
- ✅ **No expira**: Es estático para el proyecto
- ⚠️ **Requiere RLS**: Row Level Security debe estar activado en la base de datos

**Uso:**
```bash
apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRodmlqeXFhaWdmYmZiYnJta254Iiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTAwMDAwMDAsImV4cCI6MTg0NzY4MDAwMH0...
```

**Se usa en:**
- Header `apikey` en **todas** las peticiones a Supabase
- Operaciones públicas (antes del login)
- Login, registro, recuperación de contraseña

### Authorization Token (Access Token del Usuario)

> [!IMPORTANT]
> El Access Token es una **credencial privada del usuario autenticado**.

**Características:**
- 🔒 **Privado**: Nunca debe compartirse
- 🔒 **Identifica al usuario**: Contiene el ID y rol del usuario
- ⏱️ **Temporal**: Expira en 1 hora (por defecto)
- 🎯 **Permisos completos**: Acceso según las políticas RLS del usuario

**Uso:**
```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLXV1aWQiLCJyb2xlIjoiYXV0aGVudGljYXRlZCIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDAzNjAwfQ...
```

**Se usa en:**
- Header `Authorization` después del login
- Todas las operaciones que requieren autenticación
- Acceso a datos protegidos por RLS

### Comparación Visual

```
┌─────────────────────────────────────────────────────────────┐
│                    PETICIÓN A SUPABASE                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Headers:                                                    │
│    ┌────────────────────────────────────────────────────┐  │
│    │ apikey: SUPABASE_ANON_KEY                          │  │
│    │ (Identifica el PROYECTO)                           │  │
│    │ - Siempre presente                                 │  │
│    │ - Público                                          │  │
│    └────────────────────────────────────────────────────┘  │
│                                                              │
│    ┌────────────────────────────────────────────────────┐  │
│    │ Authorization: Bearer ACCESS_TOKEN                 │  │
│    │ (Identifica al USUARIO)                            │  │
│    │ - Solo después del login                           │  │
│    │ - Privado                                          │  │
│    │ - Expira en 1 hora                                 │  │
│    └────────────────────────────────────────────────────┘  │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Tipos de Tokens en Supabase

### 1. Token Anónimo (SUPABASE_ANON_KEY)

**¿Qué es?**
- Es la API Key pública del proyecto
- Representa un usuario **no autenticado**

**Decodificando el token:**
```json
{
  "iss": "supabase",
  "ref": "thvijyqaigfbfbbrmknx",
  "role": "anon",  // ← Usuario anónimo
  "iat": 1690000000,
  "exp": 1847680000  // ← Expira en años (prácticamente no expira)
}
```

**Permisos:**
- ✅ Acceso a datos públicos
- ✅ Login, registro
- ❌ No puede acceder a datos privados del usuario
- ❌ Sin identidad de usuario

**Ejemplo de uso:**
```bash
# Login sin estar autenticado
curl -X POST 'https://proyecto.supabase.co/auth/v1/token?grant_type=password' \
  -H "apikey: ANON_KEY" \
  -H "Content-Type: application/json" \
  -d '{"email": "user@email.com", "password": "pass123"}'
```

### 2. Token de Usuario Autenticado (Access Token)

**¿Qué es?**
- JWT generado después del login
- Representa un usuario **autenticado**

**Decodificando el token:**
```json
{
  "aud": "authenticated",
  "exp": 1700003600,  // ← Expira en 1 hora
  "iat": 1700000000,
  "iss": "https://proyecto.supabase.co/auth/v1",
  "sub": "uuid-del-usuario",  // ← ID del usuario
  "email": "user@email.com",
  "phone": "",
  "app_metadata": {
    "provider": "email",
    "providers": ["email"]
  },
  "user_metadata": {
    "name": "John Doe"
  },
  "role": "authenticated",  // ← Usuario autenticado
  "aal": "aal1",
  "amr": [{"method": "password", "timestamp": 1700000000}],
  "session_id": "session-uuid"
}
```

**Permisos:**
- ✅ Acceso a datos públicos
- ✅ Acceso a sus propios datos privados
- ✅ Operaciones según políticas RLS
- ✅ Identidad completa del usuario

**Ejemplo de uso:**
```bash
# Acceder a datos del usuario autenticado
curl -X GET 'https://proyecto.supabase.co/rest/v1/profiles?id=eq.uuid-usuario' \
  -H "apikey: ANON_KEY" \
  -H "Authorization: Bearer ACCESS_TOKEN"
```

### Diferencias Clave

| Aspecto | Token Anónimo (anon) | Token Autenticado (authenticated) |
|---------|----------------------|-----------------------------------|
| **Origen** | Dashboard de Supabase | Generado al hacer login |
| **Rol** | `anon` | `authenticated` |
| **Identidad** | Sin usuario | Con ID de usuario |
| **Duración** | Años (prácticamente permanente) | 1 hora (configurable) |
| **Renovación** | No necesita | Se renueva con refresh_token |
| **Uso** | Operaciones públicas, login | Operaciones autenticadas |
| **Storage** | Se puede hardcodear en el código | Debe guardarse de forma segura |

### ¿Cuándo usar cada uno?

```kotlin
// Token Anónimo (apikey) - SIEMPRE presente
val apiKey = "SUPABASE_ANON_KEY"

// Escenario 1: Usuario NO autenticado (antes del login)
httpClient.post("auth/v1/token") {
    header("apikey", apiKey)  // ← Solo token anónimo
    // Sin Authorization header
}

// Escenario 2: Usuario autenticado (después del login)
httpClient.get("rest/v1/profiles") {
    header("apikey", apiKey)  // ← Token anónimo (identifica el proyecto)
    header("Authorization", "Bearer $accessToken")  // ← Token de usuario
}
```

> [!WARNING]
> Nunca confundir:
> - **apikey**: Va en el header `apikey`, identifica el PROYECTO
> - **access_token**: Va en el header `Authorization: Bearer`, identifica al USUARIO

---

## Flujo de Autenticación

### Diagrama Completo

```mermaid
graph TD
    A[App Inicia] --> B{¿Tiene tokens<br/>guardados?}
    B -->|No| C[Mostrar Login]
    B -->|Sí| D{¿Access token<br/>válido?}
    
    D -->|Sí| E[Usuario autenticado<br/>Usar access_token]
    D -->|No| F{¿Refresh token<br/>válido?}
    
    F -->|Sí| G[Refrescar tokens]
    F -->|No| C
    
    G --> E
    
    C --> H[Usuario ingresa<br/>email y password]
    H --> I[POST /auth/v1/token<br/>grant_type=password]
    I --> J{¿Éxito?}
    
    J -->|Sí| K[Guardar access_token<br/>y refresh_token]
    J -->|No| L[Mostrar error]
    
    K --> E
    L --> C
    
    E --> M[Hacer peticiones con<br/>Authorization: Bearer token]
    M --> N{¿Respuesta<br/>401?}
    
    N -->|No| O[Éxito]
    N -->|Sí| F
```

### Secuencia Temporal

```
Tiempo 0:00 - Login
├─ POST /auth/v1/token (grant_type=password)
├─ Guardar: access_token (válido 1h), refresh_token
└─ Estado: Autenticado ✅

Tiempo 0:05 - 0:55 - Operaciones Normales
├─ GET/POST/PUT/DELETE con Authorization: Bearer access_token
└─ Estado: Todo funciona ✅

Tiempo 1:00 - Access Token Expira
├─ GET /api/data ← 401 Unauthorized ❌
└─ Detección: Token expirado

Tiempo 1:00:05 - Refresh Automático
├─ POST /auth/v1/token (grant_type=refresh_token)
├─ Guardar: nuevo access_token, nuevo refresh_token
└─ Estado: Sesión renovada ✅

Tiempo 1:00:10 - Reintento
├─ GET /api/data con nuevo access_token
└─ Estado: Éxito ✅

Tiempo 30 días - Refresh Token Expira (si no se usa)
├─ POST /auth/v1/token (grant_type=refresh_token) ← 401 ❌
└─ Acción: Solicitar login nuevamente
```

---

## Guía de Implementación

### 1. Estructura de Datos

```kotlin
// Domain Layer - Models
data class User(
    val id: String,
    val email: String,
    val emailConfirmedAt: String?,
    val name: String?,
    val avatarUrl: String?
)

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long, // Unix timestamp
    val user: User
)

// Data Layer - API Response
@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("expires_at") val expiresAt: Long,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user") val user: UserResponse
)

@Serializable
data class UserResponse(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("email_confirmed_at") val emailConfirmedAt: String?,
    @SerialName("user_metadata") val userMetadata: UserMetadata?
)

@Serializable
data class UserMetadata(
    @SerialName("name") val name: String?,
    @SerialName("avatar_url") val avatarUrl: String?
)

// Data Layer - API Request
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
```

### 2. API Service

```kotlin
interface AuthApiService {
    suspend fun signUp(email: String, password: String): LoginResponse
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun refreshToken(refreshToken: String): LoginResponse
    suspend fun getCurrentUser(): UserResponse
    suspend fun updateUser(email: String? = null, password: String? = null, data: Map<String, Any>? = null): UserResponse
    suspend fun recoverPassword(email: String)
    suspend fun logout()
}

class AuthApiServiceImpl(
    private val httpClient: HttpClient
) : AuthApiService {
    
    override suspend fun signUp(email: String, password: String): LoginResponse {
        return httpClient.post("auth/v1/signup") {
            setBody(LoginRequest(email, password))
        }.body()
    }
    
    override suspend fun login(email: String, password: String): LoginResponse {
        return httpClient.post("auth/v1/token") {
            parameter("grant_type", "password")
            setBody(LoginRequest(email, password))
        }.body()
    }
    
    override suspend fun refreshToken(refreshToken: String): LoginResponse {
        return httpClient.post("auth/v1/token") {
            parameter("grant_type", "refresh_token")
            setBody(mapOf("refresh_token" to refreshToken))
        }.body()
    }
    
    override suspend fun getCurrentUser(): UserResponse {
        return httpClient.get("auth/v1/user").body()
    }
    
    override suspend fun updateUser(
        email: String?,
        password: String?,
        data: Map<String, Any>?
    ): UserResponse {
        return httpClient.put("auth/v1/user") {
            setBody(buildMap {
                email?.let { put("email", it) }
                password?.let { put("password", it) }
                data?.let { put("data", it) }
            })
        }.body()
    }
    
    override suspend fun recoverPassword(email: String) {
        httpClient.post("auth/v1/recover") {
            setBody(mapOf("email" to email))
        }
    }
    
    override suspend fun logout() {
        // El token se agrega automáticamente por el Auth plugin
        httpClient.post("auth/v1/logout")
    }
}
```

### 3. Configuración del HttpClient

```kotlin
// NetworkModule.kt
fun provideSupabaseHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        
        defaultRequest {
            url("https://thvijyqaigfbfbbrmknx.supabase.co/")
            header("apikey", BuildConfig.SUPABASE_ANON_KEY)
            contentType(ContentType.Application.Json)
        }
        
        install(Auth) {
            bearer {
                loadTokens {
                    // Cargar tokens del almacenamiento local
                    val session = sessionManager.getSession()
                    session?.let {
                        BearerTokens(
                            accessToken = it.accessToken,
                            refreshToken = it.refreshToken
                        )
                    }
                }
                
                refreshTokens {
                    // Refrescar automáticamente cuando expire
                    val session = sessionManager.getSession()
                    session?.let {
                        val response = authApiService.refreshToken(it.refreshToken)
                        sessionManager.saveSession(response.toAuthSession())
                        
                        BearerTokens(
                            accessToken = response.accessToken,
                            refreshToken = response.refreshToken
                        )
                    }
                }
            }
        }
    }
}
```

### 4. Session Manager (Almacenamiento Seguro)

```kotlin
interface SessionManager {
    suspend fun saveSession(session: AuthSession)
    suspend fun getSession(): AuthSession?
    suspend fun clearSession()
    fun isSessionValid(): Boolean
}

class SessionManagerImpl(
    // Usar DataStore o SharedPreferences encriptado
    private val dataStore: DataStore<Preferences>
) : SessionManager {
    
    override suspend fun saveSession(session: AuthSession) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = session.accessToken
            preferences[REFRESH_TOKEN_KEY] = session.refreshToken
            preferences[EXPIRES_AT_KEY] = session.expiresAt
            preferences[USER_JSON_KEY] = Json.encodeToString(session.user)
        }
    }
    
    override suspend fun getSession(): AuthSession? {
        val preferences = dataStore.data.first()
        val accessToken = preferences[ACCESS_TOKEN_KEY] ?: return null
        val refreshToken = preferences[REFRESH_TOKEN_KEY] ?: return null
        val expiresAt = preferences[EXPIRES_AT_KEY] ?: return null
        val userJson = preferences[USER_JSON_KEY] ?: return null
        
        return AuthSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresAt = expiresAt,
            user = Json.decodeFromString(userJson)
        )
    }
    
    override suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }
    
    override fun isSessionValid(): Boolean {
        // Verificar si el access_token no ha expirado
        val session = runBlocking { getSession() }
        return session?.let {
            System.currentTimeMillis() / 1000 < it.expiresAt
        } ?: false
    }
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val EXPIRES_AT_KEY = longPreferencesKey("expires_at")
        private val USER_JSON_KEY = stringPreferencesKey("user_json")
    }
}
```

### 5. Repository

```kotlin
interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun refreshCurrentUser(): Result<User>
    suspend fun updateUser(email: String? = null, password: String? = null, name: String? = null, avatarUrl: String? = null): Result<User>
    suspend fun recoverPassword(email: String): Result<Unit>
    fun isAuthenticated(): Boolean
}

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {
    
    override suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val response = authApiService.signUp(email, password)
            val session = response.toAuthSession()
            sessionManager.saveSession(session)
            Result.success(session.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApiService.login(email, password)
            val session = response.toAuthSession()
            sessionManager.saveSession(session)
            Result.success(session.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            authApiService.logout()
            sessionManager.clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            // Limpiar sesión local aunque falle el logout remoto
            sessionManager.clearSession()
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): User? {
        return sessionManager.getSession()?.user
    }
    
    override fun isAuthenticated(): Boolean {
        return sessionManager.isSessionValid()
    }
    
    override suspend fun refreshCurrentUser(): Result<User> {
        return try {
            val userResponse = authApiService.getCurrentUser()
            val user = userResponse.toDomain()
            
            // Actualizar usuario en la sesión
            sessionManager.getSession()?.let { session ->
                sessionManager.saveSession(session.copy(user = user))
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateUser(
        email: String?,
        password: String?,
        name: String?,
        avatarUrl: String?
    ): Result<User> {
        return try {
            val data = buildMap {
                name?.let { put("name", it) }
                avatarUrl?.let { put("avatar_url", it) }
            }.takeIf { it.isNotEmpty() }
            
            val userResponse = authApiService.updateUser(email, password, data)
            val user = userResponse.toDomain()
            
            // Actualizar usuario en la sesión
            sessionManager.getSession()?.let { session ->
                sessionManager.saveSession(session.copy(user = user))
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recoverPassword(email: String): Result<Unit> {
        return try {
            authApiService.recoverPassword(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 6. Use Cases

```kotlin
class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validaciones
        if (email.isBlank()) {
            return Result.failure(Exception("El email es requerido"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("El email no es válido"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        
        return authRepository.signUp(email, password)
    }
}

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validaciones
        if (email.isBlank()) {
            return Result.failure(Exception("El email es requerido"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("El email no es válido"))
        }
        
        return authRepository.login(email, password)
    }
}

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(refresh: Boolean = false): Result<User?> {
        return try {
            if (refresh) {
                authRepository.refreshCurrentUser()
            } else {
                Result.success(authRepository.getCurrentUser())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String? = null,
        password: String? = null,
        name: String? = null,
        avatarUrl: String? = null
    ): Result<User> {
        // Validaciones
        email?.let {
            if (it.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                return Result.failure(Exception("El email no es válido"))
            }
        }
        
        password?.let {
            if (it.isNotBlank() && it.length < 6) {
                return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            }
        }
        
        return authRepository.updateUser(email, password, name, avatarUrl)
    }
}

class RecoverPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(Exception("El email es requerido"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("El email no es válido"))
        }
        
        return authRepository.recoverPassword(email)
    }
}
```

### 7. ViewModel

```kotlin
class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            
            loginUseCase(email, password)
                .onSuccess { user ->
                    _uiState.value = LoginUiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Error(
                        error.message ?: "Error desconocido"
                    )
                }
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
```

### 8. Pantalla de Login (Compose)

```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = uiState !is LoginUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Iniciar Sesión")
            }
        }
        
        if (uiState is LoginUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (uiState as LoginUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
```

### 9. Navegación con Autenticación

```kotlin
@Composable
fun AppNavigation(
    authRepository: AuthRepository = koinInject()
) {
    val navController = rememberNavController()
    val isAuthenticated = remember { authRepository.isAuthenticated() }
    
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("home") {
            HomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
```

---

## Resumen de Conceptos Importantes

### ✅ Checklist de Implementación

- [ ] Configurar constantes de Supabase (URL, ANON_KEY)
- [ ] Crear modelos de dominio y DTOs
- [ ] Implementar AuthApiService
- [ ] Configurar HttpClient con Auth plugin
- [ ] Implementar SessionManager con almacenamiento seguro
- [ ] Crear AuthRepository
- [ ] Implementar Use Cases
- [ ] Crear ViewModel de Login
- [ ] Diseñar pantalla de Login
- [ ] Configurar navegación condicional
- [ ] Manejar refresh automático de tokens
- [ ] Implementar logout
- [ ] Probar flujo completo

### 🔑 Puntos Clave para Recordar

1. **Dos Headers Siempre**:
   - `apikey`: SUPABASE_ANON_KEY (identifica el proyecto)
   - `Authorization: Bearer`: access_token (identifica al usuario)

2. **Ciclo de Vida del Token**:
   - Access Token: 1 hora
   - Refresh Token: 30 días (o según configuración)
   - Automatizar el refresh antes de la expiración

3. **Seguridad**:
   - Nunca guardar tokens en plain text
   - Usar DataStore con encriptación
   - Limpiar tokens al hacer logout

4. **Manejo de Errores**:
   - 401: Token expirado → Intentar refresh
   - 400: Credenciales inválidas → Mostrar error
   - Network error → Reintentar o modo offline

5. **Row Level Security (RLS)**:
   - Habilitar RLS en todas las tablas
   - Crear políticas que usen `auth.uid()`
   - El access_token permite a Supabase identificar al usuario en las políticas

---

## Próximos Pasos

Una vez revisada esta guía, la implementación seguirá estos pasos:

1. **Configuración Inicial**: Agregar constantes de Supabase
2. **Modelos**: Crear las clases de datos
3. **API Layer**: Implementar AuthApiService
4. **Storage**: Implementar SessionManager
5. **Repository**: Crear AuthRepository
6. **Domain**: Implementar Use Cases
7. **Presentation**: ViewModel y UI
8. **Testing**: Probar flujo completo

---

## Referencias

- [Supabase Auth Documentation](https://supabase.com/docs/guides/auth)
- [Supabase Auth API Reference](https://supabase.com/docs/reference/javascript/auth-api)
- [JWT.io - Decodificar tokens](https://jwt.io/)
- [Ktor Auth Plugin](https://ktor.io/docs/auth.html)
