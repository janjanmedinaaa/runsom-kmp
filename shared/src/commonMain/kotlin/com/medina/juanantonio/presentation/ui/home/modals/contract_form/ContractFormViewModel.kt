package com.medina.juanantonio.presentation.ui.home.modals.contract_form

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.medina.juanantonio.data.manager.UIEventManager
import com.medina.juanantonio.data.repository.CoinsPHRepository
import com.medina.juanantonio.data.repository.ContractRepository
import com.medina.juanantonio.domain.ContractDataTypes
import com.medina.juanantonio.domain.models.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ContractFormViewModel(
    private val contractRepository: ContractRepository,
    private val coinsPHRepository: CoinsPHRepository,
    private val uiEventManager: UIEventManager
) {
    val selectedActivityState = mutableIntStateOf(-1)
    val distanceState = TextFieldState()
    val pricePerKilometer = TextFieldState()

    val selectedTimeLimitState = mutableIntStateOf(0)
    val maximumActivityCountState = TextFieldState()
    val minimumActivityDistanceState = TextFieldState("1")
    val contractTitleState = TextFieldState()

    val computedPriceState = mutableIntStateOf(0)

    val isLoadingState = mutableStateOf(false)

    suspend fun submitContractForm(onFinish: () -> Unit = {}) {
        withContext(Dispatchers.IO) {
            isLoadingState.value = true

            val activityType = ContractDataTypes.activityTypes[selectedActivityState.intValue]
            val distance = distanceState.text.toString().toIntOrNull() ?: 0
            val pricePerKm = pricePerKilometer.text.toString().toIntOrNull() ?: 0

            val selectedTimeLimit =
                ContractDataTypes.defaultContractExpiration[selectedTimeLimitState.value].second
            val maximumActivityCount =
                maximumActivityCountState.text.toString().toIntOrNull() ?: -1
            val minimumActivityDistance =
                minimumActivityDistanceState.text.toString().toIntOrNull() ?: 1
            val contractTitle = contractTitleState.text.ifBlank {
                "$activityType for ${distance}KM"
            }.toString()

            val result = coinsPHRepository.requestMoneyTransfer(
                amount = computedPriceState.value,
                customSenderName = "${distance}KM $activityType Runsom",
                message = "I created a Contract to $activityType for ${distance}KM for ${computedPriceState.value}."
            )

            if (result is NetworkResult.Success) {
                coinsPHRepository.checkAccountBalance()
                contractRepository.addContract(
                    activityType = activityType,
                    distance = distance,
                    pricePerKm = pricePerKm,
                    limitInDays = selectedTimeLimit,
                    title = contractTitle,
                    maximumActivities = maximumActivityCount,
                    minimumDistanceKm = minimumActivityDistance
                )
            } else if (result is NetworkResult.Error) {
                uiEventManager.showSnackBarMessage(result.message)
            }

            isLoadingState.value = false
            onFinish()
        }
    }
}
