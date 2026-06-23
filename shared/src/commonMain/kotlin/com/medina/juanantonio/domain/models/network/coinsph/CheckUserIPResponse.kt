package com.medina.juanantonio.domain.models.network.coinsph

import kotlinx.serialization.Serializable

@Serializable
data class CheckUserIPResponse(
    val ip: String
)