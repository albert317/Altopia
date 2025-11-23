package org.terratec.altopia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.terratec.altopia.presentation.model.DialogInfo
import org.terratec.altopia.presentation.model.ManagedDialogConfig

abstract class BaseViewModel<UI_STATE, INTENT, EVENT> : ViewModel() {
    private val initialState: UI_STATE by lazy { createInitialState() }
    private val _uiState: MutableStateFlow<UI_STATE> = MutableStateFlow(initialState)

    val uiState = _uiState.asStateFlow()

    private val intents: MutableSharedFlow<INTENT> = MutableSharedFlow()

    private val _event: MutableSharedFlow<EVENT> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _managedDialogState = MutableStateFlow<ManagedDialogConfig?>(null)
    val managedDialogState: StateFlow<ManagedDialogConfig?> = _managedDialogState.asStateFlow()

    init {
        subscribeIntents()
    }

    private fun subscribeIntents() {
        viewModelScope.launch {
            try {
                intents.collect { intent ->
                    handleIntent(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setIntent(intent: INTENT) {
        viewModelScope.launch {
            intents.emit(intent)
        }
    }

    protected fun setUiState(reducer: UI_STATE.() -> UI_STATE) {
        val newState = _uiState.value.reducer()
        _uiState.value = newState
    }


    protected fun setEvent(event: EVENT) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    protected abstract fun createInitialState(): UI_STATE

    protected abstract suspend fun handleIntent(intent: INTENT)

    protected fun showDialog(userDialogConfig: DialogInfo) {
        _managedDialogState.value = ManagedDialogConfig(
            userConfig = userDialogConfig,
            finalOnDismissRequest = {
                userDialogConfig.onDismiss?.invoke()
                dismissDialogInternal()
            },
            finalOnPrimaryButtonClick = {
                userDialogConfig.onPrimaryButtonClick?.invoke() // Lógica de negocio del hijo
                dismissDialogInternal()
            },
            finalOnSecondaryButtonClick = userDialogConfig.secondaryButtonText?.let { // Solo crea lambda si hay texto de botón
                {
                    userDialogConfig.onSecondaryButtonClick?.invoke() // Lógica de negocio del hijo
                    dismissDialogInternal()
                }
            }
        )
    }

    fun dismissDialog() {
        dismissDialogInternal()
    }

    private fun dismissDialogInternal() {
        _managedDialogState.value = null
    }

}
