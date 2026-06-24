package com.medina.juanantonio.domain.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.medina.juanantonio.domain.models.entities.ContractStravaActivity

@Dao
interface ContractStravaActivityDao {

    @Insert
    suspend fun insert(item: ContractStravaActivity)

    @Query("SELECT * FROM contractstravaactivity")
    suspend fun getAll(): List<ContractStravaActivity>

    @Query("DELETE FROM contractstravaactivity")
    suspend fun deleteAll()
}
