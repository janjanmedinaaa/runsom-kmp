package com.medina.juanantonio.domain.models.network.coinsph

import kotlinx.serialization.Serializable

@Serializable
data class CoinsPHTransfer(
    val id: String,
    val status: String,
    val account: String,
    val message: String,
    val errorMessage: String?
)