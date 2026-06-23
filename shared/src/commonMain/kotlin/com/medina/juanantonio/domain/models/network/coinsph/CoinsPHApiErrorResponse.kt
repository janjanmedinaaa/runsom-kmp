package com.medina.juanantonio.domain.models.network.coinsph

import kotlinx.serialization.Serializable

@Serializable
data class CoinsPHApiErrorResponse(
    val code: Int? = null,
    val msg: String? = null
)