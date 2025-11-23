package org.terratec.altopia.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun AlertDialogCustom(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    description: String,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryButtonClick: (() -> Unit)? = null,
    showCloseButton: Boolean = false,
    dialogProperties: DialogProperties = DialogProperties()
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },
            text = { Text(text = description) },
            confirmButton = {
                Button(onClick = onPrimaryButtonClick) {
                    Text(text = primaryButtonText)
                }
            },
            dismissButton = {
                if (secondaryButtonText != null && onSecondaryButtonClick != null) {
                    TextButton(onClick = onSecondaryButtonClick) {
                        Text(text = secondaryButtonText)
                    }
                }
            },
            properties = dialogProperties
        )
    }
}
