package org.terratec.altopia.presentation.features.profile_selection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ProfileSelectionContract.Event.NavigateToOwnerHome -> onNavigateToOwnerHome()
                ProfileSelectionContract.Event.NavigateToAdminDashboard -> onNavigateToAdminDashboard()
            }
        }
    }

    ProfileSelectionContent(
        uiState = uiState,
        onIntent = viewModel::setIntent
    )
}

@Composable
private fun ProfileSelectionContent(
    uiState: ProfileSelectionContract.UiState,
    onIntent: (ProfileSelectionContract.Intent) -> Unit
) {
    BaseScreen(
        showProgress = uiState.isLoading
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                "Selecciona un Perfil",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Continua como ${uiState.userName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.availableOptions) { option ->
                    ProfileOptionCard(
                        option = option,
                        isSelected = uiState.selectedOption?.id == option.id,
                        onClick = { onIntent(ProfileSelectionContract.Intent.SelectOption(option)) }
                    )
                }
            }

            Button(
                onClick = { onIntent(ProfileSelectionContract.Intent.ConfirmSelection) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState.selectedOption != null
            ) {
                Text("Continuar")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileOptionCard(
    option: ProfileSelectionContract.ProfileOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) 
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
        else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (option.type == RoleType.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Apartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = option.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (isSelected) "Seleccionado" else "No seleccionado",
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview
@Composable
fun ProfileSelectionPreview() {
    AppTheme {
        ProfileSelectionContent(
            uiState = ProfileSelectionContract.UiState(
                userName = "Albert Montes",
                availableOptions = listOf(
                    ProfileSelectionContract.ProfileOption("1", RoleType.ADMIN, "Administrador", "Acceso Global"),
                    ProfileSelectionContract.ProfileOption("2", RoleType.PROPIETARIO, "A-302", "Condominio Las Palmeras"),
                    ProfileSelectionContract.ProfileOption("3", RoleType.PROPIETARIO, "B-101", "Condominio Los Pinos")
                ),
                selectedOption = ProfileSelectionContract.ProfileOption("1", RoleType.ADMIN, "Administrador", "Acceso Global")
            ),
            onIntent = {}
        )
    }
}
