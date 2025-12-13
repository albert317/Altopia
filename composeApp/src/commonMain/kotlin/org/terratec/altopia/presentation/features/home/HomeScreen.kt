package org.terratec.altopia.presentation.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.terratec.altopia.presentation.ui.components.BaseScreen
import org.terratec.altopia.presentation.ui.theme.AppTheme

@Composable
fun HomeScreen(
    onNavigateBack: () -> Unit = {}, // Not strictly needed for Home root
    onNavigateToPayments: () -> Unit,
    viewModel: HomeViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                HomeContract.Event.NavigateToPayments -> onNavigateToPayments()
                is HomeContract.Event.NavigateToExpenseDetail -> { /* TODO */ }
                is HomeContract.Event.ShowSnack -> { /* TODO: Show snackbar */ }
            }
        }
    }

    HomeContent(
        uiState = uiState,
        onIntent = viewModel::setIntent
    )
}

@Composable
private fun HomeContent(
    uiState: HomeContract.UiState,
    onIntent: (HomeContract.Intent) -> Unit
) {
    BaseScreen(
        showProgress = uiState.isLoading
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                HomeHeader(
                    userName = uiState.userName,
                    unitCode = uiState.unitCode
                )
            }

            // Debt Section (Hero)
            item {
                DebtStatusCard(
                    totalDebt = uiState.totalDebt,
                    isOverdue = uiState.isDebtOverdue,
                    onPayClick = { onIntent(HomeContract.Intent.PayReceipt) }
                )
            }

            // Last Receipt
            uiState.lastReceipt?.let { receipt ->
                item {
                    LastReceiptCard(receipt = receipt)
                }
            }

            // Building Expenses
            item {
                BuildingExpensesSection(
                    expenses = uiState.buildingExpenses,
                    onViewDetails = { id -> onIntent(HomeContract.Intent.ViewExpenseDetails(id)) }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(userName: String, unitCode: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hola, $userName",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Unidad $unitCode",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DebtStatusCard(
    totalDebt: Double,
    isOverdue: Boolean,
    onPayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tu Deuda Total",
                style = MaterialTheme.typography.labelLarge,
                color = if (isOverdue) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "S/. ${totalDebt}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = if (isOverdue) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val statusText = if (isOverdue) "Pago Vencido" else "Al día"
            val statusIcon = if (isOverdue) Icons.Default.Warning else Icons.Default.Receipt
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = statusIcon, 
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onPayClick,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (isOverdue) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pagar Ahora")
            }
        }
    }
}

@Composable
private fun LastReceiptCard(receipt: HomeContract.ReceiptSummary) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Último Recibo",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = receipt.periodName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Vence: ${receipt.dueDate}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = receipt.status, 
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Breakdown
            ReceiptRow("Mantenimiento", receipt.maintenanceAmount)
            ReceiptRow("Agua / Servicios", receipt.waterAmount)
            androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ReceiptRow("Total", receipt.totalAmount, isBold = true)
        }
    }
}

@Composable
private fun ReceiptRow(label: String, amount: Double, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label, 
            style = if (isBold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "S/. $amount", 
            style = if (isBold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun BuildingExpensesSection(
    expenses: List<HomeContract.ExpenseCategory>,
    onViewDetails: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Gastos del Edificio",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Simple Chart Placeholder
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // Mock bars
            Box(Modifier.width(24.dp).height(60.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(topStart=4.dp, topEnd=4.dp)))
            Box(Modifier.width(24.dp).height(80.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), RoundedCornerShape(topStart=4.dp, topEnd=4.dp)))
            Box(Modifier.width(24.dp).height(100.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart=4.dp, topEnd=4.dp))) 
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        expenses.forEach { expense ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(expense.name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "S/. ${expense.amount}", 
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            androidx.compose.material3.HorizontalDivider()
        }
        
        OutlinedButton(
            onClick = { onViewDetails("all") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Ver todos los gastos")
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeContent(
            uiState = HomeContract.UiState(
                userName = "Albert Montes",
                unitCode = "A-302",
                totalDebt = 180.0,
                isDebtOverdue = false,
                lastReceipt = HomeContract.ReceiptSummary(
                    id = "REC-123",
                    periodName = "Noviembre 2025",
                    dueDate = "15/12/2025",
                    status = "PENDIENTE",
                    maintenanceAmount = 150.0,
                    waterAmount = 30.0,
                    totalAmount = 180.0
                ),
                buildingExpenses = listOf(
                    HomeContract.ExpenseCategory("1", "Seguridad", 3500.0),
                    HomeContract.ExpenseCategory("2", "Jardinería", 1200.0),
                    HomeContract.ExpenseCategory("3", "Luz Común", 850.50)
                )
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
fun HomeScreenOverduePreview() {
    AppTheme {
        HomeContent(
            uiState = HomeContract.UiState(
                userName = "Albert Montes",
                unitCode = "A-302",
                totalDebt = 350.0,
                isDebtOverdue = true,
                lastReceipt = HomeContract.ReceiptSummary(
                    id = "REC-122",
                    periodName = "Octubre 2025",
                    dueDate = "15/11/2025",
                    status = "VENCIDO",
                    maintenanceAmount = 150.0,
                    waterAmount = 30.0,
                    totalAmount = 180.0
                ),
                buildingExpenses = emptyList()
            ),
            onIntent = {}
        )
    }
}
