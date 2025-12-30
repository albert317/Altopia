package org.terratec.altopia.presentation.features.admindashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.terratec.altopia.presentation.features.admindashboard.components.DashboardOptionCard
import org.terratec.altopia.presentation.features.admindashboard.components.StatusSummaryCard
import org.terratec.altopia.presentation.ui.components.BaseScreen
import org.terratec.altopia.presentation.ui.theme.AppTheme

@Composable
fun AdminDashboardScreen(
    onNavigateToUnits: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToDistribution: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    viewModel: AdminDashboardViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                AdminDashboardEvent.NavigateToUnits -> onNavigateToUnits()
                AdminDashboardEvent.NavigateToUsers -> onNavigateToUsers()
                AdminDashboardEvent.NavigateToDistribution -> onNavigateToDistribution()
                AdminDashboardEvent.NavigateToExpenses -> onNavigateToExpenses()
                AdminDashboardEvent.NavigateToTransactions -> onNavigateToTransactions()
            }
        }
    }

    AdminDashboardContent(
        uiState = uiState,
        onIntent = viewModel::setIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminDashboardContent(
    uiState: AdminDashboardUiState,
    onIntent: (AdminDashboardIntent) -> Unit
) {
    BaseScreen(
        showProgress = uiState.isLoading
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Panel de Administrador") }
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent // Let BaseScreen background show through if needed, or keep Scaffold default
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Section: Status Summary
                Text(
                    text = "Resumen del Mes",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusSummaryCard(
                        title = "Recaudación",
                        value = "S/ ${uiState.collectionAmount}",
                        statusText = "${uiState.collectionPercentage}% del objetivo",
                        icon = Icons.Default.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                    StatusSummaryCard(
                        title = "Validaciones",
                        value = "${uiState.pendingValidations} Pendientes",
                        statusText = if (uiState.pendingValidations > 0) "Requiere acción" else "Todo al día",
                        statusColor = if (uiState.pendingValidations > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.FactCheck,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusSummaryCard(
                        title = "Morosidad",
                        value = "S/ ${uiState.overdueAmount}",
                        statusText = "${uiState.overdueCount} Unidades",
                        statusColor = MaterialTheme.colorScheme.error,
                        icon = Icons.Default.Warning,
                        modifier = Modifier.weight(1f)
                    )
                    StatusSummaryCard(
                        title = "Periodo Actual",
                        value = uiState.currentPeriod.ifEmpty { "N/A" },
                        statusText = uiState.periodStatus,
                        icon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    )
                }


                Spacer(modifier = Modifier.height(24.dp))

                // Section: Quick Actions / Menu
                Text(
                    text = "Gestión",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Row 1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardOptionCard(
                        title = "Unidades",
                        description = "Gestionar propiedades y residentes",
                        icon = Icons.Default.Apartment,
                        onClick = { onIntent(AdminDashboardIntent.NavigateToUnits) },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardOptionCard(
                        title = "Usuarios",
                        description = "Directorio y roles de acceso",
                        icon = Icons.Default.People,
                        onClick = { onIntent(AdminDashboardIntent.NavigateToUsers) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Row 2
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardOptionCard(
                        title = "Distribución",
                        description = "Periodos y cálculo de cuotas",
                        icon = Icons.Default.Calculate,
                        onClick = { onIntent(AdminDashboardIntent.NavigateToDistribution) },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardOptionCard(
                        title = "Gastos",
                        description = "Registro de egresos y facturas",
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        onClick = { onIntent(AdminDashboardIntent.NavigateToExpenses) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Row 3
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardOptionCard(
                        title = "Transacciones",
                        description = "Conciliación de pagos",
                        icon = Icons.Default.Payments,
                        onClick = { onIntent(AdminDashboardIntent.NavigateToTransactions) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview
@Composable
fun AdminDashboardScreenPreview() {
    AppTheme {
        AdminDashboardContent(
            uiState = AdminDashboardUiState(
                collectionAmount = 12450.00,
                collectionTarget = 15000.00,
                pendingValidations = 3,
                overdueAmount = 1200.00,
                overdueCount = 5,
                currentPeriod = "Dic 2025",
                periodStatus = "En Proceso"
            ),
            onIntent = {}
        )
    }
}
