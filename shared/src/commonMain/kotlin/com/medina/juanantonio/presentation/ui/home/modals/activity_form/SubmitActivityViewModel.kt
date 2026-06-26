package com.medina.juanantonio.presentation.ui.home.modals.activity_form

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.medina.juanantonio.data.manager.UIEventManager
import com.medina.juanantonio.data.repository.CoinsPHRepository
import com.medina.juanantonio.data.repository.ContractRepository
import com.medina.juanantonio.data.repository.StravaRepository
import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import com.medina.juanantonio.domain.models.network.CoinsPHAccount
import com.medina.juanantonio.domain.models.network.strava.StravaActivity
import com.medina.juanantonio.domain.models.network.NetworkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SubmitActivityViewModel(
    private val stravaRepository: StravaRepository,
    private val contractRepository: ContractRepository,
    private val coinsPHRepository: CoinsPHRepository,
    private val uiEventManager: UIEventManager
) {

    private val _stravaActivityList = MutableStateFlow<List<StravaActivity>>(emptyList())
    val stravaActivityList: StateFlow<List<StravaActivity>>
        get() = _stravaActivityList

    private val _activityListLoadingState = MutableStateFlow(true)
    val activityListLoadingState: StateFlow<Boolean>
        get() = _activityListLoadingState

    val selectedActivityIndex = mutableIntStateOf(-1)
    val calculatedReward = mutableIntStateOf(0)

    val isLoadingState = mutableStateOf(false)

    private var job: Job? = null

    fun submitActivity(contractId: Int, onFinish: (Boolean) -> Unit) {
        if (job?.isActive == true) return
        job = CoroutineScope(Dispatchers.IO).launch {
            isLoadingState.value = true

            val selectedActivity = stravaActivityList.value[selectedActivityIndex.value]

            val isAutoPaymentsAvailable = coinsPHRepository.checkIfAutoPaymentIsAvailable()
            val message = if (isAutoPaymentsAvailable) {
                "Congratulations on finishing ${selectedActivity.distanceInKm} ${selectedActivity.type}!"
            } else {
                "I ${selectedActivity.type} for ${selectedActivity.distanceInKm}!"
            }

            val result = coinsPHRepository.requestPayment(
                amount = calculatedReward.value,
                customSenderName = "Runsom Activity Reward",
                message = message,
                account = CoinsPHAccount.ESCROW
            )

            if (result is NetworkResult.Success) {
                contractRepository.addActivityToContract(contractId, selectedActivity)
            } else if (result is NetworkResult.Error) {
                uiEventManager.showSnackBarMessage(result.message)
            }

            val isContractFinished =
                contractRepository.getContract(contractId)?.isComplete ?: false

            isLoadingState.value = false
            onFinish(isContractFinished)
        }
    }

    fun getStravaActivities(contractWithActivities: ContractWithActivities, maxRedeemableKm: Int) {
        if (job?.isActive == true) return
        job = CoroutineScope(Dispatchers.IO).launch {
            _activityListLoadingState.value = true

            val contract = contractWithActivities.contract
            val activities = contractWithActivities.activities

            val submittedActivityIds =
                contractRepository.getAllSubmittedStravaActivities().map { it.activityId }
            val hasMaximumActivityLimit = contract.maximumActivities != -1

            val isLastActivity = (contract.maximumActivities - activities.size == 1)
            val minimumDistance =
                if (hasMaximumActivityLimit && isLastActivity) maxRedeemableKm
                else contract.minimumDistanceKm

            val result =
                stravaRepository.getActivityAthletes(after = contract.unixDate / 1000)

            when (result) {
                is NetworkResult.Success -> {
                    _stravaActivityList.value =
                        result.data.filter {
                            it.type.equals(contract.activityType, ignoreCase = true) &&
                                    it.wholeKilometer > 0 &&
                                    it.id !in submittedActivityIds &&
                                    it.wholeKilometer >= minimumDistance
                        }.take(5).map {
                            it.apply {
                                this.maxRedeemableKm =
                                    minOf(wholeKilometer, maxRedeemableKm)
                            }
                        }
                }

                is NetworkResult.Error -> {
                    _stravaActivityList.value = emptyList()
                    uiEventManager.showSnackBarMessage(result.message)
                }
            }

            _activityListLoadingState.value = false
        }
    }
}
