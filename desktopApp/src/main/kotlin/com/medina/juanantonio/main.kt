package com.medina.juanantonio

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

val windowState = WindowState(
    placement = WindowPlacement.Maximized
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Runsom"
    ) {
        App()
    }
}
