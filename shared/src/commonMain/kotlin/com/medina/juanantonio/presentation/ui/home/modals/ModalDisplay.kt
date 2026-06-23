package com.medina.juanantonio.presentation.ui.home.modals

import com.medina.juanantonio.domain.models.entities.ContractWithActivities

sealed class ModalDisplay {

    class ContractForm: ModalDisplay()
    data class SubmitActivityForm(
        val contract: ContractWithActivities
    ): ModalDisplay()

    class Settings: ModalDisplay()

    class Logout: ModalDisplay()
}