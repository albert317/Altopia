package org.terratec.altopia.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.terratec.altopia.presentation.viewmodel.UserViewModel

@Composable
fun UserScreen(
    viewModel: UserViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        uiState.error?.let { error ->
            Text("Error: $error", color = androidx.compose.ui.graphics.Color.Red)
            Button(onClick = { viewModel.loadUser(1) }) {
                Text("Retry User Load")
            }
        }

        uiState.user?.let { user ->
            Text("User: ${user.name}")
            Text("Email: ${user.email}")
        }

        Button(onClick = { viewModel.loadUser(1) }, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Load User 1")
        }

        androidx.compose.foundation.lazy.LazyColumn {
            items(uiState.videos.size) { index ->
                val video = uiState.videos[index]
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Video ID: ${video.id}")
                        Text("Created At: ${video.createdAt}")
                        Text("Link: ${video.videoLink}")
                        Text("Finished: ${video.isFinished ?: "Unknown"}")
                    }
                }
            }
        }
    }
}
