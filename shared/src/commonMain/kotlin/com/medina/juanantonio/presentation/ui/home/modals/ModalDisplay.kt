package com.medina.juanantonio.presentation.ui.home.modals

import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import com.medina.juanantonio.domain.models.network.RunsomChallenge
import org.jetbrains.compose.resources.DrawableResource

sealed class ModalDisplay {

    class ContractForm(
        val challenge: RunsomChallenge? = null
    ) : ModalDisplay()

    data class SubmitActivityForm(
        val contract: ContractWithActivities
    ) : ModalDisplay()

    class Settings : ModalDisplay()

    class Challenges : ModalDisplay()

    class Alert(
        val title: String,
        val description: String,
        val positiveButtonText: String,
        val drawableResource: DrawableResource? = null,
        val onPositiveButtonClick: () -> Unit = {}
    ) : ModalDisplay()
}
