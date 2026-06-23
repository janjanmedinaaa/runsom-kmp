package com.medina.juanantonio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medina.juanantonio.data.manager.UIEventManager
import com.medina.juanantonio.di.createKoinConfig
import com.medina.juanantonio.presentation.composables.LoadingOverlay
import com.medina.juanantonio.presentation.ui.home.HomeScreenUI
import com.medina.juanantonio.presentation.ui.theme.RunsomTheme
import com.medina.juanantonio.presentation.utils.UIEvent
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    RunsomTheme {
        KoinApplication(
            configuration = createKoinConfig()
        ) {
            val uiEventManager = koinInject<UIEventManager>()
            val snackbarHostState = remember { SnackbarHostState() }
            var isLoading by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                uiEventManager.events.collect { event ->
                    when (event) {
                        is UIEvent.ShowSnackbar -> {
                            snackbarHostState.showSnackbar(
                                message = event.message,
                                actionLabel = event.actionLabel
                            )
                        }

                        is UIEvent.ToggleLoading -> {
                            isLoading = event.isVisible
                        }
                    }
                }
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .fillMaxSize()
                        .padding(padding)
                ) {

                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = 800.dp)
                                .fillMaxWidth()
                        ) {
                            HomeScreenUI()
                        }
                    }

//                    LoadingOverlay(isLoading)
                }
            }
        }
    }
}