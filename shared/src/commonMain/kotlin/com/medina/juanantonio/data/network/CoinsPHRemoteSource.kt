package com.medina.juanantonio.data.network

import com.medina.juanantonio.domain.factory.plugins.useAccount
import com.medina.juanantonio.domain.models.network.CoinsPHAccount
import com.medina.juanantonio.domain.models.network.NetworkResult
import com.medina.juanantonio.domain.models.network.coinsph.CheckCoinsAccountsResponse
import com.medina.juanantonio.domain.models.network.coinsph.CheckUserIPResponse
import com.medina.juanantonio.domain.models.network.caller.safeCoinsPhApiCall
import com.medina.juanantonio.domain.models.network.coinsph.RequestPaymentRequestResponse
import com.medina.juanantonio.domain.models.network.coinsph.TransferMoneyResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class CoinsPHRemoteSource(
    private val client: HttpClient
) {

    suspend fun getUserIP(): NetworkResult<CheckUserIPResponse> {
        return safeCoinsPhApiCall(client) {
            get("/openapi/v1/user/ip")
        }
    }

    suspend fun checkAccountBalance(
        account: CoinsPHAccount = CoinsPHAccount.MAIN
    ): NetworkResult<CheckCoinsAccountsResponse> {
        return safeCoinsPhApiCall(client) {
            get("/openapi/v1/account") {
                useAccount(account)
            }
        }
    }

    suspend fun sendPaymentRequest(
        payerContactInfo: String,
        receivingAccount: String,
        amount: Int,
        message: String,
        account: CoinsPHAccount = CoinsPHAccount.MAIN
    ): NetworkResult<RequestPaymentRequestResponse> {
        return safeCoinsPhApiCall(client) {
            post("/openapi/v3/payment-request/payment-requests") {
                useAccount(account)

                parameter("payer_contact_info", payerContactInfo)
                parameter("receiving_account", receivingAccount)
                parameter("amount", amount)
                parameter("message", message)
            }
        }
    }

    suspend fun requestMoneyTransfer(
        targetAddress: String,
        accountName: String,
        amount: Int,
        customSenderName: String,
        message: String,
        account: CoinsPHAccount = CoinsPHAccount.MAIN
    ): NetworkResult<TransferMoneyResponse> {
        return safeCoinsPhApiCall(client) {
            post("/openapi/transfer/v3/transfers") {
                useAccount(account)

                parameter("target_address", targetAddress)
                parameter("account", accountName)
                parameter("amount", amount)

                if (customSenderName.isNotBlank()) {
                    parameter("customSenderName", customSenderName)
                }

                if (message.isNotBlank()) {
                    parameter("message", message)
                }
            }
        }
    }
}
