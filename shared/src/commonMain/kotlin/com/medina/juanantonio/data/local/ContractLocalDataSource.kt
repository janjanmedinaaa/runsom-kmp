package com.medina.juanantonio.data.local

import com.medina.juanantonio.domain.database.AppDatabase
import com.medina.juanantonio.domain.database.dao.ContractDao
import com.medina.juanantonio.domain.models.entities.Contract
import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import kotlinx.coroutines.flow.Flow

class ContractLocalDataSource(database: AppDatabase) {
    private val dao: ContractDao = database.getContractDao()

    suspend fun getAllContracts(): List<Contract> =
        dao.getAll()

    fun getAllActiveContractWithActivities(): Flow<List<ContractWithActivities>> =
        dao.getAllActiveWithActivities()

    suspend fun getContractWithActivities(id: Int): ContractWithActivities? =
        dao.getContractWithActivities(id)

    suspend fun getMonthlyWithdrawnContracts(withdrawalMonth: String): Int =
        dao.getMonthlyWithdrawnContracts(withdrawalMonth)

    suspend fun updateWithdrawalMonth(id: Int, withdrawalMonth: String?) =
        dao.updateWithdrawalMonth(id, withdrawalMonth)

    suspend fun addContract(contract: Contract) {
        dao.insert(contract)
    }

    suspend fun deleteAllContracts() {
        dao.deleteAll()
    }
}
