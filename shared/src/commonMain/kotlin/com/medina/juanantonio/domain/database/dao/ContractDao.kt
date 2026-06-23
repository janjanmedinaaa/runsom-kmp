package com.medina.juanantonio.domain.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.medina.juanantonio.domain.models.entities.Contract
import com.medina.juanantonio.domain.models.entities.ContractWithActivities
import kotlinx.coroutines.flow.Flow

@Dao
interface ContractDao {
    @Insert
    suspend fun insert(item: Contract)

    @Query("SELECT * FROM contracts ORDER BY id DESC")
    fun getAllAsFlow(): Flow<List<Contract>>

    @Query("SELECT * FROM contracts ORDER BY id DESC")
    suspend fun getAll(): List<Contract>

    @Query("SELECT * FROM contracts ORDER BY id DESC")
    suspend fun getAllActive(): List<Contract>

    @Transaction
    @Query("SELECT * FROM contracts ORDER BY id DESC")
    fun getAllActiveWithActivities(): Flow<List<ContractWithActivities>>

    @Query("DELETE FROM contracts")
    suspend fun deleteAll()
}