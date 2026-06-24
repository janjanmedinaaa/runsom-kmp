package com.medina.juanantonio.domain.models.network.coinsph

import kotlinx.serialization.Serializable

@Serializable
data class TransferMoneyResponse(
    val transfer: CoinsPHTransfer
) : CoinsPHTransferResponse

interface CoinsPHTransferResponse