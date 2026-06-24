package com.medina.juanantonio.presentation.ui.home

import com.medina.juanantonio.data.manager.UIEventManager
import com.medina.juanantonio.data.repository.CoinsPHRepository
import com.medina.juanantonio.data.repository.ContractRepository
import com.medina.juanantonio.data.repository.StravaRepository
import com.medina.juanantonio.domain.models.CoinsPHData
import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import com.medina.juanantonio.domain.models.network.CoinsPHAccount
import com.medina.juanantonio.domain.models.network.strava.StravaAthlete
import com.medina.juanantonio.domain.models.network.NetworkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val stravaRepository: StravaRepository,
    private val coinsPHRepository: CoinsPHRepository,
    private val contractRepository: ContractRepository,
    private val uiEventManager: UIEventManager
) {

    val currentStravaAthlete: StateFlow<StravaAthlete?>
        get() = stravaRepository.currentStravaAthlete

    val currentCoinsPHData: StateFlow<CoinsPHData?>
        get() = coinsPHRepository.currentCoinsPHData

    val activeContractsWithActivities: Flow<List<ContractWithActivities>>
        get() = contractRepository.getActiveContracts().map { contracts ->
            contracts.filter { !it.contract.isExpired && !it.isComplete }
        }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            stravaRepository.checkIfAuthenticated()
        }

        if (coinsPHRepository.checkIfCoinsPhAccountIsAvailable()) {
            refreshCoinsPHData()
        }
    }

    private var job: Job? = null

    fun loginWithStrava() {
        stravaRepository.login()
    }

    fun refreshCoinsPHData() {
        if (job?.isActive == true) return
        job = CoroutineScope(Dispatchers.IO).launch {
            val result = coinsPHRepository.checkAccountBalance()
            if (result is NetworkResult.Error) {
                uiEventManager.showSnackBarMessage(result.message)
            }
        }
    }

    fun logout(onFinish: () -> Unit = {}) {
        if (job?.isActive == true) return
        job = CoroutineScope(Dispatchers.IO).launch {
            uiEventManager.toggleLoading(true)
            onFinish() // Dismiss Dialog

            val result = stravaRepository.logout()
            if (result is NetworkResult.Error) {
                uiEventManager.showSnackBarMessage(result.message)
            }

            uiEventManager.toggleLoading(false)
        }
    }

    fun getRemainingMonthlyWithdrawal(onCountReceived: (Int) -> Unit) {
        if (job?.isActive == true) return
        job = CoroutineScope(Dispatchers.IO).launch {
            val count = contractRepository.getRemainingMonthlyWithdrawal()
            withContext(Dispatchers.Main) {
                onCountReceived(count)
            }
        }
    }

    fun withdrawFromContract(contractId: Int, onFinish: () -> Unit = {}) {
        if (job?.isActive == true) return
        job = CoroutineScope(Dispatchers.IO).launch {
            uiEventManager.toggleLoading(true)
            onFinish() // Dismiss Dialog

            val contractWithActivities =
                contractRepository.getContract(contractId) ?: return@launch
            val contract = contractWithActivities.contract
            val activities = contractWithActivities.activities

            val totalAmount = contract.pricePerKm * contract.distance
            val currentKmCount = activities.sumOf { it.wholeKilometer }
            val currentAmount = currentKmCount * contract.pricePerKm

            val remainingBalance = totalAmount - currentAmount
            val isAutoPaymentsAvailable = coinsPHRepository.checkIfAutoPaymentIsAvailable()
            val message = if (isAutoPaymentsAvailable) {
                "Here is your withdrawn money from the ${contract.title} Contract."
            } else {
                "I would like to withdraw from the ${contract.title} Contract."
            }

            val result = coinsPHRepository.requestPayment(
                amount = remainingBalance,
                customSenderName = "Runsom Contract Withdrawal",
                message = message,
                account = CoinsPHAccount.ESCROW
            )

            if (result is NetworkResult.Success) {
                contractRepository.withdrawFromContract(contractId)
            }

            uiEventManager.toggleLoading(false)
        }
    }
}
