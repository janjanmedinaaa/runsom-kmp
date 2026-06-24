package com.medina.juanantonio.data.repository

import com.medina.juanantonio.data.network.RunsomChallengesRemoteSource
import com.medina.juanantonio.domain.models.network.NetworkResult
import com.medina.juanantonio.domain.models.network.RunsomChallenge

class RunsomRepository(
    private val challengesRemoteSource: RunsomChallengesRemoteSource,
    private val contractRepository: ContractRepository
) {

    suspend fun getChallenges(): NetworkResult<List<RunsomChallenge>> {
        val result = challengesRemoteSource.getChallenges()
        val contractChallengeIds =
            contractRepository.getAllContracts().mapNotNull { it.challengeId }

        return if (result is NetworkResult.Success) {
            val filteredList = result.data.filter { it.id !in contractChallengeIds }
            NetworkResult.Success(filteredList, result.code)
        } else {
            result
        }
    }
}
