package com.medina.juanantonio.domain.models.network

import kotlinx.serialization.Serializable

@Serializable
data class RunsomChallenge(
    val id: String = "",
    val title: String,
    val description: String,
    val activityType: String,
    val distance: Int,
    val created: Long,
    val validUntil: Long = -1L, // Until when you can join the activity
    val hasDeadline: Boolean = false, // If true, contract deadline would be validUntil, else follow dayLimit
    val dayLimit: Int = -1, // If -1, no limit, ignored when hasDeadline
    val maximumActivities: Int = -1,
    val minimumDistanceKm: Int = 1
) {

    val hasValidDeadline: Boolean
        get() = hasDeadline && validUntil != -1L
}
