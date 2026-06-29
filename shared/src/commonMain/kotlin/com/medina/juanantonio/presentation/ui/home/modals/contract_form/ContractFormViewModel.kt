package com.medina.juanantonio.presentation.ui.home.modals.contract_form

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.medina.juanantonio.data.manager.UIEventManager
import com.medina.juanantonio.data.repository.CoinsPHRepository
import com.medina.juanantonio.data.repository.ContractRepository
import com.medina.juanantonio.domain.ContractDataTypes
import com.medina.juanantonio.domain.models.network.NetworkResult
import com.medina.juanantonio.domain.models.network.RunsomChallenge
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

    // -------------------------------- PARSED VALUES --------------------------------

    private val activityType: String
        get() = ContractDataTypes.activityTypes[selectedActivityState.intValue]
    private val distance: Int
        get() = distanceState.text.toString().toIntOrNull() ?: 0
    private val pricePerKm: Int
        get() = pricePerKilometer.text.toString().toIntOrNull() ?: 0
    private val selectedTimeLimit
        get() = challenge?.dayLimit ?: ContractDataTypes.defaultContractExpiration.run {
            val dayLimit = getOrNull(selectedTimeLimitState.value)?.second
            dayLimit ?: -1
        }
    private val maximumActivityCount
        get() = maximumActivityCountState.text.toString().toIntOrNull() ?: -1
    private val minimumActivityDistance
        get() = minimumActivityDistanceState.text.toString().toIntOrNull() ?: 1
    private val contractTitle
        get() = contractTitleState.text.ifBlank {
            "$activityType for ${distance}KM"
        }.toString()

    private val computedPrice: Int
        get() = computedPriceState.value

    private var challenge: RunsomChallenge? = null

    suspend fun submitContractForm(onFinish: () -> Unit = {}) {
        withContext(Dispatchers.IO) {
            isLoadingState.value = true

            val result = coinsPHRepository.requestMoneyTransfer(
                amount = computedPrice,
                customSenderName = "${distance}KM $activityType Runsom",
                message = "I created a Contract to $activityType for ${distance}KM for ${computedPrice}."
            )

            if (result is NetworkResult.Success) {
                coinsPHRepository.checkAccountBalance()
                challenge?.run {
                    addChallengeContract(this)
                } ?: addContract()
            } else if (result is NetworkResult.Error) {
                uiEventManager.showSnackBarMessage(result.message)
            }

            isLoadingState.value = false
            onFinish()
        }
    }

    private suspend fun addContract() {
        contractRepository.addContract(
            activityType = activityType,
            distance = distance,
            pricePerKm = pricePerKm,
            limitInDays = selectedTimeLimit,
            title = contractTitle,
            maximumActivities = maximumActivityCount,
            minimumDistanceKm = minimumActivityDistance,
            challengeId = challenge?.id
        )
    }

    private suspend fun addChallengeContract(challenge: RunsomChallenge) {
        val deadline =
            if (challenge.hasValidDeadline) {
                challenge.validUntil
            } else {
                addContract(); return
            }

        contractRepository.addContract(
            activityType = activityType,
            distance = distance,
            pricePerKm = pricePerKm,
            deadline = deadline,
            title = contractTitle,
            maximumActivities = maximumActivityCount,
            minimumDistanceKm = minimumActivityDistance,
            challengeId = challenge.id
        )
    }

    fun setupChallenge(challenge: RunsomChallenge) {
        this.challenge = challenge

        val activityIndex = ContractDataTypes.activityTypes.indexOf(challenge.activityType)
        selectedActivityState.value = activityIndex

        distanceState.setTextAndPlaceCursorAtEnd("${challenge.distance}")

        selectedTimeLimitState.value = if (!challenge.hasValidDeadline) {
            val selectedExpirationIndex = ContractDataTypes.defaultContractExpiration.run {
                val item = firstOrNull {
                    it.second == challenge.dayLimit
                }

                indexOf(item)
            }
            selectedExpirationIndex
        } else {
            -1
        }

        if (challenge.maximumActivities != -1) {
            maximumActivityCountState.setTextAndPlaceCursorAtEnd("${challenge.maximumActivities}")
        }

        minimumActivityDistanceState.setTextAndPlaceCursorAtEnd("${challenge.minimumDistanceKm}")
        contractTitleState.setTextAndPlaceCursorAtEnd(challenge.title)
    }
}
