package com.medina.juanantonio.domain.models.network.strava

import kotlinx.serialization.Serializable

@Serializable
data class StravaApiErrorResponse(
    val message: String? = null
)