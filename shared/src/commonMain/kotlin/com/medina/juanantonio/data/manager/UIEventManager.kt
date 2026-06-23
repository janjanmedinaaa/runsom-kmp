package com.medina.juanantonio.data.manager

import com.medina.juanantonio.presentation.utils.UIEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class UIEventManager {

    private val _events = Channel<UIEvent>()
    val events = _events.receiveAsFlow()

    fun showSnackBarMessage(message: String) {
        _events.trySend(
            UIEvent.ShowSnackbar(
                message = message
            )
        )
    }

    fun toggleLoading(isVisible: Boolean) {
        _events.trySend(
            UIEvent.ToggleLoading(isVisible)
        )
    }
}
