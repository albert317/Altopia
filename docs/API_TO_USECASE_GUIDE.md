# Guía de Implementación: De API (CURL) a UseCase

Esta guía detalla el flujo de trabajo para implementar una nueva funcionalidad en la aplicación, partiendo desde una petición `curl` (o detalles técnicos equivalentes) hasta su exposición en un `UseCase` del dominio.

## Prerrequisitos
Antes de comenzar, necesitas uno de los siguientes:
1.  **Comando CURL**: La petición probada que funciona (ej. Postman, terminal).
2.  **Detalle Técnico**: URL del endpoint, parámetros requeridos y estructura de respuesta.

Además de:
*   **ApiService Objetivo**: Saber en qué interfaz de API (`UserApiService`, `AuthApiService`, etc.) se agregará.
*   **Respuesta JSON**: Conocer la estructura de la respuesta. *Si no la tienes, solicítala o ejecuta el curl para obtenerla.*

---

## 1. Definición del Response (DTO)
**Ubicación:** `data/remote/dto/response`

Crea o actualiza una `data class` marcada con `@Serializable`.
*   Usa `@SerialName("json_field")` para mapear los campos del JSON a propiedades `camelCase` de Kotlin.
*   **Regla**: Los tipos de datos deben coincidir exactamente con el JSON (String?, Int?, Boolean, etc.).

```kotlin
@Serializable
data class PersonResponse(
    @SerialName("id") val id: String,
    @SerialName("auth_id") val authId: String
    // ... otros campos
)
```

## 2. Implementación de API Service
**Ubicación:** `data/remote/api`

1.  **Interfaz (`ApiService`)**:
    *   **Manejo de Listas en Supabase (IMPORTANTE):**
    *   **Comportamiento por defecto:** Supabase devuelve **SIEMPRE** un Array JSON (`[]`), incluso si filtras por ID único.
    *   **NUNCA USAR HEADERS:** No utilices `Accept: application/vnd.pgrst.object+json`. Esto puede causar problemas.
    *   **Solución:**
        *   Tu `ApiService` siempre debe devolver `List<TuResponse>`.
        *   Usa `limit=1` en los parámetros de la URL para optimizar.
        *   Tu `RemoteDataSource` es el responsable de hacer `.first()` o `.firstOrNull()` para obtener el objeto único.
    *   **Retorno**: Define `List<PersonResponse>` salvo que uses headers específicos (`Accept: application/vnd.pgrst.object+json`) para forzar un objeto único.

2.  **Implementación (`ApiServiceImpl`)**:
    *   Usa `safeApiCall` para manejar errores de red HTTP.
    *   Para obtener un solo registro, se recomienda añadir `limit=1` a los parámetros (optimización), pero seguir retornando `List` para evitar errores de deserialización si el API decide devolver un array.

```kotlin
// CURL: .../rest/v1/personas?auth_id=eq.123&limit=1
override suspend fun getPerson(authId: String): List<PersonResponse> {
    return safeApiCall {
        httpClient.get("personas") {
            url.parameters.append("auth_id", "eq.$authId")
            url.parameters.append("limit", "1") // Optimización
        }
    }
}
```

## 3. Fuente de Datos Remota (RemoteDataSource)
**Ubicación:** `data/remote/datasource`

Esta capa decide si el dominio recibe una Lista o un Objeto Único.

*   Si el dominio necesita 1 objeto: Llama al API (`List`) y toma el primero (`firstOrNull()` o `first()`).
*   Si el dominio necesita una lista: Pasa la lista tal cual.

```kotlin
// Caso: Dominio necesita 1 objeto, API devuelve List
override suspend fun getPerson(authId: String): Result<PersonResponse> {
    return try {
        // Lógica de negocio de data: Convertir lista a objeto único
        val responseList = apiService.getPerson(authId)
        val singlePerson = responseList.firstOrNull() ?: throw Exception("Person not found")
        
        Result.success(singlePerson)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

## 4. Dominio (Model & UseCase)
**Ubicación:** `domain/`

### 4.1 Modelo de Dominio (`domain/model`)
Crea una `data class` pura.
*   **Sin anotaciones**: No uses `@Serializable` aquí.
*   **Tipos Limpios**: Usa tipos de Kotlin ideales para tu lógica de negocio.

```kotlin
data class Person(
    val id: String,
    val authId: String
)
```

### 4.2 UseCase (`domain/usecase/[feature]`)
Encapsula una acción de negocio única.
*   Debe tener un `operator fun invoke`.
*   Retorna `Result<ModeloDominio>`.

```kotlin
class GetPersonUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(authId: String): Result<Person> {
        return repository.getPerson(authId)
    }
}
```

## 5. Repositorio y Mapper
**Conecta Data con Dominio.**

### 5.1 Mapper (`data/mapper`)
Funciones para transformar DTO <-> Domain.

```kotlin
fun personResponseToDomain(response: PersonResponse): Person {
    return Person(
        id = response.id,
        authId = response.authId
    )
}
```

### 5.2 Repositorio (`domain/repository` & `data/repository`)
Implementa la interfaz del dominio usando el DataSource y el Mapper.

```kotlin
override suspend fun getPerson(authId: String): Result<Person> {
    return remoteDataSource.getPerson(authId)
        .map { response -> mapper.personResponseToDomain(response) }
}
```

---

## Resumen de Decisiones (Lista vs Objeto)
| Capa | Entrada | Salida Típica | Notas |
| :--- | :--- | :--- | :--- |
| **API Service** | JSON Array `[{...}]` | `List<Dto>` | Supabase default. Usa `limit=1` si buscas uno. |
| **DataSource** | `List<Dto>` | `Result<Dto>` | Aplica `first()` o `firstOrNull()` si buscas uno. |
| **Repository** | `Result<Dto>` | `Result<Domain>` | Mapea DTO a Dominio. |
| **UseCase** | `Result<Domain>` | `Result<Domain>` | Pasa el resultado a la UI. |

---

## 6. Verificaciones Críticas (Checklist)

### 6.1 Imports
Asegúrate de importar explícitamente las clases DTO en `ApiServiceImpl` y `RemoteDataSourceImpl`.
*   El compilador puede fallar con "Unresolved reference" si olvidas importar los Request/Response DTOs que acabas de crear.
*   **Action**: Verifica los imports en el encabezado del archivo.

### 6.2 Inyección de Dependencias (DI)
**Ubicación:** `di/AppModule.kt` (o `di/AuthModule.kt`)

Si creas un nuevo `UseCase`, `Repository` o `ApiService`, **DEBES** registrarlo en Koin.
1.  **ApiService**: Si es nuevo, regístralo como `single`.
2.  **Repository**: Si es nuevo, regístralo como `single`.
3.  **UseCase**: Regístralo como `factory`.

```kotlin
// Ejemplo en AppModule.kt
factory { GetNewFeatureUseCase(get()) }
```
Si olvidas esto, la app crasheará en runtime con error de Koin.
