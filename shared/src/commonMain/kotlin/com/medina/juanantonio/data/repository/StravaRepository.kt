package com.medina.juanantonio.data.repository

import com.medina.juanantonio.data.auth.StravaOAuthProvider
import com.medina.juanantonio.data.network.StravaRemoteSource
import com.medina.juanantonio.domain.models.SettingsKeys
import com.medina.juanantonio.domain.models.network.strava.StravaActivity
import com.medina.juanantonio.domain.models.network.strava.StravaAthlete
import com.medina.juanantonio.domain.models.network.NetworkResult
import com.medina.juanantonio.domain.models.network.strava.StravaOAuthTokenResponse
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.serialization.removeValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

class StravaRepository(
    private val oAuthProvider: StravaOAuthProvider,
    private val remoteSource: StravaRemoteSource,
    private val settings: Settings
) {
    private var _currentStravaAthlete = MutableStateFlow<StravaAthlete?>(null)
    val currentStravaAthlete: StateFlow<StravaAthlete?>
        get() = _currentStravaAthlete

    lateinit var currentAccessToken: String

    init {
        CoroutineScope(Dispatchers.IO).launch {
            oAuthProvider.getCodes().collect { code ->
                requestTokenWithCode(code)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            currentStravaAthlete.collect {
                if (it == null) return@collect
                saveAthlete(it)
            }
        }
    }

    fun login() {
        oAuthProvider.authenticate()
    }

    suspend fun checkIfAuthenticated(): Boolean {
        return getLoggedInStravaAthlete() != null
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    suspend fun getLoggedInStravaAthlete(): StravaAthlete? {
        val athlete = settings.decodeValueOrNull<StravaAthlete>(SettingsKeys.STRAVA.ATHLETE)
        athlete?.run {
            _currentStravaAthlete.tryEmit(athlete) // Emit to update UI immediately
            getFreshAthleteWithToken(this, refreshToken)
        }

        return athlete
    }

    suspend fun getActivityAthletes(
        before: Long = -1L,
        after: Long = -1L,
        page: Int = 1,
        perPage: Int = 30
    ): NetworkResult<List<StravaActivity>> {
        return requestWithTokenRefresh {
            remoteSource.getAthleteActivities(
                accessToken = currentAccessToken,
                before = before,
                after = after,
                page = page,
                perPage = perPage
            )
        }
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    suspend fun logout(): NetworkResult<Unit> {
        return requestWithTokenRefresh {
            val refreshToken = currentStravaAthlete.value?.refreshToken
                ?: return@requestWithTokenRefresh NetworkResult.Error("No Active Athlete")

            val result = remoteSource.revokeToken(refreshToken)
            if (result is NetworkResult.Success) {
                settings.removeValue<StravaAthlete>(SettingsKeys.STRAVA.ATHLETE)
                _currentStravaAthlete.tryEmit(null)
            }

            result
        }
    }

    private suspend fun requestTokenWithCode(code: String) {
        val result = remoteSource.requestToken(code)

        if (result is NetworkResult.Success) {
            val data = result.data
            data.athlete?.let { athlete ->
                getFreshAthleteWithToken(athlete, data.refresh_token)
            }
        }
    }

    private suspend fun getFreshAthleteWithToken(
        currentAthlete: StravaAthlete,
        refreshToken: String
    ): NetworkResult<StravaOAuthTokenResponse> {
        val result = remoteSource.refreshToken(refreshToken)

        if (result is NetworkResult.Success) {
            currentAccessToken = result.data.access_token
            val athleteWithToken = currentAthlete.apply {
                this.refreshToken = result.data.refresh_token
            }

            _currentStravaAthlete.tryEmit(athleteWithToken)
        }

        return result
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    private fun saveAthlete(stravaAthlete: StravaAthlete) {
        settings.encodeValue<StravaAthlete>(
            key = SettingsKeys.STRAVA.ATHLETE,
            value = stravaAthlete
        )
    }

    private suspend fun <T> requestWithTokenRefresh(
        networkCall: suspend () -> NetworkResult<T>
    ): NetworkResult<T> {
        val initialResult = networkCall()
        if (initialResult is NetworkResult.Error && initialResult.code == 401) {
            val currentAthlete = _currentStravaAthlete.value
            val refreshResult = currentAthlete?.run {
                getFreshAthleteWithToken(
                    currentAthlete = this,
                    refreshToken = refreshToken
                )
            } ?: return initialResult

            if (refreshResult is NetworkResult.Success) {
                return requestWithTokenRefresh(networkCall)
            }
        }

        return initialResult
    }
}
