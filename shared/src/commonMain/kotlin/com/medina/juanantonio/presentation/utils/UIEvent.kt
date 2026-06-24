package com.medina.juanantonio.presentation.utils

sealed interface UIEvent {
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null
    ) : UIEvent

    data class ToggleLoading(
        val isVisible: Boolean
    ) : UIEvent
}
