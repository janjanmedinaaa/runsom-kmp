package com.medina.juanantonio.domain.models.network.coinsph

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinsPHPaymentRequest(
    val message: String,

    @SerialName("payment_url")
    val paymentUrl: String,

    @SerialName("status")
    val status: String,

    @SerialName("payer_contact_info")
    val payerContactInfo: String
) {
}