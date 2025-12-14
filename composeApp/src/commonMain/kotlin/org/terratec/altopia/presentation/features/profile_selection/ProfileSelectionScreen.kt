package org.terratec.altopia.presentation.features.profile_selection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.terratec.altopia.domain.model.RoleType
import org.terratec.altopia.presentation.ui.components.BaseScreen
import org.terratec.altopia.presentation.ui.theme.AppTheme

@Composable
fun ProfileSelectionScreen(
    onNavigateToOwnerHome: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit,
    viewModel: ProfileSelectionViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val managedDialogState by viewModel.managedDialogState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ProfileSelectionEvent.NavigateToAdminDashboard -> onNavigateToAdminDashboard()
                ProfileSelectionEvent.NavigateToOwnerHome -> onNavigateToOwnerHome()
            }
        }
    }

    BaseScreen(
        showProgress = uiState.isLoading,
        managedDialogState = managedDialogState,
        onDialogDismiss = viewModel::dismissDialog
    ) {
        ProfileSelectionContent(
            uiState = uiState,
            onIntent = viewModel::setIntent
        )
    }
}

@Composable
private fun ProfileSelectionContent(
    uiState: ProfileSelectionUiState,
    onIntent: (ProfileSelectionIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Selecciona tu perfil",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Hola, ${uiState.userName}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            items(uiState.availableOptions) { option ->
                ProfileOptionCard(
                    option = option,
                    isSelected = uiState.selectedOption == option,
                    onSelect = { onIntent(ProfileSelectionIntent.SelectOption(option)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onIntent(ProfileSelectionIntent.ConfirmSelection) },
            enabled = uiState.selectedOption != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }
    }
}

@Composable
private fun ProfileOptionCard(
    option: ProfileOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        ListItem(
            headlineContent = { Text(option.title) },
            supportingContent = { option.subtitle?.let { Text(it) } },
            leadingContent = {
                Icon(
                    imageVector = when (option.type) {
                        RoleType.ADMIN -> Icons.Default.Security
                        RoleType.PROPIETARIO -> Icons.Default.Apartment
                        else -> Icons.Default.Person
                    },
                    contentDescription = null
                )
            },
            trailingContent = {
                RadioButton(
                    selected = isSelected,
                    onClick = null // Handled by Card click
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Preview
@Composable
fun ProfileSelectionPreview() {
    AppTheme {
        ProfileSelectionContent(
            uiState = ProfileSelectionUiState(
                userName = "Albert Montes",
                availableOptions = listOf(
                    ProfileOption("1", RoleType.ADMIN, "Administrador", "Acceso Global"),
                    ProfileOption("2", RoleType.PROPIETARIO, "A-302", "Condominio Las Palmeras"),
                    ProfileOption("3", RoleType.PROPIETARIO, "B-101", "Condominio Los Pinos")
                ),
                selectedOption = ProfileOption(
                    "1",
                    RoleType.ADMIN,
                    "Administrador",
                    "Acceso Global"
                )
            ),
            onIntent = {}
        )
    }
}
