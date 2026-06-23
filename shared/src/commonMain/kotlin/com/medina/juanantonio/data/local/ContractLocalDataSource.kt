package com.medina.juanantonio.data.local

import com.medina.juanantonio.domain.database.AppDatabase
import com.medina.juanantonio.domain.database.dao.ContractDao
import com.medina.juanantonio.domain.models.entities.Contract
import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import kotlinx.coroutines.flow.Flow

class ContractLocalDataSource(
    database: AppDatabase
) {
    private val dao: ContractDao = database.getContractDao()

    fun getAllActiveContractWithActivities(): Flow<List<ContractWithActivities>> =
        dao.getAllActiveWithActivities()

    suspend fun addContract(contract: Contract) {
        dao.insert(contract)
    }

    suspend fun deleteAllContracts() {
        dao.deleteAll()
    }
}