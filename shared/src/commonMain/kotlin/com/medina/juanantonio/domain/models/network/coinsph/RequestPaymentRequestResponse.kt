package com.medina.juanantonio.domain.models.network.coinsph

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPaymentRequestResponse(
    @SerialName("payment-request")
    val paymentRequest: CoinsPHPaymentRequest
)
