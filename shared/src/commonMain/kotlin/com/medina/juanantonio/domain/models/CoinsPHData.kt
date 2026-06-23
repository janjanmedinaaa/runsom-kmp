package com.medina.juanantonio.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class CoinsPHData(
    val pesoBalance: String,
    val email: String,
    val updateTime: String
)
