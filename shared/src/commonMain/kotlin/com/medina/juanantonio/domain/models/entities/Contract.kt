package com.medina.juanantonio.domain.models.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlin.time.Clock

@Entity(tableName = "contracts")
data class Contract(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val activityType: String,
    val distance: Int,
    val pricePerKm: Int,
    val unixDate: Long = Clock.System.now().toEpochMilliseconds(),
    val deadline: Long = -1,

    val title: String = "",
    val maximumActivities: Int = -1,
    val minimumDistanceKm: Int = 1
) {
    @get:Ignore
    val isExpired: Boolean
        get() {
            return if (deadline != -1L) (deadline - unixDate) < 0 else false
        }
}
