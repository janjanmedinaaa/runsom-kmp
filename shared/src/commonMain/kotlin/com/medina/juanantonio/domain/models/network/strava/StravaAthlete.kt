package com.medina.juanantonio.domain.models.network.strava

import kotlinx.serialization.Serializable

@Serializable
class StravaAthlete(
    val id: String,
    val firstname: String,
    val lastname: String,
    val username: String,
    val country: String,
    val city: String,
    val profile: String,
) {
    var refreshToken: String = ""

    val fullName: String
        get() = "$firstname $lastname".ifBlank { username }
}