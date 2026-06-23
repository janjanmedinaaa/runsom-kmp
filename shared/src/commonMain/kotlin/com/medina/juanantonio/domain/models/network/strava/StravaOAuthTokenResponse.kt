package com.medina.juanantonio.domain.models.network.strava

import com.medina.juanantonio.domain.models.network.strava.StravaAthlete
import kotlinx.serialization.Serializable

@Serializable
data class StravaOAuthTokenResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_at: String,
    val athlete: StravaAthlete? = null
)