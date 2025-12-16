package org.terratec.altopia.presentation.features.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.terratec.altopia.domain.model.AuthSession
import org.terratec.altopia.presentation.model.ManagedDialogConfig
import org.terratec.altopia.presentation.ui.components.BaseScreen
import org.terratec.altopia.presentation.ui.theme.AppTheme

// ===== PARTE 1: Screen (Stateful) =====
/**
 * Login screen with email and password authentication.
 */
@Composable
fun LoginScreen(
    onNavigateToHome: (AuthSession) -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToProfileSelection: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.managedDialogState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Event collection
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> onNavigateToHome(event.session)
                LoginEvent.NavigateToForgotPassword -> onNavigateToForgotPassword()
                LoginEvent.NavigateToProfileSelection -> onNavigateToProfileSelection()
                LoginEvent.NavigateToAdminDashboard -> onNavigateToAdminDashboard()
                is LoginEvent.ShowToast -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    LoginContent(
        uiState = uiState,
        onIntent = viewModel::setIntent,
        dialogState = dialogState
    )
}

// ===== PARTE 2: Content (Stateless) =====
/**
 * Stateless content composable for Login screen.
 */
@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    dialogState: ManagedDialogConfig? = null
) {
    BaseScreen(
        managedDialogState = dialogState,
        showProgress = uiState.isLoading
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top spacing
            Spacer(modifier = Modifier.height(48.dp))

            // Logo/Brand area
            BrandSection()

            Spacer(modifier = Modifier.height(48.dp))

            // Welcome section
            WelcomeSection()

            Spacer(modifier = Modifier.height(32.dp))

            // Login form
            LoginForm(
                email = uiState.email,
                password = uiState.password,
                isEmailValid = uiState.isEmailValid,
                isPasswordValid = uiState.isPasswordValid,
                errorMessage = uiState.errorMessage,
                isLoading = uiState.isLoading,
                onEmailChange = { onIntent(LoginIntent.EmailChanged(it)) },
                onPasswordChange = { onIntent(LoginIntent.PasswordChanged(it)) },
                onLoginClick = { onIntent(LoginIntent.SubmitCredentials) },
                onForgotPasswordClick = { onIntent(LoginIntent.ForgotPasswordClicked) }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun BrandSection() {
    // Placeholder for logo - could be replaced with actual logo image
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo placeholder (you can replace with actual logo)
        Text(
            text = "🏢",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Altopia",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WelcomeSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Gestiona tu condominio de manera simple y eficiente",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoginForm(
    email: String,
    password: String,
    isEmailValid: Boolean,
    isPasswordValid: Boolean,
    errorMessage: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Correo electrónico") },
            placeholder = { Text("nombre@ejemplo.com") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email icon"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            isError = !isEmailValid,
            supportingText = if (!isEmailValid) {
                { 
                    Text(
                        text = "Por favor, ingresa un correo válido",
                        style = MaterialTheme.typography.bodySmall
                    ) 
                }
            } else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña") },
            placeholder = { Text("Tu contraseña") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password icon"
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            isError = !isPasswordValid,
            supportingText = if (!isPasswordValid) {
                { 
                    Text(
                        text = "La contraseña debe tener al menos 6 caracteres",
                        style = MaterialTheme.typography.bodySmall
                    ) 
                }
            } else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { 
                    focusManager.clearFocus()
                    if (email.isNotBlank() && password.isNotBlank()) {
                        onLoginClick()
                    }
                }
            )
        )

        // Error Message
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Login Button
        Button(
            onClick = onLoginClick,
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Forgot Password
        TextButton(
            onClick = onForgotPasswordClick,
            enabled = !isLoading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ===== PARTE 3: Preview =====
@Preview
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginContent(
            uiState = LoginUiState(
                email = "",
                password = "",
                isLoading = false,
                errorMessage = null
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
fun LoginScreenErrorPreview() {
    AppTheme {
        LoginContent(
            uiState = LoginUiState(
                email = "test@example.com",
                password = "12345",
                isLoading = false,
                errorMessage = "Credenciales incorrectas. Por favor, verifica tus datos.",
                isEmailValid = true,
                isPasswordValid = false
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
fun LoginScreenLoadingPreview() {
    AppTheme {
        LoginContent(
            uiState = LoginUiState(
                email = "user@example.com",
                password = "password123",
                isLoading = true
            ),
            onIntent = {}
        )
    }
}
