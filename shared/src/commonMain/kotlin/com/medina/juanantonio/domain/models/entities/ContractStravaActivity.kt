package com.medina.juanantonio.domain.models.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.medina.juanantonio.domain.models.network.strava.StravaActivity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Contract::class,
            parentColumns = ["id"],
            childColumns = ["contractId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("contractId")]
)
class ContractStravaActivity(
    @PrimaryKey val activityId: String,
    val name: String,
    val distance: Float,
    val type: String,
    val startDate: String,
    val startDateLocal: String,
    val timezone: String,
    val utcOffset: Float,
    val contractId: Int
) {

    val wholeKilometer: Int
        get() = (distance / 1000f).toInt()
}

fun StravaActivity.addToContract(contractId: Int): ContractStravaActivity {
    return ContractStravaActivity(
        id,
        name,
        distance,
        type,
        startDate,
        startDateLocal,
        timezone,
        utcOffset,
        contractId
    )
}