package com.medina.juanantonio.data.repository

import com.medina.juanantonio.data.network.CoinsPHRemoteSource
import com.medina.juanantonio.domain.models.CoinsPHData
import com.medina.juanantonio.domain.models.SettingsKeys.COINSPH
import com.medina.juanantonio.domain.models.network.CoinsPHAccount
import com.medina.juanantonio.domain.models.network.NetworkResult
import com.medina.juanantonio.domain.models.network.coinsph.CheckCoinsAccountsResponse
import com.medina.juanantonio.domain.models.network.coinsph.CheckUserIPResponse
import com.medina.juanantonio.domain.models.network.coinsph.CoinsPHTransferResponse
import com.medina.juanantonio.domain.models.network.coinsph.RequestPaymentRequestResponse
import com.medina.juanantonio.domain.models.network.coinsph.TransferMoneyResponse
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.time.Clock

class CoinsPHRepository(
    private val settings: Settings,
    private val remoteSource: CoinsPHRemoteSource
) {

    private var _currentCoinsPHData = MutableStateFlow<CoinsPHData?>(null)
    val currentCoinsPHData: StateFlow<CoinsPHData?>
        get() = _currentCoinsPHData


    init {
        CoroutineScope(Dispatchers.IO).launch {
            currentCoinsPHData.collect {
                if (it == null) return@collect
                saveCoinsPHData(it)
            }
        }
    }

    fun getSavedMainAPIKey(): String {
        return settings.getString(COINSPH.MAIN_API_KEY, "")
    }

    fun saveMainAPIKey(apiKey: String) {
        settings.putString(COINSPH.MAIN_API_KEY, apiKey)
    }

    fun getSavedMainAPISecret(): String {
        return settings.getString(COINSPH.MAIN_API_SECRET, "")
    }

    fun saveMainAPISecret(apiSecret: String) {
        settings.putString(COINSPH.MAIN_API_SECRET, apiSecret)
    }

    fun getSavedEscrowAPIKey(): String {
        return settings.getString(COINSPH.ESCROW_API_KEY, "")
    }

    fun saveEscrowAPIKey(apiKey: String) {
        settings.putString(COINSPH.ESCROW_API_KEY, apiKey)
    }

    fun getSavedEscrowAPISecret(): String {
        return settings.getString(COINSPH.ESCROW_API_SECRET, "")
    }

    fun saveEscrowAPISecret(apiSecret: String) {
        settings.putString(COINSPH.ESCROW_API_SECRET, apiSecret)
    }

    fun clearEscrowAPIData() {
        settings.remove(COINSPH.ESCROW_API_KEY)
        settings.remove(COINSPH.ESCROW_API_SECRET)
    }

    fun getSavedHolderEmailAccount(): String {
        return settings.getString(COINSPH.HOLDER_EMAIL_ACCOUNT, "")
    }

    fun saveHolderEmailAccount(email: String) {
        settings.putString(COINSPH.HOLDER_EMAIL_ACCOUNT, email)
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    fun getSavedLastCoinsPHData(): CoinsPHData? {
        return settings.decodeValueOrNull<CoinsPHData>(COINSPH.LAST_COINSPH_DATA)
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    fun saveCoinsPHData(data: CoinsPHData) {
        settings.encodeValue<CoinsPHData>(COINSPH.LAST_COINSPH_DATA, data)
    }

    suspend fun getUserIP(): NetworkResult<CheckUserIPResponse> {
        val result = remoteSource.getUserIP()
        return result
    }

    fun checkIfCoinsPhAccountIsAvailable(): Boolean {
        val apiKey = getSavedMainAPIKey()
        val secret = getSavedMainAPISecret()

        return (apiKey.isNotBlank() && secret.isNotBlank())
    }

    fun checkIfAutoPaymentIsAvailable(): Boolean {
        val apiKey = getSavedEscrowAPIKey()
        val secret = getSavedEscrowAPISecret()

        return (apiKey.isNotBlank() && secret.isNotBlank())
    }

    suspend fun checkAccountBalance(
        account: CoinsPHAccount = CoinsPHAccount.MAIN
    ): NetworkResult<CheckCoinsAccountsResponse> {
        getSavedLastCoinsPHData()?.let { data ->
            _currentCoinsPHData.value = data
        }

        val result = remoteSource.checkAccountBalance(account)
        if (result is NetworkResult.Success && account == CoinsPHAccount.MAIN) {
            _currentCoinsPHData.value = CoinsPHData(
                pesoBalance = result.data.pesoBalance,
                email = result.data.email,
                updateTime = "${Clock.System.now().toEpochMilliseconds()}"
            )
        }

        return result
    }

    suspend fun requestPaymentRequest(
        amount: Int,
        message: String
    ): NetworkResult<RequestPaymentRequestResponse> {
        val savedHolderEmail = getSavedHolderEmailAccount()
        val result = remoteSource.sendPaymentRequest(
            payerContactInfo = savedHolderEmail,
            receivingAccount = "PHP",
            amount = amount,
            message = message
        )

        return result
    }

    suspend fun requestMoneyTransfer(
        amount: Int,
        customSenderName: String = "",
        message: String = "",
        account: CoinsPHAccount = CoinsPHAccount.MAIN
    ): NetworkResult<TransferMoneyResponse> {
        val targetAddress =
            if (account == CoinsPHAccount.MAIN) getSavedHolderEmailAccount()
            else getSavedLastCoinsPHData()?.email
                ?: return NetworkResult.Error("No Coins.PH Email Available")

        val result = remoteSource.requestMoneyTransfer(
            targetAddress = targetAddress,
            accountName = "PHP",
            amount = amount,
            customSenderName = customSenderName,
            message = message,
            account = account
        )

        if (result is NetworkResult.Success) {
            val errorMessage = result.data.transfer.errorMessage
            if (!errorMessage.isNullOrBlank()) {
                return NetworkResult.Error(message = errorMessage)
            }
        }

        return result
    }

    suspend fun requestPayment(
        amount: Int,
        customSenderName: String = "",
        message: String = "",
        account: CoinsPHAccount = CoinsPHAccount.ESCROW
    ): NetworkResult<CoinsPHTransferResponse> {
        return if (checkIfAutoPaymentIsAvailable()) {
            val result = requestMoneyTransfer(
                amount = amount,
                customSenderName = customSenderName,
                message = message,
                account = account
            )

            if (result is NetworkResult.Success) {
                checkAccountBalance()
            }

            result
        } else {
            requestPaymentRequest(
                amount = amount,
                message = message
            )
        }
    }
}
