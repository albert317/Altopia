package org.terratec.altopia.presentation.features.forgotpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.terratec.altopia.presentation.model.ManagedDialogConfig
import org.terratec.altopia.presentation.ui.components.BaseScreen
import org.terratec.altopia.presentation.ui.theme.AppTheme

// ===== PARTE 1: Screen (Stateful) =====
/**
 * Forgot Password screen for password recovery flow.
 */
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.managedDialogState.collectAsState()

    // Event collection
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ForgotPasswordEvent.NavigateToLogin -> onNavigateBack()
            }
        }
    }

    ForgotPasswordContent(
        uiState = uiState,
        onIntent = viewModel::setIntent,
        dialogState = dialogState
    )
}

// ===== PARTE 2: Content (Stateless) =====
/**
 * Stateless content composable for Forgot Password screen.
 */
@Composable
private fun ForgotPasswordContent(
    uiState: ForgotPasswordUiState,
    onIntent: (ForgotPasswordIntent) -> Unit,
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
            // Top app bar with back button
            IconButton(
                onClick = { onIntent(ForgotPasswordIntent.NavigateBack) },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand/Icon section
            Text(
                text = "🔐",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Recuperar Contraseña",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = "Te enviaremos un email con instrucciones para restablecer tu contraseña",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email form
            ForgotPasswordForm(
                email = uiState.email,
                isEmailValid = uiState.isEmailValid,
                errorMessage = uiState.errorMessage,
                isLoading = uiState.isLoading,
                onEmailChange = { onIntent(ForgotPasswordIntent.EmailChanged(it)) },
                onSubmit = { onIntent(ForgotPasswordIntent.SubmitEmail) },
                onBackToLogin = { onIntent(ForgotPasswordIntent.NavigateBack) }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ForgotPasswordForm(
    email: String,
    isEmailValid: Boolean,
    errorMessage: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxWidth()
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
            isError = !isEmailValid || errorMessage != null,
            supportingText = if (!isEmailValid || errorMessage != null) {
                {
                    Text(
                        text = errorMessage ?: "Por favor, ingresa un correo válido",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (email.isNotBlank()) {
                        onSubmit()
                    }
                }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = onSubmit,
            enabled = !isLoading && email.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Enviar instrucciones",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back to Login
        TextButton(
            onClick = onBackToLogin,
            enabled = !isLoading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Volver al inicio de sesión",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ===== PARTE 3: Preview =====
@Preview
@Composable
fun ForgotPasswordScreenPreview() {
    AppTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(
                email = "",
                isLoading = false
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
fun ForgotPasswordScreenWithEmailPreview() {
    AppTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(
                email = "user@example.com",
                isLoading = false
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
fun ForgotPasswordScreenErrorPreview() {
    AppTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(
                email = "invalid-email",
                isLoading = false,
                isEmailValid = false,
                errorMessage = "Por favor, ingresa un correo válido"
            ),
            onIntent = {}
        )
    }
}
