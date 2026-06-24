package com.medina.juanantonio.data.local

import com.medina.juanantonio.domain.database.AppDatabase
import com.medina.juanantonio.domain.models.entities.ContractStravaActivity
import com.medina.juanantonio.domain.models.network.strava.StravaActivity
import com.medina.juanantonio.domain.models.entities.addToContract

class ContractStravaActivityLocalDataSource(
    database: AppDatabase
) {
    private val dao = database.getContractStravaActivityDao()

    suspend fun insertActivity(
        contractId: Int,
        stravaActivity: StravaActivity
    ) {
        val contractStravaActivity = stravaActivity.addToContract(contractId)
        dao.insert(contractStravaActivity)
    }

    suspend fun getAllActivities(): List<ContractStravaActivity> {
        return dao.getAll()
    }

    suspend fun deleteAllActivities() {
        dao.deleteAll()
    }
}
