package org.terratec.altopia.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.terratec.altopia.presentation.model.ManagedDialogConfig

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import altopia.composeapp.generated.resources.Res
import altopia.composeapp.generated.resources.background_base

@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
    managedDialogState: ManagedDialogConfig? = null,
    onDialogDismiss: () -> Unit = {},
    onTapOutside: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(Res.drawable.background_base),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.25f
        )

        // Safe Area Wrapper
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            content()
            
            // Mostrar diálogo si existe configuración
            managedDialogState?.let { dialogConfig ->
                AlertDialogCustom(
                    showDialog = true,
                    onDismissRequest = dialogConfig.finalOnDismissRequest,
                    title = dialogConfig.userConfig.title,
                    description = dialogConfig.userConfig.description,
                    primaryButtonText = dialogConfig.userConfig.primaryButtonText,
                    onPrimaryButtonClick = dialogConfig.finalOnPrimaryButtonClick,
                    secondaryButtonText = dialogConfig.userConfig.secondaryButtonText,
                    onSecondaryButtonClick = dialogConfig.finalOnSecondaryButtonClick,
                    showCloseButton = dialogConfig.userConfig.showCloseButton,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = dialogConfig.userConfig.dismissOnBackClick,
                        dismissOnClickOutside = dialogConfig.userConfig.dismissOnClickOutside
                    )
                )
            }
        }
        
        // Mostrar indicador de progreso (Overlay total)
        if (showProgress) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Color.White,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}
