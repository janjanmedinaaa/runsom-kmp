package com.medina.juanantonio.presentation.ui.home.modals.challenges

import com.medina.juanantonio.data.manager.UIEventManager
import com.medina.juanantonio.data.repository.RunsomRepository
import com.medina.juanantonio.domain.models.network.NetworkResult
import com.medina.juanantonio.domain.models.network.RunsomChallenge
import com.medina.juanantonio.presentation.utils.isInPast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChallengesDisplayViewModel(
    private val runsomRepository: RunsomRepository,
    private val uiEventManager: UIEventManager
) {

    private val _challengesList = MutableStateFlow<List<RunsomChallenge>>(emptyList())
    val challengesList: StateFlow<List<RunsomChallenge>>
        get() = _challengesList

    private val _challengesListLoadingState = MutableStateFlow(true)
    val challengesListLoadingState: StateFlow<Boolean>
        get() = _challengesListLoadingState

    private var job: Job? = null

    init {
        getChallenges()
    }

    private fun getChallenges() {
        if (job?.isActive == true) return
        job = CoroutineScope(Dispatchers.IO).launch {
            _challengesListLoadingState.value = true

            val result = runsomRepository.getChallenges()

            if (result is NetworkResult.Success) {
                _challengesList.value = result.data.filter {
                    !it.validUntil.isInPast() || it.validUntil == -1L
                }
            } else if (result is NetworkResult.Error) {
                _challengesList.value = emptyList()
                uiEventManager.showSnackBarMessage(result.message)
            }

            _challengesListLoadingState.value = false
        }
    }
}
