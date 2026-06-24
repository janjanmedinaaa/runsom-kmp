package com.medina.juanantonio.data.network

import com.medina.juanantonio.BuildKonfig
import com.medina.juanantonio.domain.models.network.NetworkResult
import com.medina.juanantonio.domain.models.network.RunsomChallenge
import com.medina.juanantonio.domain.models.network.caller.safeRunsomChallengeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class RunsomChallengesRemoteSource(
    private val client: HttpClient
) {

    suspend fun getChallenges(): NetworkResult<List<RunsomChallenge>> {
        return safeRunsomChallengeApiCall(client) {
            get(BuildKonfig.RUNSOM_CHALLENGES_URL)
        }
    }
}
