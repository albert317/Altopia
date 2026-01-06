package org.terratec.altopia.presentation.features.units

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.terratec.altopia.domain.usecase.unit.CreateUnitUseCase
import org.terratec.altopia.domain.usecase.unit.DeleteUnitUseCase
import org.terratec.altopia.domain.usecase.unit.GetBlocksUseCase
import org.terratec.altopia.domain.usecase.unit.GetUnitsUseCase
import org.terratec.altopia.domain.usecase.unit.UpdateUnitUseCase
import org.terratec.altopia.presentation.model.DialogInfo
import org.terratec.altopia.presentation.viewmodel.BaseViewModel

class UnitsViewModel(
    private val getUnitsUseCase: GetUnitsUseCase,
    private val getBlocksUseCase: GetBlocksUseCase,
    private val createUnitUseCase: CreateUnitUseCase,
    private val updateUnitUseCase: UpdateUnitUseCase,
    private val deleteUnitUseCase: DeleteUnitUseCase
) : BaseViewModel<UnitsUiState, UnitsIntent, UnitsEvent>() {

    override fun createInitialState(): UnitsUiState = UnitsUiState()

    init {
        loadData()
    }

    override fun handleIntent(intent: UnitsIntent) {
        when (intent) {
            UnitsIntent.LoadData -> loadData()
            UnitsIntent.OpenCreateDialog -> openCreateDialog()
            is UnitsIntent.OpenEditDialog -> openEditDialog(intent.unitId)
            UnitsIntent.CloseDialog -> closeDialog()
            
            is UnitsIntent.BlockSelected -> updateForm { copy(selectedBlockId = intent.blockId) }
            is UnitsIntent.CodeChanged -> updateForm { copy(code = intent.code) }
            is UnitsIntent.FloorChanged -> updateForm { copy(floor = intent.floor) }
            is UnitsIntent.CoefficientChanged -> updateForm { copy(areaCoefficient = intent.coefficient) }
            is UnitsIntent.UsageTypeChanged -> updateForm { copy(usageType = intent.type) }
            
            UnitsIntent.SubmitForm -> submitForm()
            is UnitsIntent.DeleteUnit -> confirmDeleteUnit(intent.unitId)
        }
    }

    private fun loadData() {
        setUiState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            // Load blocks first or in parallel, then units
            val blocksResult = getBlocksUseCase()
            val unitsResult = getUnitsUseCase()

            if (blocksResult.isSuccess && unitsResult.isSuccess) {
                setUiState {
                    copy(
                        isLoading = false,
                        blocks = blocksResult.getOrThrow(),
                        units = unitsResult.getOrThrow()
                    )
                }
            } else {
                val error = blocksResult.exceptionOrNull()?.message 
                    ?: unitsResult.exceptionOrNull()?.message 
                    ?: "Error cargando datos"
                setUiState { copy(isLoading = false, error = error) }
                showErrorDialog(error)
            }
        }
    }

    private fun openCreateDialog() {
        setUiState {
            copy(
                isFormVisible = true,
                isEditing = false,
                selectedUnitId = null,
                selectedBlockId = blocks.firstOrNull()?.id ?: "",
                code = "",
                floor = "",
                areaCoefficient = "",
                usageType = "VIVIENDA",
                isFormValid = false
            )
        }
        validateForm()
    }

    private fun openEditDialog(unitId: String) {
        val unit = uiState.value.units.find { it.id == unitId } ?: return
        setUiState {
            copy(
                isFormVisible = true,
                isEditing = true,
                selectedUnitId = unitId,
                selectedBlockId = unit.bloqueId,
                code = unit.codigo,
                floor = unit.piso.toString(),
                areaCoefficient = unit.coeficienteArea.toString(),
                usageType = unit.tipoUso,
                isFormValid = true
            )
        }
        validateForm()
    }

    private fun closeDialog() {
        setUiState { copy(isFormVisible = false) }
    }

    private fun updateForm(reducer: UnitsUiState.() -> UnitsUiState) {
        setUiState(reducer)
        validateForm()
    }

    private fun validateForm() {
        setUiState {
            val isValid = selectedBlockId.isNotBlank() &&
                    code.isNotBlank() &&
                    floor.toIntOrNull() != null &&
                    areaCoefficient.toDoubleOrNull() != null &&
                    usageType.isNotBlank()
            copy(isFormValid = isValid)
        }
    }

    private fun submitForm() {
        if (!uiState.value.isFormValid) return

        viewModelScope.launch {
            setUiState { copy(isLoading = true) }
            val state = uiState.value
            
            val result = if (state.isEditing && state.selectedUnitId != null) {
                updateUnitUseCase(
                    id = state.selectedUnitId,
                    bloqueId = state.selectedBlockId,
                    codigo = state.code,
                    piso = state.floor.toInt(),
                    coeficienteArea = state.areaCoefficient.toDouble(),
                    tipoUso = state.usageType
                )
            } else {
                createUnitUseCase(
                    bloqueId = state.selectedBlockId,
                    codigo = state.code,
                    piso = state.floor.toInt(),
                    coeficienteArea = state.areaCoefficient.toDouble(),
                    tipoUso = state.usageType
                )
            }

            result.onSuccess {
                setUiState { copy(isFormVisible = false) }
                loadData() // Refresh list
                setEvent(UnitsEvent.ShowToast(if (state.isEditing) "Unidad actualizada" else "Unidad creada"))
            }.onFailure { error ->
                setUiState { copy(isLoading = false) }
                showErrorDialog(error.message ?: "Error al guardar unidad")
            }
        }
    }

    private fun confirmDeleteUnit(unitId: String) {
        showDialog(
            DialogInfo(
                title = "Eliminar Unidad",
                description = "¿Estás seguro de eliminar esta unidad? Esta acción no se puede deshacer.",
                primaryButtonText = "Eliminar",
                secondaryButtonText = "Cancelar",
                onPrimaryButtonClick = { deleteUnit(unitId) }
            )
        )
    }

    private fun deleteUnit(unitId: String) {
        viewModelScope.launch {
            setUiState { copy(isLoading = true) }
            deleteUnitUseCase(unitId)
                .onSuccess {
                    loadData()
                    setEvent(UnitsEvent.ShowToast("Unidad eliminada"))
                }
                .onFailure { error ->
                    setUiState { copy(isLoading = false) }
                    showErrorDialog(error.message ?: "Error al eliminar unidad")
                }
        }
    }

    private fun showErrorDialog(message: String) {
        showDialog(
            DialogInfo(
                title = "Error",
                description = message,
                primaryButtonText = "Aceptar"
            )
        )
    }
}
