package com.medina.juanantonio.domain.models.network.coinsph

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransferMoneyRequest(
    @SerialName("target_address")
    val targetAddress: String,
    val account: String,
    val amount: Int,
    val customSenderName: String,
    val message: String
)
