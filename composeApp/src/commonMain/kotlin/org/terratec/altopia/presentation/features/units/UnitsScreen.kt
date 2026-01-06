package org.terratec.altopia.presentation.features.units

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.terratec.altopia.domain.model.Block
import org.terratec.altopia.domain.model.PropertyUnit
import org.terratec.altopia.presentation.model.DialogInfo
import org.terratec.altopia.presentation.model.ManagedDialogConfig
import org.terratec.altopia.presentation.ui.components.BaseScreen
import org.terratec.altopia.presentation.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UnitsScreen(
    onNavigateBack: () -> Unit,
    viewModel: UnitsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.managedDialogState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is UnitsEvent.ShowToast -> {
                    // Toast handling would go here (e.g., SnackbarHostState)
                }
            }
        }
    }

    UnitsContent(
        uiState = uiState,
        onIntent = viewModel::setIntent,
        dialogState = dialogState,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitsContent(
    uiState: UnitsUiState,
    onIntent: (UnitsIntent) -> Unit,
    dialogState: ManagedDialogConfig?,
    onNavigateBack: () -> Unit
) {
    BaseScreen(
        managedDialogState = dialogState,
        showProgress = uiState.isLoading
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestión de Unidades") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { onIntent(UnitsIntent.OpenCreateDialog) }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Unidad")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.units) { unit ->
                    UnitCard(
                        unit = unit,
                        onEdit = { onIntent(UnitsIntent.OpenEditDialog(unit.id)) },
                        onDelete = { onIntent(UnitsIntent.DeleteUnit(unit.id)) }
                    )
                }
            }

            if (uiState.isFormVisible) {
                UnitsFormDialog(
                    uiState = uiState,
                    onIntent = onIntent,
                    onDismiss = { onIntent(UnitsIntent.CloseDialog) }
                )
            }
        }
    }
}

@Composable
private fun UnitCard(
    unit: PropertyUnit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${unit.codigo} - ${unit.bloqueNombre ?: "Sin Bloque"}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Piso: ${unit.piso} | Coef: ${unit.coeficienteArea}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (unit.tipoUso == "VIVIENDA") Icons.Default.Home else Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit.tipoUso,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun UnitsFormDialog(
    uiState: UnitsUiState,
    onIntent: (UnitsIntent) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (uiState.isEditing) "Editar Unidad" else "Nueva Unidad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Block Selector (Dropdown simplified as Clickable Text or Radio for now, or exposed dropdown)
                // For simplicity in this iteration, we'll use a simple indicator or basic dropdown if needed.
                // Assuming Blocks list is small.
                Text("Bloque", style = MaterialTheme.typography.labelMedium)
                if (uiState.blocks.isNotEmpty()) {
                    // Simple selection logic with dropdown menu approach or similar would be ideal.
                    // For MVP stability with unknown dropdown components, let's use a Column of RadioButtons if few, or just display selected ID for now if implementation is complex.
                    // Better: Standard generic dropdown logic.
                    // Since I don't have a generic Dropdown component in my context, I'll rely on a simple list or just text input for block ID if complex.
                    // Wait, the plan said "Selector". I'll try to use ExposedDropdownMenuBox if available or just a list of buttons if few.
                    // Let's assume standard DropdownMenu.
                    var expanded by remember { mutableStateOf(false) }
                    val selectedBlockName = uiState.blocks.find { it.id == uiState.selectedBlockId }?.nombre ?: "Seleccionar Bloque"
                    
                    Box {
                        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(selectedBlockName)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            uiState.blocks.forEach { block ->
                                DropdownMenuItem(
                                    text = { Text(block.nombre) },
                                    onClick = {
                                        onIntent(UnitsIntent.BlockSelected(block.id))
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text("No hay bloques disponibles. Cree bloques primero.", color = MaterialTheme.colorScheme.error)
                }

                OutlinedTextField(
                    value = uiState.code,
                    onValueChange = { onIntent(UnitsIntent.CodeChanged(it)) },
                    label = { Text("Código (Ej. 101)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.floor,
                        onValueChange = { onIntent(UnitsIntent.FloorChanged(it)) },
                        label = { Text("Piso") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.areaCoefficient,
                        onValueChange = { onIntent(UnitsIntent.CoefficientChanged(it)) },
                        label = { Text("Coef. (%)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Tipo de Uso", style = MaterialTheme.typography.labelMedium)
                Row {
                   listOf("VIVIENDA", "COMERCIAL").forEach { type ->
                       Row(verticalAlignment = Alignment.CenterVertically) {
                           RadioButton(
                               selected = uiState.usageType == type,
                               onClick = { onIntent(UnitsIntent.UsageTypeChanged(type)) }
                           )
                           Text(type, style = MaterialTheme.typography.bodySmall)
                       }
                       Spacer(modifier = Modifier.width(8.dp))
                   } 
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onIntent(UnitsIntent.SubmitForm) },
                enabled = uiState.isFormValid
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview
@Composable
fun UnitsScreenPreview() {
    AppTheme {
        UnitsContent(
            uiState = UnitsUiState(
                units = listOf(
                    PropertyUnit("1", "b1", "Torre A", "101", 1, 1.5, "VIVIENDA", "")
                ),
                blocks = listOf(Block("b1", "Torre A"))
            ),
            onIntent = {},
            dialogState = null,
            onNavigateBack = {}
        )
    }
}
