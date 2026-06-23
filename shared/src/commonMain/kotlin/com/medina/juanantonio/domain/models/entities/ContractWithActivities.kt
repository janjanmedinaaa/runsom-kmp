package com.medina.juanantonio.domain.models.entities

import androidx.room.Embedded
import androidx.room.Relation
import kotlin.math.roundToInt

data class ContractWithActivities(
    @Embedded
    val contract: Contract,

    @Relation(
        parentColumn = "id",
        entityColumn = "contractId"
    )
    val activities: List<ContractStravaActivity>
) {

    val isComplete: Boolean
        get() {
            val totalAmount = contract.pricePerKm * contract.distance
            val currentKmCount = activities.sumOf { it.wholeKilometer }
            val currentAmount = currentKmCount * contract.pricePerKm

            return currentAmount >= totalAmount
        }
}