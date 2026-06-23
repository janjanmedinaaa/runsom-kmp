package com.medina.juanantonio.domain.models.network.coinsph

import kotlinx.serialization.Serializable

@Serializable
data class CheckCoinsAccountsResponse(
    val token: String,
    val canTrade: Boolean,
    val canDeposit: Boolean,
    val canWithdraw: Boolean,
    val accountType: String,
    val updateTime: String,
    val balances: List<AccountBalance>,
    val email: String
) {
    val pesoBalance: String
        get() = balances.firstOrNull { it.asset == "PHP" }?.free ?: ""
}

@Serializable
data class AccountBalance(
    val asset: String,
    val free: String,
    val locked: String
)