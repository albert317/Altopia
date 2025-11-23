package org.terratec.altopia.presentation.model

/**
 * Configuration for a managed dialog.
 *
 * @property userConfig The configuration provided by the user/viewmodel.
 * @property finalOnDismissRequest The final action to execute when the dialog is dismissed.
 * @property finalOnPrimaryButtonClick The final action to execute when the primary button is clicked.
 * @property finalOnSecondaryButtonClick The final action to execute when the secondary button is clicked.
 */
data class ManagedDialogConfig(
    val userConfig: DialogInfo,
    val finalOnDismissRequest: () -> Unit,
    val finalOnPrimaryButtonClick: () -> Unit,
    val finalOnSecondaryButtonClick: (() -> Unit)? = null
)

/**
 * Configuration data class for the dialog content and behavior.
 */
data class DialogInfo(
    val title: String,
    val description: String,
    val primaryButtonText: String,
    val secondaryButtonText: String? = null,
    val showCloseButton: Boolean = false,
    val dismissOnBackClick: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
    val onDismiss: (() -> Unit)? = null,
    val onPrimaryButtonClick: (() -> Unit)? = null,
    val onSecondaryButtonClick: (() -> Unit)? = null
)
