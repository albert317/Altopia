package org.terratec.altopia.presentation.features.resetpassword

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.terratec.altopia.presentation.model.ManagedDialogConfig
import org.terratec.altopia.presentation.ui.components.BaseScreen
import org.terratec.altopia.presentation.ui.theme.AppTheme

@Composable
fun ResetPasswordScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ResetPasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.managedDialogState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ResetPasswordEvent.NavigateToHome -> onNavigateToHome()
                ResetPasswordEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    ResetPasswordContent(
        uiState = uiState,
        onIntent = viewModel::setIntent,
        dialogState = dialogState
    )
}

@Composable
private fun ResetPasswordContent(
    uiState: ResetPasswordUiState,
    onIntent: (ResetPasswordIntent) -> Unit,
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
            Spacer(modifier = Modifier.height(48.dp))

            // Brand/Icon section
            Text(
                text = "🔐",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Nueva Contraseña",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = "Ingresa tu nueva contraseña a continuación",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            ResetPasswordForm(
                uiState = uiState,
                onIntent = onIntent
            )
        }
    }
}

@Composable
private fun ResetPasswordForm(
    uiState: ResetPasswordUiState,
    onIntent: (ResetPasswordIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Password Field
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { onIntent(ResetPasswordIntent.PasswordChanged(it)) },
            label = { Text("Nueva contraseña") },
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
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Field
        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = { onIntent(ResetPasswordIntent.ConfirmPasswordChanged(it)) },
            label = { Text("Confirmar contraseña") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            isError = uiState.errorMessage != null,
            supportingText = if (uiState.errorMessage != null) {
                {
                    Text(
                        text = uiState.errorMessage,
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
                    onIntent(ResetPasswordIntent.SubmitPassword)
                }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = { onIntent(ResetPasswordIntent.SubmitPassword) },
            enabled = !uiState.isLoading && uiState.password.isNotBlank() && uiState.confirmPassword.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Actualizar contraseña",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview
@Composable
fun ResetPasswordScreenPreview() {
    AppTheme {
        ResetPasswordContent(
            uiState = ResetPasswordUiState(),
            onIntent = {}
        )
    }
}
