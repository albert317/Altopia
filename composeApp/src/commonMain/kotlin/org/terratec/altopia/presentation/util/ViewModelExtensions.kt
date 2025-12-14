package org.terratec.altopia.presentation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.terratec.altopia.domain.model.BusinessError

fun <T> ViewModel.executeTask(
    onSuccess: (T) -> Unit,
    onFailure: (BusinessError) -> Unit,
    task: suspend () -> T,
) {
    viewModelScope.launch {
        try {
            val result = task.invoke()
            withContext(Dispatchers.Main) {
                onSuccess.invoke(
                    result
                )
            }
        } catch (e: Throwable) {
            when(e){
                is BusinessError -> {
                    withContext(Dispatchers.Main) {
                        onFailure.invoke(e)
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        onFailure.invoke(
                            BusinessError(
                                message = e.message ?: "Unknown error",
                                success = false,
                                codeMessage = "UNKNOWN_ERROR",
                            )
                        )
                    }
                }
            }
        }
    }
}
