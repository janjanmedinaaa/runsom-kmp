package com.medina.juanantonio.data.repository

import com.medina.juanantonio.data.local.ContractLocalDataSource
import com.medina.juanantonio.data.local.ContractStravaActivityLocalDataSource
import com.medina.juanantonio.domain.models.entities.Contract
import com.medina.juanantonio.domain.models.entities.ContractStravaActivity
import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import com.medina.juanantonio.domain.models.network.strava.StravaActivity
import com.medina.juanantonio.presentation.utils.TimeUtils.getCurrentMonthYear
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.time.Clock

class ContractRepository(
    private val contractLocalDataSource: ContractLocalDataSource,
    private val activityLocalDataSource: ContractStravaActivityLocalDataSource
) {

    companion object {
        const val MAX_MONTHLY_WITHDRAWAL = 3
    }

    suspend fun getAllContracts(): List<Contract> {
        return contractLocalDataSource.getAllContracts()
    }

    fun getActiveContracts(): Flow<List<ContractWithActivities>> {
        return contractLocalDataSource.getAllActiveContractWithActivities()
    }

    suspend fun getContract(id: Int): ContractWithActivities? {
        return contractLocalDataSource.getContractWithActivities(id)
    }

    suspend fun withdrawFromContract(id: Int) {
        val currentWithdrawalMonth = getCurrentMonthYear()
        contractLocalDataSource.updateWithdrawalMonth(id, currentWithdrawalMonth)
    }

    suspend fun addContract(
        activityType: String,
        distance: Int,
        pricePerKm: Int,
        limitInDays: Int,
        title: String,
        maximumActivities: Int,
        minimumDistanceKm: Int,
        challengeId: String? = null
    ) {
        val now = Clock.System.now()

        addContract(
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
            maximumActivities = maximumActivities,
            minimumDistanceKm = minimumDistanceKm,
            challengeId = challengeId
        )
    }

    suspend fun addContract(
        activityType: String,
        distance: Int,
        pricePerKm: Int,
        unixDate: Long = Clock.System.now().toEpochMilliseconds(),
        deadline: Long,
        title: String,
        maximumActivities: Int,
        minimumDistanceKm: Int,
        challengeId: String? = null
    ) {
        val contract = Contract(
            activityType = activityType,
            distance = distance,
            pricePerKm = pricePerKm,
            unixDate = unixDate,
            deadline = deadline,
            title = title,
            maximumActivities = maxOf(-1, maximumActivities),
            minimumDistanceKm = maxOf(1, minimumDistanceKm),
            challengeId = challengeId
        )

        contractLocalDataSource.addContract(contract)
    }

    suspend fun getRemainingMonthlyWithdrawal(): Int {
        val currentWithdrawalMonth = getCurrentMonthYear()
        val count =
            contractLocalDataSource.getMonthlyWithdrawnContracts(currentWithdrawalMonth)

        return MAX_MONTHLY_WITHDRAWAL - count
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