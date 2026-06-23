package com.medina.juanantonio.data.repository

import com.medina.juanantonio.data.local.ContractLocalDataSource
import com.medina.juanantonio.data.local.ContractStravaActivityLocalDataSource
import com.medina.juanantonio.domain.models.entities.Contract
import com.medina.juanantonio.domain.models.entities.ContractStravaActivity
import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import com.medina.juanantonio.domain.models.network.strava.StravaActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.time.Clock

class ContractRepository(
    private val contractLocalDataSource: ContractLocalDataSource,
    private val activityLocalDataSource: ContractStravaActivityLocalDataSource
) {

    fun getActiveContracts(): Flow<List<ContractWithActivities>> {
        return contractLocalDataSource.getAllActiveContractWithActivities()
    }

    suspend fun addContract(
        activityType: String,
        distance: Int,
        pricePerKm: Int,
        limitInDays: Int,
        title: String,
        maximumActivities: Int,
        minimumDistanceKm: Int
    ) {
        val now = Clock.System.now()

        val contract = Contract(
            activityType = activityType,
            distance = distance,
            pricePerKm = pricePerKm,
            unixDate = now.toEpochMilliseconds(),
            deadline = if (limitInDays != -1) {
                now.plus(
                    period = DateTimePeriod(days = limitInDays),
                    timeZone = TimeZone.currentSystemDefault()
                ).toEpochMilliseconds()
            } else -1,
            title = title,
            maximumActivities = maxOf(-1, maximumActivities),
            minimumDistanceKm = maxOf(1, minimumDistanceKm)
        )

        contractLocalDataSource.addContract(contract)
    }

    suspend fun addActivityToContract(
        contractId: Int,
        stravaActivity: StravaActivity
    ) {
        activityLocalDataSource.insertActivity(contractId, stravaActivity)
    }

    suspend fun getAllSubmittedStravaActivities(): List<ContractStravaActivity> {
        return activityLocalDataSource.getAllActivities()
    }

    suspend fun deleteAllContractsAndActivities() {
        contractLocalDataSource.deleteAllContracts()
        activityLocalDataSource.deleteAllActivities()
    }
}