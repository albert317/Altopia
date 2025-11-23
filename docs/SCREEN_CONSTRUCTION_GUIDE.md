# Guía de Construcción de Pantallas

## Índice
- [1. Introducción](#1-introducción)
- [2. Estructura de Archivos](#2-estructura-de-archivos)
- [3. BaseScreen](#3-basescreen)
- [4. Material Design 3 Typography](#4-material-design-3-typography)
- [5. Color System](#5-color-system)
- [6. Componentes Material](#6-componentes-material)
- [7. Spacing y Layout](#7-spacing-y-layout)
- [8. Previews](#8-previews)
- [9. Ejemplo Completo](#9-ejemplo-completo)
- [10. Checklist](#10-checklist)

---

## 1. Introducción

Esta guía establece los estándares para construir pantallas en el proyecto Altopia, combinando:
- **Material Design 3**: Sistema de diseño oficial de Google
- **Patrón MVI**: Arquitectura unidireccional (ver [MVI_IMPLEMENTATION_GUIDE.md](file:///Users/albertmontesanccasi/androidProject/Altopia/docs/MVI_IMPLEMENTATION_GUIDE.md))
- **BaseScreen**: Contenedor estándar con diálogos y loading
- **AppTheme**: Tema personalizado con colores para gestión de condominios

### Objetivos
- ✅ Consistencia visual en toda la app
- ✅ Código mantenible y escalable
- ✅ Accesibilidad garantizada
- ✅ Desarrollo ágil con estándares claros

---

## 2. Estructura de Archivos

### 2.1 Organización por Features

Todas las pantallas deben organizarse en `presentation/features/{feature-name}/`:

```
presentation/features/payments/
├── PaymentsScreen.kt          # Screen composable (stateful)
├── PaymentsViewModel.kt       # ViewModel con MVI
├── PaymentsUiState.kt         # Estado de UI
├── PaymentsIntent.kt          # Acciones del usuario
└── PaymentsEvent.kt           # Eventos one-shot
```

### 2.2 Anatomía de un Screen File

Cada archivo `.kt` de pantalla debe tener **3 partes obligatorias**:

#### Parte 1: Screen Composable (Stateful)
```kotlin
@Composable
fun PaymentsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PaymentsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.managedDialogState.collectAsState()

    // Event collection
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is PaymentsEvent.NavigateToDetail -> onNavigateToDetail(event.paymentId)
                // ... otros eventos
            }
        }
    }

    PaymentsContent(
        uiState = uiState,
        onIntent = viewModel::setIntent,
        dialogState = dialogState
    )
}
```

#### Parte 2: Content Composable (Stateless)
```kotlin
@Composable
private fun PaymentsContent(
    uiState: PaymentsUiState,
    onIntent: (PaymentsIntent) -> Unit,
    dialogState: ManagedDialogConfig? = null
) {
    BaseScreen(
        managedDialogState = dialogState,
        showProgress = uiState.isLoading
    ) {
        // UI Content aquí
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Componentes...
        }
    }
}
```

#### Parte 3: Preview (Obligatorio)
```kotlin
@Preview
@Composable
fun PaymentsScreenPreview() {
    AppTheme {
        PaymentsContent(
            uiState = PaymentsUiState(
                payments = samplePayments()
            ),
            onIntent = {}
        )
    }
}
```

> [!IMPORTANT]
> **BaseScreen SIEMPRE debe estar dentro de `Content`, nunca en `Screen`.**
> Esto permite que los previews funcionen sin necesidad del ViewModel.

---

## 3. BaseScreen

### 3.1 Uso Obligatorio

**TODAS las pantallas deben usar `BaseScreen` como contenedor raíz.**

```kotlin
import org.terratec.altopia.presentation.ui.components.BaseScreen

@Composable
private fun MyContent(...) {
    BaseScreen(
        managedDialogState = dialogState,
        showProgress = uiState.isLoading
    ) {
        // Tu contenido aquí
    }
}
```

### 3.2 Parámetros Disponibles

```kotlin
BaseScreen(
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,              // Muestra loading overlay
    managedDialogState: ManagedDialogConfig? = null,  // Diálogo gestionado
    onDialogDismiss: () -> Unit = {},
    onTapOutside: (() -> Unit)? = null,
    content: @Composable () -> Unit
)
```

### 3.3 Beneficios

- ✅ Background automático con color del theme
- ✅ Gestión centralizada de diálogos
- ✅ Loading overlay consistente
- ✅ Base para futuras features (snackbars, etc.)

---

## 4. Material Design 3 Typography

### 4.1 Jerarquía Completa

> [!NOTE]
> **Los tamaños son de REFERENCIA. SIEMPRE usa `MaterialTheme.typography.{estilo}` en tu código.**

| Estilo | Tamaño | Peso | Uso Recomendado | Ejemplo en App |
|--------|--------|------|-----------------|----------------|
| **Display** | | | |
| `displayLarge` | 57sp | Regular | Números grandes, hero data | Saldo total destacado |
| `displayMedium` | 45sp | Regular | Hero sections | "Bienvenido a {Nombre}" |
| `displaySmall` | 36sp | Regular | Pantallas de onboarding | Título de introducción |
| **Headline** | | | |
| `headlineLarge` | 32sp | Regular | Sección principal | "Gastos del Mes" |
| `headlineMedium` | 28sp | Regular | **Título de pantalla** | "Iniciar Sesión", "Mis Pagos" |
| `headlineSmall` | 24sp | Regular | Títulos de tarjetas grandes | Nombre del condominio |
| **Title** | | | |
| `titleLarge` | 22sp | Medium | Títulos de diálogos | "Confirmar Pago" |
| `titleMedium` | 16sp | Medium | Subtítulos prominentes | Nombre en lista |
| `titleSmall` | 14sp | Medium | Subtítulos pequeños | Categoría de gasto |
| **Body** | | | |
| `bodyLarge` | 16sp | Regular | Texto principal extenso | Descripción de gasto |
| `bodyMedium` | 14sp | Regular | **Texto estándar** | Contenido general |
| `bodySmall` | 12sp | Regular | Texto secundario | Notas, disclaimers |
| **Label** | | | |
| `labelLarge` | 14sp | Medium | **Botones** | Texto en Button |
| `labelMedium` | 12sp | Medium | Chips, badges | "Pagado", "Pendiente" |
| `labelSmall` | 11sp | Medium | Timestamps, metadata | "Hace 2 horas" |

### 4.2 Guía de Selección

#### Para Títulos de Pantalla
```kotlin
Text(
    text = "Gestión de Pagos",
    style = MaterialTheme.typography.headlineMedium,  // 28sp
    color = MaterialTheme.colorScheme.onBackground
)
```

#### Para Subtítulos de Sección
```kotlin
Text(
    text = "Últimos 30 días",
    style = MaterialTheme.typography.titleMedium,  // 16sp
    color = MaterialTheme.colorScheme.onSurfaceVariant
)
```

#### Para Contenido Regular
```kotlin
Text(
    text = "Descripción del gasto común del mes de noviembre...",
    style = MaterialTheme.typography.bodyMedium,  // 14sp
    color = MaterialTheme.colorScheme.onSurface
)
```

#### Para Metadata/Timestamps
```kotlin
Text(
    text = "Hace 2 horas",
    style = MaterialTheme.typography.labelSmall,  // 11sp
    color = MaterialTheme.colorScheme.onSurfaceVariant
)
```

### 4.3 Reglas Importantes

> [!IMPORTANT]
> **Reglas de Tipografía**
> 1. NUNCA usar `fontSize` directamente → Usa `MaterialTheme.typography.{estilo}`
> 2. NUNCA hardcodear `fontWeight` → Ya está incluido en el estilo
> 3. SIEMPRE especificar `color` del theme → Garantiza contraste correcto
> 4. Máximo 2-3 niveles de jerarquía por pantalla → Evita confusión visual

---

## 5. Color System

### 5.1 Roles Semánticos

> [!WARNING]
> **NUNCA uses `Color(0xFFXXXXXX)` directamente. SIEMPRE usa `MaterialTheme.colorScheme.{role}`**

| Color Role | Cuándo Usar | Ejemplo en App | Código |
|------------|-------------|----------------|--------|
| **Primary Colors** | | | |
| `primary` | Acciones principales, FABs | Botón "Pagar Ahora" | `MaterialTheme.colorScheme.primary` |
| `onPrimary` | Texto/iconos sobre primary | Texto en botón principal | `MaterialTheme.colorScheme.onPrimary` |
| `primaryContainer` | Fondos de elementos destacados | Card de pago destacado | `MaterialTheme.colorScheme.primaryContainer` |
| `onPrimaryContainer` | Texto sobre primaryContainer | Texto en card destacado | `MaterialTheme.colorScheme.onPrimaryContainer` |
| **Secondary Colors** | | | |
| `secondary` | Acciones secundarias | Botón "Filtrar" | `MaterialTheme.colorScheme.secondary` |
| `onSecondary` | Texto sobre secondary | Texto en botón secundario | `MaterialTheme.colorScheme.onSecondary` |
| `secondaryContainer` | Fondos de elementos menos prominentes | Chip de filtro seleccionado | `MaterialTheme.colorScheme.secondaryContainer` |
| **Tertiary Colors** | | | |
| `tertiary` | Acentos, contrastes | Alerta de gasto alto | `MaterialTheme.colorScheme.tertiary` |
| `onTertiary` | Texto sobre tertiary | Texto en alerta | `MaterialTheme.colorScheme.onTertiary` |
| **Surface Colors** | | | |
| `surface` | Fondo de Cards, Dialogs | Card de gasto | `MaterialTheme.colorScheme.surface` |
| `onSurface` | Texto principal sobre surface | Texto en cards | `MaterialTheme.colorScheme.onSurface` |
| `surfaceVariant` | Surfaces de menor énfasis | TextField background | `MaterialTheme.colorScheme.surfaceVariant` |
| `onSurfaceVariant` | Texto secundario | Labels, hints | `MaterialTheme.colorScheme.onSurfaceVariant` |
| **Background** | | | |
| `background` | Fondo de pantalla (BaseScreen) | Color de fondo general | `MaterialTheme.colorScheme.background` |
| `onBackground` | Texto sobre background | Títulos de pantalla | `MaterialTheme.colorScheme.onBackground` |
| **Error** | | | |
| `error` | Estados de error | Pago vencido | `MaterialTheme.colorScheme.error` |
| `onError` | Texto sobre error | Texto en error badge | `MaterialTheme.colorScheme.onError` |
| `errorContainer` | Fondos de error | Banner de error | `MaterialTheme.colorScheme.errorContainer` |

### 5.2 Ejemplos Prácticos

#### Botón Principal
```kotlin
Button(
    onClick = { onIntent(PaymentsIntent.PayNow) },
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
) {
    Text("Pagar Ahora")  // Usa onPrimary automáticamente
}
```

#### Card de Contenido
```kotlin
Card(
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
) {
    Text(
        text = "Gasto Común",
        color = MaterialTheme.colorScheme.onSurface
    )
}
```

#### Estado de Error
```kotlin
Text(
    text = "Pago vencido",
    color = MaterialTheme.colorScheme.error,
    style = MaterialTheme.typography.labelMedium
)
```

### 5.3 Jerarquía de Surfaces

Para crear profundidad visual, usa la jerarquía de containers:

```kotlin
// Nivel 0 (más bajo)
MaterialTheme.colorScheme.surfaceContainerLowest

// Nivel 1
MaterialTheme.colorScheme.surfaceContainerLow

// Nivel 2 (estándar)
MaterialTheme.colorScheme.surfaceContainer

// Nivel 3
MaterialTheme.colorScheme.surfaceContainerHigh

// Nivel 4 (más alto)
MaterialTheme.colorScheme.surfaceContainerHighest
```

---

## 6. Componentes Material

### 6.1 Text

```kotlin
// Título de pantalla
Text(
    text = "Mi Título",
    style = MaterialTheme.typography.headlineMedium,
    color = MaterialTheme.colorScheme.onBackground
)

// Texto de contenido
Text(
    text = "Descripción...",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurface
)

// Texto secundario
Text(
    text = "Información adicional",
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant
)
```

### 6.2 Buttons

#### Button (Principal)
```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier.fillMaxWidth()
) {
    Text("Acción Principal")
}
```

#### FilledTonalButton (Secundario)
```kotlin
FilledTonalButton(
    onClick = { /* action */ },
    modifier = Modifier.fillMaxWidth()
) {
    Text("Acción Secundaria")
}
```

#### OutlinedButton (Terciario)
```kotlin
OutlinedButton(
    onClick = { /* action */ },
    modifier = Modifier.fillMaxWidth()
) {
    Text("Cancelar")
}
```

#### TextButton (Mínimo)
```kotlin
TextButton(onClick = { /* action */ }) {
    Text("Olvidé mi contraseña")
}
```

### 6.3 TextFields

#### OutlinedTextField (Recomendado)
```kotlin
OutlinedTextField(
    value = uiState.email,
    onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
    label = { Text("Email") },
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
    enabled = !uiState.isLoading,
    isError = !uiState.isEmailValid,
    supportingText = if (!uiState.isEmailValid) {
        { Text("Email inválido") }
    } else null,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next
    )
)
```

### 6.4 Cards

```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Gasto Común",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$150.00",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
```

### 6.5 Divider

```kotlin
HorizontalDivider(
    modifier = Modifier.padding(vertical = 8.dp),
    color = MaterialTheme.colorScheme.outlineVariant
)
```

### 6.6 Material Icons

#### 6.6.1 Dependencia Necesaria

Para usar Material Icons en Compose Multiplatform, agrega en `build.gradle.kts`:

```kotlin
commonMain.dependencies {
    implementation(compose.materialIconsExtended)
}
```

> [!NOTE]
> `compose.materialIconsExtended` incluye todos los iconos de Material Design. Si solo necesitas iconos básicos, usa `compose.material` (más ligero pero con menos iconos).

#### 6.6.2 Imports

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
```

#### 6.6.3 Categorías de Iconos

Material Icons tiene 5 variantes visuales:

| Categoría | Importación | Uso Recomendado |
|-----------|-------------|-----------------|
| **Filled** | `Icons.Filled.{IconName}` o `Icons.Default.{IconName}` | **Más común**, usa este por defecto |
| **Outlined** | `Icons.Outlined.{IconName}` | Para UI más ligera visualmente |
| **Rounded** | `Icons.Rounded.{IconName}` | Para diseños con bordes suaves |
| **TwoTone** | `Icons.TwoTone.{IconName}` | Para iconos con dos tonos de color |
| **Sharp** | `Icons.Sharp.{IconName}` | Para diseños angulares, modernos |

> [!TIP]
> **Usa `Icons.Default.*` (alias de `Icons.Filled.*`) para la mayoría de casos.**

#### 6.6.4 Uso Básico de Icon

```kotlin
Icon(
    imageVector = Icons.Default.Email,
    contentDescription = "Email icon",
    tint = MaterialTheme.colorScheme.onSurface
)
```

**Parámetros importantes:**
- `imageVector`: El icono de `Icons.*.*`
- `contentDescription`: **OBLIGATORIO** para accesibilidad
- `tint`: Color del icono (por defecto usa `LocalContentColor`)
- `modifier`: Para tamaño y posicionamiento

#### 6.6.5 Iconos en TextField

##### LeadingIcon
```kotlin
OutlinedTextField(
    value = email,
    onValueChange = onEmailChange,
    label = { Text("Email") },
    leadingIcon = {
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = "Email icon"
        )
    }
)
```

##### TrailingIcon con Acción
```kotlin
OutlinedTextField(
    value = password,
    onValueChange = onPasswordChange,
    label = { Text("Contraseña") },
    leadingIcon = {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Password icon"
        )
    },
    trailingIcon = {
        IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(
                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
            )
        }
    }
)
```

#### 6.6.6 Iconos en Buttons

```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier.fillMaxWidth()
) {
    Icon(
        imageVector = Icons.Default.Send,
        contentDescription = null,  // null porque el botón ya tiene texto
        modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text("Enviar")
}
```

#### 6.6.7 IconButton

Para acciones solo con icono:

```kotlin
IconButton(onClick = { /* action */ }) {
    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "Eliminar",
        tint = MaterialTheme.colorScheme.error
    )
}
```

#### 6.6.8 Tamaños de Iconos

```kotlin
// Pequeño (16.dp)
Icon(
    imageVector = Icons.Default.Check,
    contentDescription = "Success",
    modifier = Modifier.size(16.dp)
)

// Estándar (24.dp - por defecto)
Icon(
    imageVector = Icons.Default.Home,
    contentDescription = "Home"
)

// Grande (32.dp)
Icon(
    imageVector = Icons.Default.Error,
    contentDescription = "Error",
    modifier = Modifier.size(32.dp)
)
```

#### 6.6.9 Iconos Comunes para la App

| Funcionalidad | Icono | Import |
|---------------|-------|--------|
| Email/Correo | ✉️ | `Icons.Default.Email` |
| Contraseña | 🔒 | `Icons.Default.Lock` |
| Visibilidad On | 👁️ | `Icons.Default.Visibility` |
| Visibilidad Off | 👁️‍🗨️ | `Icons.Default.VisibilityOff` |
| Usuario | 👤 | `Icons.Default.Person` |
| Inicio | 🏠 | `Icons.Default.Home` |
| Pagos | 💳 | `Icons.Default.Payment` |
| Dinero | 💵 | `Icons.Default.AttachMoney` |
| Calendario | 📅 | `Icons.Default.CalendarToday` |
| Agregar | ➕ | `Icons.Default.Add` |
| Editar | ✏️ | `Icons.Default.Edit` |
| Eliminar | 🗑️ | `Icons.Default.Delete` |
| Buscar | 🔍 | `Icons.Default.Search` |
| Filtrar | 🔽 | `Icons.Default.FilterList` |
| Menú | ☰ | `Icons.Default.Menu` |
| Atrás | ← | `Icons.Default.ArrowBack` |
| Cerrar | ✖️ | `Icons.Default.Close` |
| Check/Éxito | ✔️ | `Icons.Default.Check` |
| Error | ⚠️ | `Icons.Default.Error` |
| Info | ℹ️ | `Icons.Default.Info` |
| Configuración | ⚙️ | `Icons.Default.Settings` |

#### 6.6.10 Content Description para Accesibilidad

> [!IMPORTANT]
> **SIEMPRE proporciona `contentDescription` excepto cuando:**
> 1. El icono es puramente decorativo y está acompañado de texto
> 2. En ese caso, usa `contentDescription = null`

**Buenas prácticas:**

```kotlin
// ✅ Correcto - Icono con función
Icon(
    imageVector = Icons.Default.Delete,
    contentDescription = "Eliminar pago"  // Describe la acción
)

// ✅ Correcto - Icono decorativo con texto adyacente
Button(onClick = { }) {
    Icon(
        imageVector = Icons.Default.Send,
        contentDescription = null  // El texto del botón ya describe la acción
    )
    Text("Enviar")
}

// ❌ Incorrecto - Falta contentDescription
Icon(
    imageVector = Icons.Default.Warning,
    // contentDescription falta!
)
```

#### 6.6.11 Dónde Encontrar Más Iconos

- **Material Icons Library**: [fonts.google.com/icons](https://fonts.google.com/icons)
- Busca el icono que necesitas
- Usa el nombre en `camelCase`: `home` → `Icons.Default.Home`

**Ejemplo de búsqueda:**
1. Busca "payment" en fonts.google.com/icons
2. Encuentra "Payment"
3. Usa: `Icons.Default.Payment`

> [!TIP]
> **Convención de nombres:** El nombre del icono en la web se convierte a PascalCase en código.
> - `account_circle` → `Icons.Default.AccountCircle`
> - `arrow_back` → `Icons.Default.ArrowBack`

---

## 7. Spacing y Layout

### 7.1 Sistema de Espaciado

Usa múltiplos de 4dp para consistencia:

| Nombre | Valor | Cuándo Usar |
|--------|-------|-------------|
| `Tiny` | 4.dp | Separación mínima entre elementos relacionados |
| `Small` | 8.dp | Padding interno de componentes pequeños |
| `Medium` | 16.dp | **Padding estándar de pantalla y cards** |
| `Large` | 24.dp | Separación entre secciones |
| `ExtraLarge` | 32.dp | Márgenes grandes, espaciado hero |

### 7.2 Padding de Pantalla

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)  // Padding estándar
) {
    // Contenido
}
```

### 7.3 Espaciado Entre Elementos

```kotlin
Column {
    Text("Título")
    Spacer(modifier = Modifier.height(8.dp))   // Pequeño
    Text("Subtítulo")
    Spacer(modifier = Modifier.height(24.dp))  // Entre secciones
    
    // Nueva sección
    Text("Otra Sección")
}
```

### 7.4 Layout Patterns Comunes

#### Lista de Items
```kotlin
LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(payments) { payment ->
        PaymentCard(payment)
    }
}
```

#### Formulario
```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    OutlinedTextField(/* ... */)
    OutlinedTextField(/* ... */)
    Button(/* ... */)
}
```

---

## 8. Previews

### 8.1 Preview Básico (Obligatorio)

TODAS las pantallas deben tener al menos 1 preview:

```kotlin
@Preview(showBackground = true)
@Composable
fun PaymentsScreenPreview() {
    AppTheme {
        PaymentsContent(
            uiState = PaymentsUiState(
                payments = listOf(
                    Payment(
                        id = "1",
                        concept = "Gasto Común",
                        amount = 150.0,
                        status = PaymentStatus.PENDING
                    )
                )
            ),
            onIntent = {}
        )
    }
}
```

### 8.2 Previews de Estados (Opcional)

Para casos importantes, agrega previews de estados:

```kotlin
// Estado de loading
@Preview(name = "Loading", showBackground = true)
@Composable
fun PaymentsScreenLoadingPreview() {
    AppTheme {
        PaymentsContent(
            uiState = PaymentsUiState(isLoading = true),
            onIntent = {}
        )
    }
}

// Estado vacío
@Preview(name = "Empty", showBackground = true)
@Composable
fun PaymentsScreenEmptyPreview() {
    AppTheme {
        PaymentsContent(
            uiState = PaymentsUiState(payments = emptyList()),
            onIntent = {}
        )
    }
}
```

### 8.3 Preview Dark Mode (Recomendado)

```kotlin
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PaymentsScreenDarkPreview() {
    AppTheme(darkTheme = true) {
        PaymentsContent(
            uiState = PaymentsUiState(
                payments = samplePayments()
            ),
            onIntent = {}
        )
    }
}
```

### 8.4 Límites de Previews

> [!CAUTION]
> **Máximo 3 previews por pantalla**
> - 1 preview básico (obligatorio)
> - 1-2 previews de casos extremos (opcional)
> - Demasiados previews ralentizan el IDE

---

## 9. Ejemplo Completo

Pantalla de listado de pagos con todos los estándares aplicados:

```kotlin
package org.terratec.altopia.presentation.features.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.terratec.altopia.domain.model.Payment
import org.terratec.altopia.presentation.model.ManagedDialogConfig
import org.terratec.altopia.presentation.ui.components.BaseScreen
import org.terratec.altopia.presentation.ui.theme.AppTheme

// ===== PARTE 1: Screen (Stateful) =====
@Composable
fun PaymentsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: PaymentsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.managedDialogState.collectAsState()

    // Event collection
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is PaymentsEvent.NavigateToDetail -> onNavigateToDetail(event.paymentId)
                PaymentsEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    PaymentsContent(
        uiState = uiState,
        onIntent = viewModel::setIntent,
        dialogState = dialogState
    )
}

// ===== PARTE 2: Content (Stateless) =====
@Composable
private fun PaymentsContent(
    uiState: PaymentsUiState,
    onIntent: (PaymentsIntent) -> Unit,
    dialogState: ManagedDialogConfig? = null
) {
    BaseScreen(
        managedDialogState = dialogState,
        showProgress = uiState.isLoading
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título de pantalla
            Text(
                text = "Mis Pagos",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Últimos 30 días",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de pagos
            if (uiState.payments.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.payments) { payment ->
                        PaymentCard(
                            payment = payment,
                            onClick = { onIntent(PaymentsIntent.PaymentClicked(payment.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentCard(
    payment: Payment,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.concept,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = payment.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(
                    text = "$${payment.amount}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                PaymentStatusBadge(status = payment.status)
            }
        }
    }
}

@Composable
private fun PaymentStatusBadge(status: PaymentStatus) {
    val (text, color) = when (status) {
        PaymentStatus.PAID -> "Pagado" to MaterialTheme.colorScheme.primary
        PaymentStatus.PENDING -> "Pendiente" to MaterialTheme.colorScheme.tertiary
        PaymentStatus.OVERDUE -> "Vencido" to MaterialTheme.colorScheme.error
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = color
    )
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = "No hay pagos",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tus pagos aparecerán aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ===== PARTE 3: Preview =====
@Preview(showBackground = true)
@Composable
fun PaymentsScreenPreview() {
    AppTheme {
        PaymentsContent(
            uiState = PaymentsUiState(
                payments = listOf(
                    Payment(
                        id = "1",
                        concept = "Gasto Común",
                        amount = 150.0,
                        date = "15 Nov 2025",
                        status = PaymentStatus.PAID
                    ),
                    Payment(
                        id = "2",
                        concept = "Fondo de Reserva",
                        amount = 50.0,
                        date = "15 Nov 2025",
                        status = PaymentStatus.PENDING
                    )
                )
            ),
            onIntent = {}
        )
    }
}
```

---

## 10. Checklist

Antes de considerar una pantalla completa, verifica:

### Estructura
- [ ] Archivo ubicado en `presentation/features/{feature}/`
- [ ] Nomenclatura según MVI guide
- [ ] 3 partes: Screen (stateful), Content (stateless), Preview
- [ ] BaseScreen dentro de Content

### MVI Pattern
- [ ] UiState, Intent, Event definidos
- [ ] ViewModel extiende BaseViewModel
- [ ] Events colectados en LaunchedEffect
- [ ] Intents enviados via `viewModel::setIntent`

### Material Design
- [ ] Typography usando `MaterialTheme.typography.{estilo}`
- [ ] Colores usando `MaterialTheme.colorScheme.{role}`
- [ ] Sin valores hardcodeados (`fontSize`, `Color(0xFF...)`)
- [ ] Spacing consistente (múltiplos de 4dp)

### BaseScreen
- [ ] BaseScreen como contenedor raíz en Content
- [ ] `managedDialogState` pasado desde Screen
- [ ] `showProgress` vinculado a `uiState.isLoading`

### Accesibilidad
- [ ] Contraste suficiente (usando on-colors correctos)
- [ ] Tamaños de texto apropiados
- [ ] Áreas de toque adecuadas (min 48dp)

### Previews
- [ ] Al menos 1 preview básico funcional
- [ ] Máximo 3 previews totales
- [ ] Preview usa datos de ejemplo realistas
- [ ] Preview envuelto en `AppTheme`

### Código Limpio
- [ ] Composables pequeños y enfocados
- [ ] Nombres descriptivos
- [ ] Sin lógica de negocio en la UI
- [ ] Comentarios solo donde sea necesario

---

## Recursos Adicionales

- [MVI Implementation Guide](file:///Users/albertmontesanccasi/androidProject/Altopia/docs/MVI_IMPLEMENTATION_GUIDE.md)
- [Material Design 3 Typography](https://m3.material.io/styles/typography/overview)
- [Material Design 3 Color](https://m3.material.io/styles/color/overview)
- [Jetpack Compose Layouts](https://developer.android.com/jetpack/compose/layouts)

---

**Última actualización**: 2025-11-23  
**Versión**: 1.0
