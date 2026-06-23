package com.medina.juanantonio.domain.models.network.strava

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.round

@Serializable
data class StravaActivity(
    val id: String,
    val name: String,
    val distance: Float,
    val type: String,

    @SerialName("start_date")
    val startDate: String,

    @SerialName("start_date_local")
    val startDateLocal: String,

    @SerialName("timezone")
    val timezone: String,

    @SerialName("utc_offset")
    val utcOffset: Float
) {

    val distanceInKm: String
        get() {
            val km = distance / 1000f
            val rounded = round(km * 10) / 10

            return if (rounded % 1f == 0f) {
                "${rounded.toInt()}KM"
            } else {
                "${rounded}KM"
            }
        }

    val wholeKilometer: Int
        get() = (distance / 1000f).toInt()

    var maxRedeemableKm: Int = wholeKilometer
        set(value) {
            field = if (value < 0) 0 else value
        }

}