package com.medina.juanantonio.presentation.ui.home.modals.settings

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.mutableStateOf
import com.medina.juanantonio.data.manager.UIEventManager
import com.medina.juanantonio.data.repository.CoinsPHRepository
import com.medina.juanantonio.domain.models.network.CoinsPHAccount
import com.medina.juanantonio.domain.models.network.NetworkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsFormViewModel(
    private val coinsPHRepository: CoinsPHRepository,
    private val uiEventManager: UIEventManager
) {
    val mainApiKeyState = TextFieldState()
    val mainApiSecretState = TextFieldState()
    val escrowApiKeyState = TextFieldState()
    val escrowApiSecretState = TextFieldState()
    val holderEmailAccount = TextFieldState()

    val autoPaymentsEnabledState = mutableStateOf(false)

    private var verifyJob: Job? = null

    private val _ipAddressDisplay = MutableStateFlow("Unverified")
    val ipAddressDisplay: StateFlow<String>
        get() = _ipAddressDisplay

    val isLoadingState = mutableStateOf(false)

    init {
        mainApiKeyState.setTextAndPlaceCursorAtEnd(
            coinsPHRepository.getSavedMainAPIKey()
        )

        mainApiSecretState.setTextAndPlaceCursorAtEnd(
            coinsPHRepository.getSavedMainAPISecret()
        )

        escrowApiKeyState.setTextAndPlaceCursorAtEnd(
            coinsPHRepository.getSavedEscrowAPIKey()
        )

        escrowApiSecretState.setTextAndPlaceCursorAtEnd(
            coinsPHRepository.getSavedEscrowAPISecret()
        )

        holderEmailAccount.setTextAndPlaceCursorAtEnd(
            coinsPHRepository.getSavedHolderEmailAccount()
        )

        autoPaymentsEnabledState.value =
            coinsPHRepository.getSavedEscrowAPISecret().isNotBlank()
                    && coinsPHRepository.getSavedEscrowAPISecret().isNotBlank()

        verifyAPIKey()
    }

    fun verifyAPIKey() {
        if (verifyJob?.isActive == true) return

        verifyJob = CoroutineScope(Dispatchers.IO).launch {
            _ipAddressDisplay.value = "Verifying..."

            when (val result = coinsPHRepository.getUserIP()) {
                is NetworkResult.Success -> {
                    _ipAddressDisplay.value = result.data.ip
                }

                is NetworkResult.Error -> {
                    _ipAddressDisplay.value = result.message
                    uiEventManager.showSnackBarMessage(result.message)
                }
            }
        }
    }

    fun submitForm(onFinish: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            isLoadingState.value = true

            coinsPHRepository.saveMainAPIKey(mainApiKeyState.text.toString())
            coinsPHRepository.saveMainAPISecret(mainApiSecretState.text.toString())

            if (autoPaymentsEnabledState.value) {
                coinsPHRepository.saveEscrowAPIKey(escrowApiKeyState.text.toString())
                coinsPHRepository.saveEscrowAPISecret(escrowApiSecretState.text.toString())

                checkEscrowAccount()
            } else {
                coinsPHRepository.saveHolderEmailAccount(holderEmailAccount.text.toString())
                coinsPHRepository.clearEscrowAPIData()
            }

            val result = coinsPHRepository.checkAccountBalance()
            if (result is NetworkResult.Error) {
                uiEventManager.showSnackBarMessage(result.message)
            }

            isLoadingState.value = false
            onFinish()
        }
    }

    private suspend fun checkEscrowAccount() {
        val result =
            coinsPHRepository.checkAccountBalance(CoinsPHAccount.ESCROW)

        if (result is NetworkResult.Success) {
            coinsPHRepository.saveHolderEmailAccount(result.data.email)
        } else if (result is NetworkResult.Error) {
            uiEventManager.showSnackBarMessage(result.message)
        }
    }
}
